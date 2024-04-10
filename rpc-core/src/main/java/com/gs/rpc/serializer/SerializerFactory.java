package com.gs.rpc.serializer;

import com.gs.rpc.spi.SpiLoader;

import java.util.HashMap;
import java.util.Map;


/**
 * 序列化器工厂
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-08 21:42
 **/
public class SerializerFactory {

    static {
        SpiLoader.load(Serializer.class);
    }

//说明：由在工厂中硬编码HashMap存储序列化器和实现类变为上面SPI加载指定序列化器
//    /**
//     * 序列化映射，用于实现单例
//     */
//    private static final Map<String, Serializer> KEY_SERIALIZER_MAP = new HashMap<String, Serializer>(){{
//        put(SerializerKeys.JDK,new JdkSerializer());
//        put(SerializerKeys.JSON,new JsonSerializer());
//        put(SerializerKeys.HESSIAN,new HessianSerializer());
//        put(SerializerKeys.KRYO,new KryoSerializer());
//    }};

    /**
     * 默认序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    /**
    * 获取实例
    * @Param: [key]
    * @return: com.gs.rpc.serializer.Serializer
    * @Date: 2024/4/8
    */
    public static Serializer getInstance(String key) {
        return SpiLoader.getInstance(Serializer.class,key);
    }



}
