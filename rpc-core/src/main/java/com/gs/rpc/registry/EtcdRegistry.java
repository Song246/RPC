package com.gs.rpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.gs.rpc.model.ServiceMetaInfo;
import com.gs.rpc.config.RegistryConfig;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Etcd注册中心实现
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-10 20:41
 **/
@Slf4j
public class EtcdRegistry implements Registry {


    /**
     * 本机注册的节点Key集合（用户维护续期）
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    /**
     * etcd，Client，可获取不同的client
     */
    private Client client;
    /**
     * 操作读写功能
     */
    private KV kvClient;

    /**
     * 根节点，区分不同服务
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";

    /**
     * 注册中心服务缓存（存在注册中心上的，用户去服务发现时先访问缓存，再访问注册中心实际存的）
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    /**
     * 正在监听的Key的集合（消费端监听，获取服务端服务下线通知，避免并发冲突）
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder()
                .endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient=client.getKVClient();
        // 开启续期功能
        heartBeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception{
        // 创建Lease和KV客户端
        Lease leaseClient = client.getLeaseClient();    // 负责租约（TTL过期）客户端
        // 创建一个30秒的租约，心跳机制每 10 秒进行检测 进行续约，
        long leaseId = leaseClient.grant(30).get().getID();

        // 设置要存储的键值对
        // 键：/rpc/服务注册节点键名（服务名：版本号）
        // 值：数据元对象，serviceMetaInfo
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        // 将键值对与租约关联，并设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key,value,putOption).get();

        // 添加节点信息到本地缓存
        localRegisterNodeKeySet.add(registerKey);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String registerKey = ETCD_ROOT_PATH+serviceMetaInfo.getServiceNodeKey();

        // 服务注销，从注册中心删除key
        kvClient.delete(ByteSequence.from(registerKey,StandardCharsets.UTF_8));
        // 节点从本地缓存移除
        localRegisterNodeKeySet.remove(registerKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
         // 优先从缓存获取服务,缓存中没有才去注册中心获取并设置到缓存中
        List<ServiceMetaInfo> cacheServiceMetaInfoList = registryServiceCache.readCache();
        if(!CollUtil.isEmpty(cacheServiceMetaInfoList)) {
            return cacheServiceMetaInfoList;
        }
        // 根据服务名称作为前缀，从Etcd获取服务下的节点列表
        // 前缀搜索，结尾一定 要加 /
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";
        try {
            // 前缀查询
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),
                    getOption)
                    .get()
                    .getKvs();

            // 解析服务信息
            List<ServiceMetaInfo> serviceMetaInfoList = keyValues.stream()
                    .map(keyValue -> {
                        String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                        // 监听key的变化
                        watch(key);

                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    })
                    .collect(Collectors.toList());

            // 本地缓存不存在，去注册中心获取服务并写入本地缓存
            registryServiceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;

        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败",e);
        }

    }

    @Override
    public void destroy() {
        System.out.println("当前节点下线");

        // 遍历本节点所有的key
        for(String key : localRegisterNodeKeySet) {
            try {
                kvClient.delete(ByteSequence.from(key,StandardCharsets.UTF_8)).get();
            } catch (Exception e) {
                throw new RuntimeException(key+"节点下线失败",e);
            }
        }

        // 释放资源
        if (kvClient!=null) {
            kvClient.close();
        }
        if (client!=null) {
            client.close();
        }
    }

    @Override
    public void heartBeat() {
        // 续期：对所有集合中的节点进行重新注册，相当于续期
        // 10 秒续约一次
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
                // 遍历所有节点key
                for(String key : localRegisterNodeKeySet) {
                    try {
                        List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8))
                                .get()
                                .getKvs();

                        // 该节点已过期，需要重启节点才能重新注册
                        if(CollUtil.isEmpty(keyValues)) {
                            continue;
                        }
                        // 该节点未过期，重新注册即为续期
                        KeyValue keyValue = keyValues.get(0);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        register(serviceMetaInfo);

                    }catch (Exception e) {
                        throw new RuntimeException(key+ "续期失败",e);
                    }
                }
            }
        });
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String serviceNodeKey) {
        // 消费端监听，如果出现了DELETE key 删除事件，则清理服务注册缓存
        // 注意：即使key在注册中心被删除后再重新设置，之前的监听依旧生效，所以我们只监听首次加入到监听集合的key，防止重复

        Watch watchClient = client.getWatchClient();
        // 之前未被监听，开启监听
        boolean newWatch = watchingKeySet.add(serviceNodeKey);

        if(newWatch) {
            watchClient.watch(ByteSequence.from(serviceNodeKey,StandardCharsets.UTF_8),response -> {
                for (WatchEvent event : response.getEvents()) {
                    switch (event.getEventType()) {
                        // Key删除时触发
                        case DELETE:
                            // 清理注册服务缓存
                            registryServiceCache.clearCache();
                            break;
                        case PUT:
                        default:
                            break;
                    }
                }
            });
        }

    }
}
