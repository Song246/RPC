package com.gs.rpc.server.tcp;

import com.gs.rpc.server.HttpServer;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;
import lombok.extern.slf4j.Slf4j;

/**
 * 发送消息粘包、半包问题解决案例server端
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-20 15:57
 **/
@Slf4j
public class VertxServerSendMessageDemo implements HttpServer {


    @Override
    public void doStart(int port) {
        // 创建Vertx实例
        Vertx vertx = Vertx.vertx();

        // 创建TCP 服务器
        NetServer server = vertx.createNetServer();

        // 发送消息的自定义匿名Handler
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
                        System.out.println("server handle,size="+size);
                        if (size == -1) {
                            // 读取消息体长度
                            size = buffer.getInt(4);    // 返回 Buffer 中位置 pos 的 int
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
                log.info("TCP server started on port " + port);
            }else {
                log.info("Fail to start TCP server:"+result.cause());
            }
        });
    }
}
