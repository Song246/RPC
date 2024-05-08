package com.gs.rpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务本地服务注册，注意，本地服务注册器和注册中心的作用是有区别的。注册中心的作用侧重于管理注册的服务、提供服务信息给消费者；而本地服务注册器的作用是根据服务名获取到对应的实现类，是完成调用必不可少的模块。
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-01 21:20
 **/
public class LocalRegistry {
    /*
    * 注册信息存储
    */
    private static final Map<String,Class<?>> map = new ConcurrentHashMap<>();


    /** 
    * 注册服务
    * @Param: [serviceName, implClass]
    * @return: void
    * @Date: 2024/4/1
    */
    public static void register(String serviceName,Class<?> implClass) {
        map.put(serviceName,implClass);
    }

    /** 
    * 获取服务
    * @Param: [serviceName]
    * @return: java.lang.Class<?>
    * @Date: 2024/4/1
    */
    public static Class<?> get(String serviceName) {
        return map.get(serviceName);
    }

    /**
    * 删除服务
    * @Param: [serviceName]
    * @return: void
    * @Date: 2024/4/1
    */

    public static void delete(String serviceName) {
        map.remove(serviceName);
    }
    

}
