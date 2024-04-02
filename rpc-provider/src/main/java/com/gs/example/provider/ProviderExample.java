package com.gs.example.provider;

import com.gs.example.common.service.UserService;
import com.gs.rpc.registry.LocalRegistry;
import com.gs.rpc.server.HttpServer;
import com.gs.rpc.server.VertxHttpServer;

/**
 * 简易服务提供者示例
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-01 20:34
 **/
public class ProviderExample {
    public static void main(String[] args) {
        // 注册服务   key为接口名称 com.gs.example.common.service.UserService    val为实现类calss如class com.gs.example.provider.UserServiceImpl
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        // 启动 服务器
        HttpServer server = new VertxHttpServer();
        server.doStart(8080);
    }
}
