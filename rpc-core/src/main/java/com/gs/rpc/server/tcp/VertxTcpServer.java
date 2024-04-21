package com.gs.rpc.server.tcp;

import com.gs.rpc.server.HttpServer;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import lombok.extern.slf4j.Slf4j;

/**
 * VertxTcpServer 案例demo
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-16 20:46
 **/
@Slf4j
public class VertxTcpServer implements HttpServer {

    private byte[] handleRequest(byte[] requestData) {
        // 在这里编写处理请求的逻辑，根据requestData构造响应数据并返回
        // 这里只是一个示例，实例逻辑根据具体业务逻辑需求实现
        return "hello,client".getBytes();
    }

    @Override
    public void doStart(int port) {
        // 创建Vertx实例
        Vertx vertx = Vertx.vertx();

        // 创建TCP 服务器
        NetServer server = vertx.createNetServer();

        // 处理请求，进行服务调用的TcpServerHandler
        server.connectHandler(new TcpServerHandler());

        // 启动TCP 服务器并监听端口
        server.listen(port,result -> {
            if (result.succeeded()) {
                log.info("TCP server started on port " + port);
            }else {
                log.info("Fail to start TCP server:"+result.cause());
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }
}
