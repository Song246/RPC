package com.gs.example.provider;

import com.gs.example.common.service.UserService;
import com.gs.rpc.bootstrap.ProviderBootstrap;
import com.gs.rpc.model.ServiceRegisterInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 简易服务提供者示例
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-01 20:34
 **/
@Slf4j
public class ProviderExample {
//    public static void main(String[] args) {
//        // RPC 框架初始化,加载配置文件，单例模式
//        RpcApplication.init();
//
//        // 注册服务   key为接口名称 com.gs.example.common.service.UserService,消费者通过实现类获取val的实现类    val为实现类calss如class com.gs.example.provider.UserServiceImpl
//        String serviceName = UserService.class.getName();
//        LocalRegistry.register(serviceName, UserServiceImpl.class);
//
//        // 注册服务到注册中心
//        RpcConfig rpcConfig = RpcApplication.getRpcConfig();    // 获取配置文件，配置文件没有就用类的默认值(配置文件类采用单例模式，采用懒加载，配置文件类还没加载就先去加载)
//        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
//        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
//
//        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
//        serviceMetaInfo.setServiceName(serviceName);
//        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
//        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
//        log.info("serviceMetaInfo:{}",serviceMetaInfo);
//        try {
//            registry.register(serviceMetaInfo); // 服务注册，添加到zk/etcd 以及zk/etcd自身的缓存localRegistryNodeKeySet
//        } catch (Exception e) {
//            throw new RuntimeException("服务注册失败",e);
//        }
//
//
//        // 启动 HTTP 服务器
////         HttpServer server = new VertxHttpServer();
////        server.doStart(rpcConfig.getServerPort());
//
//        // 启动TCP服务器
//        VertxTcpServer server = new VertxTcpServer();
//        server.doStart(rpcConfig.getServerPort());
////
//
//
//    }

    public static void main(String[] args) {
        // 要注册的服务
        List<ServiceRegisterInfo<?>> serviceRegistryInfoList = new ArrayList<>();
        ServiceRegisterInfo serviceRegisterInfo = new ServiceRegisterInfo(UserService.class.getName(), UserServiceImpl.class);
        serviceRegistryInfoList.add(serviceRegisterInfo);

        // 服务提供者初始化
        ProviderBootstrap.init(serviceRegistryInfoList);


    }
}
