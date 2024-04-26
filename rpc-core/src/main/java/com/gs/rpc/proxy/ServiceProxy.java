package com.gs.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import com.gs.rpc.RpcApplication;
import com.gs.rpc.config.RpcConfig;
import com.gs.rpc.constant.RpcConstant;
import com.gs.rpc.fault.retry.RetryStrategy;
import com.gs.rpc.fault.retry.RetryStrategyFactory;
import com.gs.rpc.fault.tolerant.TolerantStrategy;
import com.gs.rpc.fault.tolerant.TolerantStrategyFactory;
import com.gs.rpc.loadbalancer.LoadBalancer;
import com.gs.rpc.loadbalancer.LoadBalancerFactory;
import com.gs.rpc.model.RpcRequest;
import com.gs.rpc.model.RpcResponse;
import com.gs.rpc.model.ServiceMetaInfo;
import com.gs.rpc.registry.Registry;
import com.gs.rpc.registry.RegistryFactory;
import com.gs.rpc.serializer.Serializer;
import com.gs.rpc.serializer.SerializerFactory;
import com.gs.rpc.server.tcp.VertxTcpClient;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

            // 读取配置文件获取注册中心对象，从注册中心获取服务提供者请求地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());

            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            // System.out.println("serviceKey="+serviceMetaInfo.getServiceKey());
            // 服务发现，获取注册中心提供的服务，去对应url请求
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("暂无服务地址");
            }


            // 负载均衡
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
            // 将调用方法名（请求路径）作为负载均衡参数
            Map<String,Object> requestParams = new HashMap<>();
            requestParams.put("methodName",rpcRequest.getMethodName());
            ServiceMetaInfo selectServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);

            // rpc请求
            // 使用重试机制
            // TODO: 更多重试策略的实现，指数退避算法实现器
            RpcResponse rpcResponse;
            try {
                RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
                rpcResponse =retryStrategy.doRetry(()->
                        // 通过Vert 的Tcp 服务器进行请求，发送TCP 请求
                        VertxTcpClient.doRequest(rpcRequest,selectServiceMetaInfo)
                );
            } catch (Exception e) {
                // 使用容错策略
                TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
                rpcResponse = tolerantStrategy.doTolerant(null, e);
            }

            System.out.println(rpcResponse);
            return rpcResponse.getData();



//            // 下面为HTTP 协议的流程
//
//            // 将构造的的RpcReq进行发送到服务器并获取返回结果
//            //TODO: 地址被硬编码，注册中心和服务发现机制解决
//            System.out.println("client invoke url: " +selectServiceMetaInfo.getServiceAddress());
//            // 注册中心，存服务和地址kv，客户端去注册中心找到节点信息ServiceMetaInfo后，拿到服务地址去请求
//            try (HttpResponse httpResponse = HttpRequest.post(selectServiceMetaInfo.getServiceAddress())
//                    .body(bodyBytes)
//                    .execute()) {
//
//                // 获取响应的数据
//                byte[] result = httpResponse.bodyBytes();
//                // 反序列化
//                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
//                return rpcResponse.getData();
//            }


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("调用服务失败");
        }

    }
}
