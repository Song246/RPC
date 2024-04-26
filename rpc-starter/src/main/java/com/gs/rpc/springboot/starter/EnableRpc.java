package com.gs.rpc.springboot.starter;

import com.gs.rpc.springboot.starter.bootstrap.RpcConsumerBootstrap;
import com.gs.rpc.springboot.starter.bootstrap.RpcInitBootstrap;
import com.gs.rpc.springboot.starter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用Rpc注解，用于全局标识项目需要引入RPC框架、执行初始化方法
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {

    /**
     * 是否需要启动server（由于服务消费者和服务提供者初始化模块不同，用于指定是否需要服务器）
     * @return
     */
    boolean needServer() default true;
}
