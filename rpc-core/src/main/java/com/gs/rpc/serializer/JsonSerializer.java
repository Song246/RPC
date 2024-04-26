package com.gs.rpc.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gs.rpc.model.RpcRequest;
import com.gs.rpc.model.RpcResponse;

import java.io.IOException;

/**
 * Json序列化器
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-08 21:03
 **/
public class JsonSerializer implements Serializer{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public <T> byte[] serialize(T object) throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(object);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> classType) throws IOException {
        T obj = OBJECT_MAPPER.readValue(bytes, classType);
        if (obj instanceof RpcRequest) {
            return handleRequest((RpcRequest) obj,classType);
        }
        if (obj instanceof RpcResponse) {
            return handledResponse((RpcResponse) obj,classType);
        }
        return obj;
    }



    /**
    * 由于Object的原始对象会被擦除，导致反序列化时会被作为LinkedHashMap无法转换成原始对象，因此这里做了特殊处理
    * @Param: [rpcRequest, type]
    * @return: T
    * @Date: 2024/4/8
    */
    private <T> T handleRequest(RpcRequest rpcRequest,Class<T> type) throws IOException {
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        Object[] args = rpcRequest.getArgs();

        // 循环处理每个参数类型
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> clazz = parameterTypes[i];
            // 如果类型不同，则重新处理一下类型
            if(!clazz.isAssignableFrom(args[i].getClass())) {
                byte[] argBytes = OBJECT_MAPPER.writeValueAsBytes(args[i]);
                args[i] = OBJECT_MAPPER.readValue(argBytes,clazz);
            }

        }
        return type.cast(rpcRequest);
    }

    /**
     * 由于Object的原始对象会被擦除，导致反序列化时会被作为LinkedHashMap无法转换成原始对象，因此这里做了特殊处理
     * @Param: [rpcResponse, type]
     * @return: T
     * @Date: 2024/4/8
     */
    private <T> T handledResponse(RpcResponse rpcResponse, Class<T> type) throws IOException{
        // 处理响应数据
        byte[] dataBytes = OBJECT_MAPPER.writeValueAsBytes(rpcResponse.getData());
        rpcResponse.setData(OBJECT_MAPPER.readValue(dataBytes,rpcResponse.getDataType()));
        return type.cast(rpcResponse);  //  将对象强制转换为此 Class 对象表示的类或接口

    }
}
