package com.gs.rpc.serializer;

import java.io.*;

/**
 * JDK序列化器
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-01 21:30
 **/
public class JdkSerializer implements Serializer{

    /**
     * 说明：序列化和反序列化代码无需记忆，需要用到时照抄即可
     */

    /**
    * 序列化
    * @Param: [object]
    * @return: byte[]
    * @Date: 2024/4/1
    */
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        return outputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        try {
            return (T) objectInputStream.readObject();
        }catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }finally {
            objectInputStream.close();
        }

    }
}
