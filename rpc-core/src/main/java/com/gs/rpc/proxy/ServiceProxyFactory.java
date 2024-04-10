package com.gs.rpc.proxy;

import com.gs.rpc.RpcApplication;
import com.gs.rpc.config.RpcConfig;

import java.lang.reflect.Proxy;

/**
 * 动态代理工厂
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-02 15:12
 **/
public class ServiceProxyFactory {

    /**
    * 根据服务类接口获取代理对象（不同接口对应不同rpc服务）
    * @Param: [serviceClass]
    * @return: T
    * @Date: 2024/4/2
    */
    public static <T> T getProxy(Class<T> serviceClass) {
        // 启动mock模拟,返回Mock代理对象， 返回调用时返回mock的值
        if (RpcApplication.getRpcConfig().isMock()) {
            return getMockProxy(serviceClass);
        }
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy());
    }


    /**
    * 根据服务类获取Mocl代理对象
    * @Param: [serviceClass]
    * @return: T
    * @Date: 2024/4/8
    */
    public static <T> T getMockProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new MockServiceProxy());
    }
}
