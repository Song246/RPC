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
                String testMessage = "hello, server!hello, server!hello, server!hello, server!";
                int messageLength = testMessage.getBytes().length;

                // Vert框架，内置RecordParser完美解决半包粘包问题，它的作用：保证下次读取到特定长度的字符
                // 构造parser
                RecordParser parser = RecordParser.newFixed(messageLength); // 为Parser指定每次读取固定值长度的内容
                parser.setOutput(new Handler<Buffer>() {
                    @Override
                    public void handle(Buffer buffer) {
                        String str = new String(buffer.getBytes());
                        System.out.println(str);
                        if (testMessage.equals(str)) {
                            System.out.println("good");
                        }

                    }
                });
                socket.handler(parser);


// 不使用 RecodeParser 半包、粘包演示
//                if(buffer.getBytes().length<messageLength) {
//                    System.out.println("半包，length="+buffer.getBytes().length);
//                    return;
//                }
//
//                if(buffer.getBytes().length>messageLength) {
//                    System.out.println("粘包，length="+buffer.getBytes().length);
//                    return;
//                }
//                String str = new String(buffer.getBytes(0,messageLength));
//                System.out.println(str);
//                if (testMessage.equals(str)) {
//                    System.out.println("");
//                }


//                // 处理收到的字节数组
//                byte[] requestData = buffer.getBytes();
//                // 在这里进行自定义的字节数组处理逻辑，比如解析请求、调用服务、构造响应等
//                byte[] responseData = handleRequest(requestData);
//
//                // 发送响应
//                socket.write(Buffer.buffer(responseData));  // 向连接到服务器的客户端发送数据，发送格式为BUffer，这是Vertx服务器为我们提供的字节数组缓冲区实现
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
