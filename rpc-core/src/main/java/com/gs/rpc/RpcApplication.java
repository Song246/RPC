package com.gs.rpc;

import com.gs.rpc.config.RegistryConfig;
import com.gs.rpc.config.RpcConfig;
import com.gs.rpc.constant.RpcConstant;
import com.gs.rpc.registry.Registry;
import com.gs.rpc.registry.RegistryFactory;
import com.gs.rpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC框架应用，相当于holder，存放项目全局用到的变量，双重检查单例
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-02 18:27
 **/
@Slf4j
public class RpcApplication {

    /**
     * 全局配置对象
     */
    private static volatile RpcConfig rpcConfig;

    /**
    * 框架初始化，传入自定义配置
    * @Param: [newRpcConfig]
    * @return: void
    * @Date: 2024/4/2
    */
    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("rpc application init, config={}",newRpcConfig);
        // 服务端注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        log.info("rpc application init registry init,config={},registry={}",registryConfig,registry);
        registry.init(registryConfig);


        // 创建并注册Shutdown Hook，JVM退出时执行操作
        // 某个服务提供者节点宕机时，应该从注册中心移除掉本机注册的服务，否则会影响消费端调用(消费者从注册中心获取一个下线服务地址进行调用)
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }

    /** 
    * 初始化配置
    * @Param: []
    * @return: void
    * @Date: 2024/4/2
    */
    public static void init() {
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        }catch (Exception e) {
            // 配置加载失败，使用默认值
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);

    }

    /**
    * 获取配置
    * @Param: []
    * @return: com.gs.rpc.config.RpcConfig
    * @Date: 2024/4/2
    */
    public static RpcConfig getRpcConfig() {
        // 双重检查锁，volatile
        if (rpcConfig==null) {
            synchronized (RpcConfig.class) {
                if (rpcConfig==null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }

}
