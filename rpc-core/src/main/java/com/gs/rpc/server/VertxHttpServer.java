package com.gs.rpc.server;
import io.vertx.core.Vertx;

/**
 * 创建服务器监听端口
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-01 21:02
 **/
public class VertxHttpServer implements HttpServer {
    
    /** 
    * 启动服务器
    * @Param: [port]
    * @return: void
    * @Date: 2024/4/2
    */
    
    @Override
    public void doStart(int port) {
        // 创建实例对象
        Vertx vertx = Vertx.vertx();

        // 创建HTTP服务器
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();

        // 监听端口并处理请求,服务器绑定请求处理器
        server.requestHandler(new HttpServerHandler());

        // 启动HTTP服务器并监听指定端口
        server.listen(port,result->{
            if(result.succeeded()) {
                System.out.println("listen is listening on port"+port);
            }else {
                System.out.println("faided to start server:"+result.cause());
            }
        });
    }

}
