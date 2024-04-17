package com.gs.rpc.server.tcp;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

/**
 * VertxTcpClient demo
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-16 20:58
 **/
public class VertxTcpClient {

    public void start() {
        Vertx vertx = Vertx.vertx();
        NetClient client = vertx.createNetClient();
        client.connect(8888,"localhost",result -> {
            if (result.succeeded()) {
                System.out.println("Connected to TCP server");
                NetSocket socket = result.result();

                // 发送数据
                for (int i = 0; i < 10; i++) {
                    // 发送数据
                    Buffer buffer = Buffer.buffer();
                    String str= "hello, server!hello, server!hello, server!hello, server!";
                    buffer.appendInt(0);
                    buffer.appendInt(str.getBytes().length);
                    buffer.appendBytes(str.getBytes());
                    socket.write(buffer);
                }

                // 接收响应
                socket.handler(buffer -> {
                    System.out.println("received response from server"+buffer.toString());
                });

            }else {
                System.out.println("failed to connect to tcp server");
            }
        });
        client.close();

    }

    public static void main(String[] args) {
        new VertxTcpClient().start();
    }
}
