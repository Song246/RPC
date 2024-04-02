package com.gs.rpc;

import com.gs.rpc.config.RpcConfig;
import com.gs.rpc.constant.RpcConstant;
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

    private static volatile RpcConfig rpcConfig;

    /**
    * 框架初始化，传入自定义配置
    * @Param: [newRpcConfig]
    * @return: void
    * @Date: 2024/4/2
    */
    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("rpc init, config={}",newRpcConfig.toString());
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
