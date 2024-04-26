package com.gs.rpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.gs.rpc.RpcApplication;
import com.gs.rpc.model.RpcRequest;
import com.gs.rpc.model.RpcResponse;
import com.gs.rpc.model.ServiceMetaInfo;
import com.gs.rpc.protocol.*;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Vertx Tcp请求客户端，用户直接new调用
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-16 20:58
 **/
@Slf4j
public class VertxTcpClient {


    /**
    * 发送请求，RpcRequest封装成ProtocolMessage<RpcRequest>进行发送，获取响应
    * @Param: [rpcRequest, serviceMetaInfo]
    * @return: com.gs.rpc.model.RpcResponse
    * @Date: 2024/4/17
    */
    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws InterruptedException, ExecutionException {
        log.info("client send Request :rpcRequest={}",rpcRequest);
        // 发送TCP请求(消费者，发送protocolMessage<RpcRequest>，)
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        netClient.connect(serviceMetaInfo.getServicePort(),serviceMetaInfo.getServiceHost(),
                result -> {
                    if (!result.succeeded()) {
                        System.out.println("Failed to connect to TCP Server'");
                    }

                    NetSocket socket = result.result();
                    // 发送数据
                    // 构造消息
                    ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                    ProtocolMessage.Header header = new ProtocolMessage.Header();
                    header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                    header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                    header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                    header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
//                    header.setStatus((byte) 0x1);
                    // 生成全局请求ID
                    header.setRequestId(IdUtil.getSnowflakeNextId());
                    protocolMessage.setHeader(header);
                    protocolMessage.setBody(rpcRequest);

                    // 编码请求,RpcRequest -> ProtocolMessage -> Buffer
                    try {
                        Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);   // // 客户端发送远程调用Service
                        Future<Void> written = socket.write(encodeBuffer);// 客户端发送数据
                    } catch (IOException e) {
                        throw new RuntimeException("Req 协议消息编码错误");
                    }

                    // 接收响应
                    TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(
                            buffer -> {
                                try {
                                    ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                                    responseFuture.complete(rpcResponseProtocolMessage.getBody());
                                    log.info("client received response: " + rpcResponseProtocolMessage.getBody());
                                } catch (IOException e) {
                                    throw new RuntimeException("协议消息解码错误");
                                }
                            }
                    );
                    socket.handler(bufferHandlerWrapper);
                });
        RpcResponse rpcResponse = responseFuture.get(); // rpcResponse=RpcResponse(data=com.gs.example.common.model.User@44b3501e, dataType=class com.gs.example.common.model.User, message=ok, exception=null)
        System.out.println("response"+rpcResponse);
        // 记得关闭连接
        netClient.close();
        return rpcResponse;
    }

    public void start() {
        Vertx vertx = Vertx.vertx();
        NetClient client = vertx.createNetClient();
        client.connect(8888,"localhost",result -> {
            if (result.succeeded()) {   // 连接成功
                System.out.println("client Connected to TCP server");
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
        VertxTcpClient vertxTcpClient = new VertxTcpClient();
        vertxTcpClient.start();
    }
}
