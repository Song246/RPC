package com.gs.rpc.springboot.starter.bootstrap;

import com.gs.rpc.springboot.starter.EnableRpc;
import com.gs.rpc.RpcApplication;
import com.gs.rpc.config.RpcConfig;
import com.gs.rpc.server.tcp.VertxTcpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Rpc框架启动，在Spring框架初始化时，获取@EnableRpc注解的属性，并初始化Rpc框架
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-23 15:31
 **/
@Slf4j
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {    // 实现Spring的ImportBeanDefinitionRegistrar接口，并在方法中获取到项目的注解和注解属性


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获取@Enable注解的属性
        boolean needServer = (boolean) importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName()).get("needServer");

        // Rpc 框架初始化（配置和注册中心）
        RpcApplication.init();

        // 全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        if(needServer) {
            VertxTcpServer vertxTcpServer = new VertxTcpServer();
            vertxTcpServer.doStart(rpcConfig.getServerPort());
        } else {
            log.info("不启动server");
        }


        ImportBeanDefinitionRegistrar.super.registerBeanDefinitions(importingClassMetadata, registry);
    }
}
