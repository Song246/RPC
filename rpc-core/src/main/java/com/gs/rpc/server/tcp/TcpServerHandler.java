package com.gs.rpc.server.tcp;

import com.gs.rpc.model.RpcRequest;
import com.gs.rpc.model.RpcResponse;
import com.gs.rpc.protocol.ProtocolMessage;
import com.gs.rpc.protocol.ProtocolMessageDecoder;
import com.gs.rpc.protocol.ProtocolMessageEncoder;
import com.gs.rpc.protocol.ProtocolMessageTypeEnum;
import com.gs.rpc.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * TCP请求处理器(接收请求，通过反射调用服务实现类,用于实现远程服务调用的hanndler，而不是发送消息)
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-17 14:49
 **/
public class TcpServerHandler implements Handler<NetSocket> {   // 通过实现Vert.x提供的Handler接口，可以定义TCP请求处理器

    @Override
    public void handle(NetSocket socket) {

        // 装饰器增强，socket.handler（）内的 参数类型Handler<Buffer>通过TcpBufferHandlerWrapper装饰增强
        TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
            System.out.println("server handle req");
            // 处理连接
            // 接收请求，解码  buff-> ProtocolMessage<RpcRequest>
            ProtocolMessage<RpcRequest> protocolMessage;
            try {
                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            } catch (IOException e) {
                throw new RuntimeException("协议消息解码错误");
            }
            RpcRequest rpcRequest = protocolMessage.getBody();

            // 处理请求
            // 构造响应结果对象
            RpcResponse rpcResponse = new RpcResponse();
            try {
                // 获取要调用的服务实现类，通过反射调用
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                // 封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }

            // 发送响应编码
            ProtocolMessage.Header header = protocolMessage.getHeader();
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
            ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);
            try {
                Buffer encode = ProtocolMessageEncoder.encode(rpcResponseProtocolMessage);
                socket.write(encode);
            } catch(IOException e) {
                throw new RuntimeException("handler 协议消息编码错误");
            }
        });
        // 处理连接
        socket.handler(bufferHandlerWrapper);

    }

//    // 不采用装饰器增强模式版本，原来的HttpServerHandler处理逻辑，存在半包粘包问题
//    @Override
//    public void handle2(NetSocket netSocket) {
//        // 处理连接
//        netSocket.handler(buffer -> {
//            // 接收请求，解码  buff-> ProtocolMessage<RpcRequest>
//            ProtocolMessage<RpcRequest> protocolMessage;
//            try {
//                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
//            } catch (IOException e) {
//                throw new RuntimeException("协议消息解码错误");
//            }
//            RpcRequest rpcRequest = protocolMessage.getBody();
//
//            // 处理请求
//            // 构造响应结果对象
//            RpcResponse rpcResponse = new RpcResponse();
//            try {
//                // 获取要调用的服务实现类，通过反射调用
//                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
//                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
//                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
//                // 封装返回结果
//                rpcResponse.setData(result);
//                rpcResponse.setDataType(method.getReturnType());
//                rpcResponse.setMessage("ok");
//            } catch (Exception e) {
//                e.printStackTrace();
//                rpcResponse.setMessage(e.getMessage());
//                rpcResponse.setException(e);
//            }
//
//            // 发送响应编码
//            ProtocolMessage.Header header = protocolMessage.getHeader();
//            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
//            ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);
//            try {
//                Buffer encode = ProtocolMessageEncoder.encode(protocolMessage);
//                netSocket.write(encode);
//            } catch(IOException e) {
//                throw new RuntimeException("handler 协议消息编码错误");
//            }
//        });
//    }

}
