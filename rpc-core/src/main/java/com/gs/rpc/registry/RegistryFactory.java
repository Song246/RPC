package com.gs.rpc.registry;

import com.gs.rpc.spi.SpiLoader;

/**
 * 注册中心工厂（用户获取注册中心对象，工厂模式）
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-11 20:04
 **/
public class RegistryFactory {
    static {
        // 类初始化时通过SPI机制加载注册中心对象
        SpiLoader.load(Registry.class);
    }

    /**
     * 默认注册中心
     */
    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    /** 
    * 获取实例
    * @Param: [key]
    * @return: com.gs.rpc.registry.Registry
    * @Date: 2024/4/11
    */
    public static Registry getInstance(String key) {
        return SpiLoader.getInstance(Registry.class,key);
    }


}
