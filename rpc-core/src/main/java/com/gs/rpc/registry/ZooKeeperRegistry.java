package com.gs.rpc.registry;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.gs.rpc.config.RegistryConfig;
import com.gs.rpc.model.ServiceMetaInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * zookeeper注册中心，包含自身缓存对象
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-12 21:58
 *  * 操作文档：<a href="https://curator.apache.org/docs/getting-started">Apache Curator</a>
 *  * 代码示例：<a href="https://github.com/apache/curator/blob/master/curator-examples/src/main/java/discovery/DiscoveryExample.java">DiscoveryExample.java</a>
 *  * 监听 key 示例：<a href="https://github.com/apache/curator/blob/master/curator-examples/src/main/java/cache/CuratorCacheExample.java">CuratorCacheExample.java</a>
 **/
@Slf4j
public class ZooKeeperRegistry implements Registry{

    /**
     * 操作zookeeper客户端
     */
    private CuratorFramework client;

    /**
     * 服务发现
     */
    private ServiceDiscovery<ServiceMetaInfo> serviceDiscovery;

    /**
     * 服务提供者注册的节点key（服务）集合（服务提供者在本地维护已注册节点集合，在注册中心中进行续期）,/rpc/zk/service:1.0/2374:8081 格式
     * 服务提供者需要找到自己注册的节点（注册中心中存储）、续期自己的节点（）
     */
    private final Set<String> localRegistryNodeKeySet = new HashSet<>();

    /**
     * 本地注册中心服务缓存（存在消费者本地缓存，用户去服务发现时先访问本地缓存，再去访问注册中心）
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    /**
     * 正在监听的key集合（消费端监听注册中心的事件，进行本地缓存更新）
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    /**
     * 根节点
     */
    private static final String ZK_ROOT_PATH = "/rpc/zk";


    @Override
    public void init(RegistryConfig registryConfig) {
        // 构建client实例
        client = CuratorFrameworkFactory
                .builder()
                .connectString(registryConfig.getAddress())
                .retryPolicy(new ExponentialBackoffRetry(Math.toIntExact(registryConfig.getTimeout()),3))
                .build();
        // 构建serviceDiscovery实例
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMetaInfo.class)
                .client(client)
                .basePath(ZK_ROOT_PATH)
                .serializer(new JsonInstanceSerializer<>(ServiceMetaInfo.class))
                .build();

        try {
            // 启动client和serviceDiscovery
            client.start();
            serviceDiscovery.start();
        } catch (Exception e) {
            throw new RuntimeException("初始化失败：",e);
        }

    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        // 注册到zk里面
        serviceDiscovery.registerService(buildServiceInstance(serviceMetaInfo));

        // 添加节点信息到本地缓存
        String registryKey = ZK_ROOT_PATH+"/"+ serviceMetaInfo.getServiceNodeKey();
        localRegistryNodeKeySet.add(registryKey);
        System.out.println("local registry:"+localRegistryNodeKeySet);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        try {
            // 服务下线
            serviceDiscovery.unregisterService(buildServiceInstance(serviceMetaInfo));
        } catch (Exception e) {
            throw new RuntimeException("服务下线失败：",e);
        }

        // 从本地缓存移除
        String registryKey = ZK_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        localRegistryNodeKeySet.remove(registryKey);

    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        //优先从缓存获取服务
        List<ServiceMetaInfo> cachedServiceMetaInfoList = registryServiceCache.readCache();
        if(cachedServiceMetaInfoList!=null) {
            return cachedServiceMetaInfoList;
        }
        // 缓存中没有，去注册中心获取并加入本地缓存
        try {
            // 查询服务信息
            Collection<ServiceInstance<ServiceMetaInfo>> serviceInstanceList = serviceDiscovery.queryForInstances(serviceKey);
            // 解析服务信息  ServiceInstance=》List<ServiceMetaInfo>
            List<ServiceMetaInfo> serviceMetaInfoList = serviceInstanceList.stream()
                    .map(ServiceInstance::getPayload)
                    .collect(Collectors.toList());

            // 写入服务缓存
            registryServiceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败：",e);
        }

    }

    @Override
    public void destroy() {
        log.info("当前注册中心节点下线,localRegistryNodeKeySet={}",localRegistryNodeKeySet);
        // 下线节点（这一步可以不做，因为都是临时节点，服务下线，自然就被删掉了）
        for (String key : localRegistryNodeKeySet) {
            try {
                client.delete().guaranteed().forPath(key);
            }catch (Exception e) {
                throw new RuntimeException(key + "节点下线失败：",e);
            }
        }

        if(client!=null) {
            client.close();
        }
    }

    @Override
    public void heartBeat() {
        // 不需要心跳机制，建立了临时节点，如果服务器故障，则临时节点直接丢失
    }

    @Override
    public void watch(String serviceNodeKey) {
        String watchKey = ZK_ROOT_PATH + "/"+  serviceNodeKey;
        boolean newWatch = watchingKeySet.add(watchKey);    // 若已添加过再添加返回false，首次添加返回true

        // 首次添加，进行绑定监听的
        if (newWatch) {
            CuratorCache curatorCache = CuratorCache.build(client, watchKey);
            curatorCache.start();
            curatorCache.listenable().addListener(CuratorCacheListener
                    .builder()
                    .forDeletes(childData -> registryServiceCache.clearCache())
                    .forChanges(((oldNode,node)-> registryServiceCache.clearCache()))
                    .build());
        }


    }

    private ServiceInstance<ServiceMetaInfo> buildServiceInstance(ServiceMetaInfo serviceMetaInfo) {
        String serviceAddress = serviceMetaInfo.getServiceHost() + ":" + serviceMetaInfo.getServicePort();
        try {
            return ServiceInstance.<ServiceMetaInfo>builder()
                    .id(serviceAddress)
                    .name(serviceMetaInfo.getServiceKey())
                    .address(serviceAddress)
                    .payload(serviceMetaInfo)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
