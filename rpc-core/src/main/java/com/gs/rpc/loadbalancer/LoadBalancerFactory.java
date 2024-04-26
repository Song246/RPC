package com.gs.rpc.loadbalancer;

import com.gs.rpc.spi.SpiLoader;

/**
 * 负载均衡器工厂（工厂模式，用于获取负载均衡对象）
 * @program: cong-rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-22 14:01
 **/
public class LoadBalancerFactory {

    /**
    * 类初始化通过SPI机制加载负载均衡器对象
    * @Param:
    * @return:
    * @Date: 2024/4/22
    */
    static {
        SpiLoader.load(LoadBalancer.class);
    }

    /**
     * 默认负载均衡器
     */
    private static final LoadBalancer DEFAULT_LOAD_BALANCER = new RoundRobinLoadBalancer();

    /** 
    * 获取实例
    * @Param: [key]
    * @return: com.cong.rpc.core.loadbalancer.LoadBalancer
    * @Date: 2024/4/22
    */
    public static final LoadBalancer getInstance(String key) {
        return SpiLoader.getInstance(LoadBalancer.class,key);
    }
}
