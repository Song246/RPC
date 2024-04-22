package com.gs.rpc.config;

import com.gs.rpc.fault.retry.RetryStrategyKeys;
import com.gs.rpc.fault.tolerant.TolerantStrategyKeys;
import com.gs.rpc.loadbalancer.LoadBalancerKeys;
import com.gs.rpc.registry.Registry;
import com.gs.rpc.serializer.Serializer;
import com.gs.rpc.serializer.SerializerKeys;
import lombok.Data;

/**
 * RPC 框架配置
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-02 17:12
 **/
@Data
public class RpcConfig {
    /**
     * 名称
     */
    private String name = "gs-rpc";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务器主机ip
     */
    private String serverHost = "localhost";

    /**
     * 服务器主机端口
     */
    private Integer serverPort = 8080;

    /**
     * 模拟调用
     */
    private boolean mock = false;

    /**
     * 序列化器
     */
    private String serializer = SerializerKeys.JDK;

    /**
     * 注册中心
     */
    private RegistryConfig registryConfig = new RegistryConfig();

    /**
     * 负载均衡器
     */
    private String loadBalancer = LoadBalancerKeys.CONSISTENT_HASH;

    /**
     * 重试策略
     */
    private String retryStrategy = RetryStrategyKeys.NO;

    /**
     * 容错策略
     */
    private String tolerantStrategy = TolerantStrategyKeys.FAIL_FAST;

}
