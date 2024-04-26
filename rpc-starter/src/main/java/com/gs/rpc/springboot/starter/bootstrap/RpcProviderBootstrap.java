package com.gs.rpc.springboot.starter.bootstrap;

import com.gs.rpc.RpcApplication;
import com.gs.rpc.springboot.starter.RpcService;
import com.gs.rpc.config.RegistryConfig;
import com.gs.rpc.config.RpcConfig;
import com.gs.rpc.model.ServiceMetaInfo;
import com.gs.rpc.registry.LocalRegistry;
import com.gs.rpc.registry.Registry;
import com.gs.rpc.registry.RegistryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Rpc服务提供者启动
 * 获取到所有包含@RpcService注解的类，并且通过注解的属性和反射机制，获取到要注册的服务信息，并且完成服务注册
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-23 15:32
 **/
@Slf4j
public class RpcProviderBootstrap implements BeanPostProcessor {
    // 如何获取到所有包含@RpcService注解的类
    // 主动扫描包，也可以利用Spring的特性监听bean的加载
    // 选择后者，实现更简单，而且能够直接获取到服务提供者类的Bean对象，只需要让启动类实现BeanPostProcessor接口的postProcessAfterInitialization方法，就可以在某个服务提供者Bean初始化后执行注册服务等操作了


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        Class<?> beanClass = bean.getClass();
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        if (rpcService != null) {
            // 需要注册服务信息
            // 1、获取服务基本信息
            Class<?> interfaceClass = rpcService.interfaceClass();
            // 默认值处理
            if (interfaceClass==void.class) {
                interfaceClass = beanClass.getInterfaces()[0];
            }
            String serviceName = interfaceClass.getName();
            String serviceVersion = rpcService.serviceVersion();
            // 2、注册服务
            // 本地注册
            LocalRegistry.register(serviceName,beanClass);

            // 全局配置
            final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            // 注册服务到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(serviceVersion);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName+"服务注册失败",e);
            }

        }

        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
