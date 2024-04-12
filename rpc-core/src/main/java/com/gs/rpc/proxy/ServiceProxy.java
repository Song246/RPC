package com.gs.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.gs.rpc.RpcApplication;
import com.gs.rpc.config.RpcConfig;
import com.gs.rpc.constant.RpcConstant;
import com.gs.rpc.model.RpcRequest;
import com.gs.rpc.model.RpcResponse;
import com.gs.rpc.model.ServiceMetaInfo;
import com.gs.rpc.registry.Registry;
import com.gs.rpc.registry.RegistryFactory;
import com.gs.rpc.serializer.JdkSerializer;
import com.gs.rpc.serializer.Serializer;
import com.gs.rpc.serializer.SerializerFactory;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 动态服务代理（JDK动态代理）
 * @program:
 * @description: 服务类动态代理
 * @author: lydms
 * @create: 2024-04-02 15:03
 **/
@Slf4j
public class ServiceProxy implements InvocationHandler{

    /**
    * 动态代理，用户调用某个接口方法，会改为调用invoke方法
    * @Param: [proxy, method, args]
    * @return: java.lang.Object
    * @Date: 2024/4/2
    */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        // Serializer serializer = new JdkSerializer();  // 硬编码指定序列化器

        // 采用动态代理加载配置类application.properties中的的序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        log.info("客户端代理序列化器{}"+serializer.getClass());


        // 构造请求RpcReq,RPC的输入输出都是RPC形式
        String serviceName = method.getDeclaringClass().getName();  // com.gs.example.common.service.UserService
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        try{
            // 序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);

            // 从注册中心获取服务提供者请求地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());

            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            // System.out.println("serviceKey="+serviceMetaInfo.getServiceKey());
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("暂无服务地址");
            }


            //  TODO:从注册中心获取到的服务节点地址可能多个，暂时先取第一个
            ServiceMetaInfo selectServiceMetaInfo = serviceMetaInfoList.get(0);

            // 发送请求
            // 将构造的的RpcReq进行发送到服务器并获取返回结果
            //TODO: 地址被硬编码，注册中心和服务发现机制解决

            // 注册中心，存服务和地址kv，客户端去注册中心找到节点信息ServiceMetaInfo后，拿到服务地址去请求
            try (HttpResponse httpResponse = HttpRequest.post(selectServiceMetaInfo.getServiceAddress())
                    .body(bodyBytes)
                    .execute()) {

                // 获取响应的数据
                byte[] result = httpResponse.bodyBytes();
                // 反序列化
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
                return rpcResponse.getData();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
