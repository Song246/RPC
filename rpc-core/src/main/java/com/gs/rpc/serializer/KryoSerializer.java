package com.gs.rpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Kryo序列化器
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-08 21:27
 **/
public class KryoSerializer implements Serializer{
    /**
     * Kyro线程不安全，使用ThreadLocal 保证每个线程只有一个Kyro
     * 这里ThreadLocal.withInitial是一个工厂方法，它接收一个Supplier函数式接口作为参数，并且为每个线程提供一个新的Kryo实例。
     * 因此，每次调用KRYO_THREAD_LOCAL.get()将为当前线程返回独立的Kryo实例。
     *
     * ThreadLocal已经解决了Kryo实例的线程安全问题。由于Kryo实例不是线程安全的，直接在多线程环境下共享一个Kryo实例可能会导致各种并发问题。
     * 使用ThreadLocal确保每个线程都使用自己独立的Kryo实例，从而避免了线程安全问题。
     */
    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL =ThreadLocal.withInitial(()->{ //ThreadLocal.withInitial 工厂方法，创建ThreadLocal
        // 为每个线程提供一个新的kryo实例
        Kryo kryo = new Kryo();
        // 设置动态序列化和反序列化类，不提前注册所有类（可能有安全问题）
        kryo.setRegistrationRequired(false);
        return kryo;
    });


    @Override
    public <T> byte[] serialize(T object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        // 每个线程调用都会返回一个自己线程的ThreadLocal对象，保证每次序列化的线程拥有自己独立的kryop示例，解决kryo序列化不安全问题
        KRYO_THREAD_LOCAL.get().writeObject(output,object);
        output.close();
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input input = new Input();
        // 每个线程调用都会返回一个自己线程的ThreadLocal对象，保证每次序列化的线程拥有自己独立的kryop示例，解决kryo序列化不安全问题
        T result = KRYO_THREAD_LOCAL.get().readObject(input, type);
        input.close();
        return result;
    }
}
