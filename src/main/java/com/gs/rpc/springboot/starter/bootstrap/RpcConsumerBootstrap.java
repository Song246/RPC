package com.gs.rpc.springboot.starter.bootstrap;

import com.gs.rpc.springboot.starter.RpcReference;
import com.gs.rpc.proxy.ServiceProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * Rpc服务消费者启动
 * 类似服务提供者启动，在Bean初始化后，通过反射获取到Bean的所有属性，如果属性包含@RpcReference注解，那么就为该属性生成动态代理对象并赋值
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-23 15:32
 **/
public class RpcConsumerBootstrap implements BeanPostProcessor {

    /** 
    * Bean 初始化后执行，注入服务
    * @Param: [bean, beanName]
    * @return: java.lang.Object
    * @Date: 2024/4/23
    */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        // 遍历对象的所有属性
        Field[] declaredFields = beanClass.getDeclaredFields(); // 返回所有声明的字段，包含私有、保护
        for (Field field : declaredFields) {
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);  // 获取加了RpcReference注解的属性，没加的不做处理
            if (rpcReference != null) {
                // 为属性生成代理对象
                Class<?> interfaceClass = rpcReference.interfaceClass();
                if (interfaceClass== void.class) {
                    interfaceClass = field.getType();
                }
                field.setAccessible(true);
                Object proxyObject = ServiceProxyFactory.getProxy(interfaceClass);
                try {
                    field.set(bean,proxyObject);
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("为字段注入代理对象失败");
                }
            }
        }

        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
