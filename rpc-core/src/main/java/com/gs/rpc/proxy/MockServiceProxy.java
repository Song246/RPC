package com.gs.rpc.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Mock服务代理（JDK代理）
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-08 20:10
 **/
@Slf4j
public class MockServiceProxy implements InvocationHandler {

    /**
    * 调用代理
    * @Param: [proxy, method, args]
    * @return: java.lang.Object
    * @Date: 2024/4/8
    */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 根据方法的返回值类型， 生成特定的默认值对象
        Class<?> methodReturnType = method.getReturnType();
        log.info("mock invoke:{}",method.getName());
        return getDefaultObject(methodReturnType);
    }

    /**
    * 生成指定类型的默认值对象()
    * @Param: [type]
    * @return: java.lang.Object
    * @Date: 2024/4/8
    */

    private Object getDefaultObject(Class<?> type) {
        if (type.isPrimitive()) {   // 是否基础类型
            if (type == boolean.class) {
                return false;
            }else if (type == short.class) {
                return (short)0;
            } else if (type == int.class) {
                return 0;
            } else if (type == long.class) {
                return 0L;
            }
        }else { // TODO:对象类型返回默认值
            return null;
        }
        return null;
    }
}
