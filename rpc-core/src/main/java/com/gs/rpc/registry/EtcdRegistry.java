package com.gs.rpc.registry;

import cn.hutool.json.JSONUtil;
import com.gs.rpc.model.ServiceMetaInfo;
import com.gs.rpc.config.RegistryConfig;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Etcd注册中心实现
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-10 20:41
 **/
public class EtcdRegistry implements Registry{
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

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder()
                .endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient=client.getKVClient();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception{
        // 创建Lease和KV客户端
        Lease leaseClient = client.getLeaseClient();    // 负责租约（TTL过期）客户端
        // 创建一个30秒的租约
        long leaseId = leaseClient.grant(30).get().getID();

        // 设置要存储的键值对
        // 键：/rpc/服务注册节点键名（服务名：版本号）
        // 值：数据元对象，serviceMetaInfo
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        // 将键值对与租约关联，并设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key,value,putOption);

    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        // 服务注销，从注册中心删除key
        kvClient.delete(ByteSequence.from(ETCD_ROOT_PATH+serviceMetaInfo.getServiceNodeKey(),StandardCharsets.UTF_8));
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
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
            return keyValues.stream().map(keyValue -> {
                String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                return JSONUtil.toBean(value, ServiceMetaInfo.class);
            }).collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败",e);
        }

    }

    @Override
    public void destroy() {
        System.out.println("当前节点下线");
        // 释放资源
        if (kvClient!=null) {
            kvClient.close();
        }
        if (client!=null) {
            client.close();
        }
    }
}
