package com.gs.rpc.server;

import com.gs.rpc.registry.LocalRegistry;
import com.gs.rpc.serializer.SerializerFactory;
import com.gs.rpc.RpcApplication;
import com.gs.rpc.model.RpcRequest;
import com.gs.rpc.model.RpcResponse;
import com.gs.rpc.serializer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * 自定义请求处理器，绑定到服务器，HTTP处理接收到的请求
 * @program: rpc
 * @description: HTTP请求处理
 * @author: lydms
 * @create: 2024-04-01 21:43
 **/
@Slf4j
public class HttpServerHandler implements Handler<HttpServerRequest> {
    /**
     * 业务流程：
     * 1、反序列化请求为对象，并从请求对象中获取参数。
     * 2、通过反射机制调用方法，得到返回结果。
     * 3、根据服务名称从本地注册器中获取到对应的服务实现类。
     * 4、对返回结果进行封装和序列化，并写入到响应中。
     */

    /**
    * 处理请求
    * @Param: [request]
    * @return: void
    * @Date: 2024/4/2
    */

    @Override
    public void handle(HttpServerRequest request) {
        // 指定序列化器
        // final Serializer serializer = new JdkSerializer();
        // TODO：服务器配置的序列化器要和生产者的一致，RpcConfig(服务端读取序列化器类型),application.properties（客户端读取序列化器类型）、服务器三者的序列胡器要一致；后续调整服务器不用配置,客户端配置，服务器自适应
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        log.info("Server Handler Serializer"+serializer.getClass().getName());
        // 记录日志
        System.out.println("Received request: " + request.method() + " " + request.uri());

        // 异步处理HTTP请求
        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes,RpcRequest.class);    // 将二进制反序列化为RPC请求对象
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 构造RPC响应结果对象
            RpcResponse rpcResponse = new RpcResponse();
            if (rpcRequest==null) {
                rpcResponse.setMessage("request is null");
                doResponse(request,rpcResponse,serializer);
                return;
            }

            try {
                // 获取想要调用的服务实现类，通过反射调用
                System.out.println("HttpServerHandler service name="+ rpcRequest.getMethodName());
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                // 封装返回结果RPC格式
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                // 封装返回失败的结果
                log.error("Invoke HttpServerHandler handle error", e);
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            doResponse(request,rpcResponse,serializer);
        });


    }

    /**
    * 响应
    * @Param: [request, rpcResponse, serializer]
    * @return: void
    * @Date: 2024/4/2
    */
    void doResponse(HttpServerRequest request, RpcResponse rpcResponse,Serializer serializer) {
        HttpServerResponse httpServerResponse = request.response().putHeader("content-type", "application/json");
        try {
            byte[] serialized = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (Exception e) {
            log.error("HttpServerHandler Serialize error", e);
            httpServerResponse.end(Buffer.buffer());
        }
        
    }

}
