package com.gs.rpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.gs.rpc.model.RpcRequest;
import com.gs.rpc.model.RpcResponse;
import com.gs.rpc.serializer.JdkSerializer;
import com.gs.rpc.serializer.Serializer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 动态代理（JDK动态代理）
 * @program:
 * @description: 服务类动态代理
 * @author: lydms
 * @create: 2024-04-02 15:03
 **/
public class ServiceProxy implements InvocationHandler{

    /**
    * 动态代理，用户调用某个接口方法，会改为调用invoke方法
    * @Param: [proxy, method, args]
    * @return: java.lang.Object
    * @Date: 2024/4/2
    */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        Serializer serializer = new JdkSerializer();

        // 构造请求RpcReq,RPC的输入输出都是RPC形式
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        try{
            byte[] bodyBytes = serializer.serialize(rpcRequest);

            // 将构造的的RpcReq进行发送到服务器并获取返回结果
            //TODO: 地址被硬编码，注册中心和服务发现机制解决
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    .body(bodyBytes)
                    .execute()) {
                byte[] result = httpResponse.bodyBytes();

                // 反序列化
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
                return rpcResponse.getData();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
