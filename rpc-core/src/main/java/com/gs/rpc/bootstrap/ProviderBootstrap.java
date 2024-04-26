package com.gs.rpc.bootstrap;

import com.gs.rpc.RpcApplication;
import com.gs.rpc.config.RegistryConfig;
import com.gs.rpc.config.RpcConfig;
import com.gs.rpc.model.ServiceMetaInfo;
import com.gs.rpc.model.ServiceRegisterInfo;
import com.gs.rpc.registry.LocalRegistry;
import com.gs.rpc.registry.Registry;
import com.gs.rpc.registry.RegistryFactory;
import com.gs.rpc.server.tcp.VertxTcpServer;

import java.util.List;

/**
 * 服务提供者启动类（初始化）
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-22 17:07
 **/
public class ProviderBootstrap {

    /**
    * 服务提供者启动类初始化
    * @Param: [serviceRegisterInfoList]
    * @return: void
    * @Date: 2024/4/26
    */

    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {

        // RPC 框架初始化(配置和注册中心),加载配置文件，单例模式
        RpcApplication.init();
        // 全局配置
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();    // 获取配置文件，配置文件没有就用类的默认值(配置文件类采用单例模式，采用懒加载，配置文件类还没加载就先去加载)


        // 注册服务   key为接口名称 com.gs.example.common.service.UserService,消费者通过实现类获取val的实现类    val为实现类calss如class com.gs.example.provider.UserServiceImpl
        for (ServiceRegisterInfo<?> serviceRegisterInfo:serviceRegisterInfoList) {
            String serviceName = serviceRegisterInfo.getServiceName();
            // 本地注册
            LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass());

            // 注册服务到注册中心

            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());

            try {
                registry.register(serviceMetaInfo); // 服务注册，添加到zk/etcd 以及zk/etcd自身的缓存localRegistryNodeKeySet
            } catch (Exception e) {
                throw new RuntimeException(serviceName+"服务注册失败",e);
            }

        }


        // 启动 HTTP 服务器
        // HttpServer server = new VertxHttpServer();
        // server.doStart(rpcConfig.getServerPort());

        // 启动TCP服务器
        VertxTcpServer server = new VertxTcpServer();
        server.doStart(rpcConfig.getServerPort());

    }


}
