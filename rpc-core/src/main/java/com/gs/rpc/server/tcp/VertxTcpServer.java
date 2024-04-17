package com.gs.rpc.server.tcp;

import com.gs.rpc.server.HttpServer;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;

/**
 * VertxTcpServer demo
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-16 20:46
 **/
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

        // 处理请求
        server.connectHandler(socket -> {

                // Vert框架，内置RecordParser完美解决半包粘包问题，它的作用：保证下次读取到特定长度的字符
                // 构造parser
                RecordParser parser = RecordParser.newFixed(8); // 为Parser指定每次读取固定值长度的内容(消息头固定)
                parser.setOutput(new Handler<Buffer>() {
                    // 初始化
                    int size = -1;
                    // 一次完整的读取（头(头中记录信息体的长度)+体）
                    Buffer resultBuffer = Buffer.buffer();


                    @Override
                    public void handle(Buffer buffer) {
                        if (size == -1) {
                            // 读取消息体长度
                            size = buffer.getInt(4);
                            parser.fixedSizeMode(size); // 信息体长度不固定
                            // 写入头信息到结果
                            resultBuffer.appendBuffer(buffer);  // 写入定长头信息
                        } else {
                            // 写入体信息到结果
                            resultBuffer.appendBuffer(buffer);  // 写入size大小的信息体body
                            System.out.println("体信息="+resultBuffer.toString());
                            // 重置一轮
                            parser.fixedSizeMode(8);
                            size = -1;
                            resultBuffer = Buffer.buffer();
                        }
                    }
                });
                socket.handler(parser);


            });

        // 启动TCP 服务器并监听端口
        server.listen(port,result -> {
            if (result.succeeded()) {
                System.out.println("TCP server started on port " + port);
            }else {
                System.out.println("Fail to start TCP server:"+result.cause());
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }
}
