package com.gs.rpc.serializer;

import java.io.IOException;

/**
* 抽象序列化接口
* @author tckry
* @Param:
* @return:
* @Date: 2024/4/1
*/
public interface Serializer {

    /**
    * 序列化
    * @Param: [object]
    * @return: byte[]
    * @Date: 2024/4/1
    */
    <T> byte[] serialize(T object) throws IOException;

    <T> T deserialize(byte[] bytes,Class<T> type) throws IOException;


}
