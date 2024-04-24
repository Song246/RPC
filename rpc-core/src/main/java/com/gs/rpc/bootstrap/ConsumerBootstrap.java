package com.gs.rpc.bootstrap;

import com.gs.rpc.RpcApplication;

/**
 * 服务端消费者启动类
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-23 14:55
 **/
public class ConsumerBootstrap {

    /** 
    * 初始化
    * @Param: []
    * @return: void
    * @Date: 2024/4/23
    */
    public static void init() {
        // RPC框架初始化（配置和注册中心），不需要注册服务也不需要启动web服务器，只需要执行RpcApplication.init完成框架初始化
        RpcApplication.init();
    }
}
