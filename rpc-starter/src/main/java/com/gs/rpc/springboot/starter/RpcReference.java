package com.gs.rpc.springboot.starter;

import com.gs.rpc.constant.RpcConstant;
import com.gs.rpc.fault.retry.RetryStrategyKeys;
import com.gs.rpc.fault.tolerant.TolerantStrategyKeys;
import com.gs.rpc.loadbalancer.LoadBalancerKeys;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 服务消费者注解（用于注入服务）
 * 服务消费者注解。在需要注入服务代理对象的属性上使用，类似Spring中的@Resource注解。需要指定调用服务相关的属性，比如服务接口类、版本号等
 *
 */
@Target({ElementType.FIELD})    // 加在字段上的注解
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {

    /**
     * 服务接口类
     * @return
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 服务版本
     * @return
     */
    String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;

    /**
     * 负载均衡器
     * @return
     */
    String loadBalancer() default LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 重试策略
     * @return
     */
    String retryStrategy() default RetryStrategyKeys.NO;

    /**
     * 容错机制
     * @return
     */
    String tolerantStrategy() default TolerantStrategyKeys.FAIL_FAST;

    /**
     * 模拟调用
     * @return
     */
    boolean mock() default false;


}
