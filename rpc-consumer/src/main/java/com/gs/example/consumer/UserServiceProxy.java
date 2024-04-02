package com.gs.example.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.gs.example.common.model.User;
import com.gs.example.common.service.UserService;
import com.gs.rpc.model.RpcRequest;
import com.gs.rpc.model.RpcResponse;
import com.gs.rpc.serializer.JdkSerializer;
import com.gs.rpc.serializer.Serializer;

/**
 * 用户请求静态代理
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-02 14:46
 **/
public class UserServiceProxy implements UserService {  // 代理用户，实现接口
    @Override
    public User getUser(User user) {
        // 指定序列化器
        Serializer serializer = new JdkSerializer();

        // 发请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();

        try{
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                         .body(bodyBytes)
                         .execute()) {
                result = httpResponse.bodyBytes();
            }
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return (User)rpcResponse.getData();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
