package com.gs.rpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.gs.rpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * SPI 加载器（支持键值对映射），仿照SPI源码实现SPI加载机制（通过扫描加反射的方式）
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-09 19:51
 **/
@Slf4j
public class SpiLoader {

    /**
     * 存储已加载的类，key=接口 ,val=接口中不同的实现类；   内部map：jdk=class com.gs.rpc.serializer.JdkSerializer
     * {com.gs.rpc.serializer.Serializer =
     * map{jdk=class com.gs.rpc.serializer.JdkSerializer, hessian=class com.gs.rpc.serializer.HessianSerializer, json=class com.gs.rpc.serializer.JsonSerializer, kryo=class com.gs.rpc.serializer.KryoSerializer}}
     */
    private static final Map<String, Map<String,Class<?>>> loaderMap = new ConcurrentHashMap<>();


    /**
     * 对象实例，避免重复new，类路径=》对象实例，单例模式
     */
    private static Map<String,Object> instanceCache = new ConcurrentHashMap<>();

    /**
     * 系统SPI目录
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";

    /**
     * 用户自定义SPI目录
     */
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom/";

    /**
     * 扫描路径
     */
    private static final String[] SCAN_DIRS = new String[]{RPC_CUSTOM_SPI_DIR,RPC_SYSTEM_SPI_DIR};

    /**
     * 动态加载的类列表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(Serializer.class);

    /**
    * 加载所有类型,没必要加载所有，更推荐使用load加载指定的类
    * @Param: []
    * @return: void
    * @Date: 2024/4/9
    */
    public static  void loadAll() {
        log.info("加载所有SPI");
        for(Class<?> aClass : LOAD_CLASS_LIST) {
            load(aClass);
        }
    }

    /** 
    * 获取某个接口的实例
    * @Param: [tClass, key]
    * @return: T
    * @Date: 2024/4/9
    */
    public static <T> T getInstance(Class<?> tClass,String key) {
        String tClassName = tClass.getName();   // getName():获取接口名称，   com.gs.rpc.serializer.Serializer
        Map<String, Class<?>> keyClassMap = loaderMap.get(tClassName);  // 判断服务类型，Serializer、Registry、RetryStrategy等
        if (keyClassMap == null) {
            throw new RuntimeException(String.format("SpiLoader 未加载的%s类型",tClassName));
        }
        if(!keyClassMap.containsKey(key)) {
            throw new RuntimeException(String.format("SpiLoader 的%s不存在的key=%s的类型",tClassName,key));
        }
        // 获取要加载的实现类
        Class<?> implClass = keyClassMap.get(key);
        // 从实例缓存中加载指定类型的实例,不在缓存中就加入缓存
        String implClassName = implClass.getName();
        if(!instanceCache.containsKey(implClassName)) {
            try {
                instanceCache.put(implClassName,implClass.newInstance());   // 加入缓存
            }catch (InstantiationException | IllegalAccessException e) {
                String errMsg = String.format("%s类实例化失败", implClassName);
                throw new RuntimeException(errMsg,e);
            }
        }
        return  (T) instanceCache.get(implClassName);
    }

    /**
    * 加载某个类型，resources下面的读取
    * @Param: [loadClass]
    * @return: java.util.Map<java.lang.String,java.lang.Class<?>>
    * @Date: 2024/4/9
    */
    public static Map<String,Class<?>> load(Class<?> loadClass) {
        log.info("加载类型为{}的SPI",loadClass.getName());
        // 扫描路径，用户自定义SPI优先级高于系统SPI
        Map<String,Class<?>> keyClassMap = new HashMap<>();
        for (String scanDir : SCAN_DIRS) {
            List<URL> resources = ResourceUtil.getResources(scanDir + loadClass.getName());
            // 读取每个资源文件
            for (URL resource : resources) {
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] strArray = line.split("=");
                        if (strArray.length>1) {
                            String key = strArray[0];
                            String className = strArray[1];
                            keyClassMap.put(key,Class.forName(className));
                        }
                    }
                }catch (Exception e) {
                    log.error("spi load error",e);
                }
            }
        }
        loaderMap.put(loadClass.getName(),keyClassMap);
//        System.out.println("key map:"+keyClassMap);
//        System.out.println("load map:" + loaderMap);
        return keyClassMap;

    }


}
