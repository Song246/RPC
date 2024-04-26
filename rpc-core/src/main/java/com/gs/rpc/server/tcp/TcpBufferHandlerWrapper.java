package com.gs.rpc.server.tcp;


import com.gs.rpc.protocol.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

/**
 * 装饰器模式（使用RecordParser对原有buffer处理能力进行增强）
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-17 19:40
 **/
public class TcpBufferHandlerWrapper implements Handler<Buffer> {

    private final RecordParser recordParser;

    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler) {
        recordParser = initRecordParser(bufferHandler);
    }

    /**
    * 请求处理器，装饰后的handle方法，能够解决半包粘包
    * @Param: [buffer]
    * @return: void
    * @Date: 2024/4/18
    */
    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }

    private RecordParser initRecordParser(Handler<Buffer> bufferHandler) {
        // 构造parser
        RecordParser parser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);

        parser.setOutput(new Handler<Buffer>() {
            // 初始化
            int size = -1;
            // 一次完整的读取（消息头+消息体）
            Buffer resultBuffer = Buffer.buffer();
            @Override
            public void handle(Buffer buffer) {
                if (size==-1) {
                    // size -1 读取消息体长度
                    size = buffer.getInt(13);   // 第13个字节起始刚好是请求长度（）
                    parser.fixedSizeMode(size);
                    resultBuffer.appendBuffer(buffer);
                }else {
                    // 写入消息体到结果中，组成一次完整的消息
                    resultBuffer.appendBuffer(buffer);
                    // 已拼接为完整的Buffer，执行处理
                    bufferHandler.handle(resultBuffer);
                    // 重置一轮
                    size = -1;
                    parser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                    // buffer 清空准备读取下一轮的消息头和消息体，再组成完整消息
                    resultBuffer = Buffer.buffer();

                }
            }
        });
        return parser;
    }
}
