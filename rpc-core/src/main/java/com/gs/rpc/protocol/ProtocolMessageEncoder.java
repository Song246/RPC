package com.gs.rpc.protocol;

import com.gs.rpc.protocol.ProtocolMessage;
import com.gs.rpc.protocol.ProtocolMessageSerializerEnum;
import com.gs.rpc.serializer.Serializer;
import com.gs.rpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * 协议消息编码器（ProtocolMessage<T> --> Buffer对象，通过vertx传输）
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-16 21:08
 **/
public class ProtocolMessageEncoder {

    public static Buffer encode(ProtocolMessage<?> protocolMessage) throws IOException {
        if (protocolMessage==null||protocolMessage.getHeader()==null) {
            return Buffer.buffer();
        }
        ProtocolMessage.Header header = protocolMessage.getHeader();
        // 依次向缓存区写入数据
        Buffer buffer = Buffer.buffer();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());
        // 获取序列化器
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (serializerEnum == null) {
            throw new RuntimeException("序列化器协议不存在");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        byte[] bytes = serializer.serialize(protocolMessage.getBody());
        // 写入body长度和数据，（记录长度，避免粘包半包）
        buffer.appendInt(bytes.length);
        buffer.appendBytes(bytes);
        return buffer;
    }
}
