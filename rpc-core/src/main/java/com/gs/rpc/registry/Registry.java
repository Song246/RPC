package com.gs.rpc.registry;

import com.gs.rpc.model.ServiceMetaInfo;
import com.gs.rpc.config.RegistryConfig;

import java.util.List;


/**
* 注册中心接口
* @Param:
* @return:
* @Date: 2024/4/10
*/
public interface Registry {


    /**
    * 初始化
    * @Param: [registryConfig]
    * @return: void
    * @Date: 2024/4/10
    */
    void init(RegistryConfig registryConfig);

    /**
    * 服务注册（服务端，在注册中心以及服务提供者本地已注册节点集合中localRegistryNodeKeySet同时添加）
    * @Param: [serviceMetaInfo]
    * @return: void
    * @Date: 2024/4/10
    */
    void register(ServiceMetaInfo serviceMetaInfo)throws Exception;

    /**
    * 服务注销（服务端）
    * @Param: [serviceMetaInfo]
    * @return: void
    * @Date: 2024/4/10
    */
    void unRegister(ServiceMetaInfo serviceMetaInfo);


    /**
    * 服务发现（获取某服务的所有节点，消费端代理进行服务发现，按照负载均衡选择服务对象，根据服务键名，com.gs.example.common.service.UserService：1.0）
    * @Param: [serviceKey] 服务键名
    * @return: java.util.List<com.gs.rpc.model.ServiceMetaInfo>
    * @Date: 2024/4/10
    */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    /** 
    * 注册中心服务销毁
    * @Param: []
    * @return: void
    * @Date: 2024/4/10
    */
    void destroy();

    /**
    * 心跳机制（服务端，注册中心初始化后开启心跳机制，进行续期，ETCD需要，zk不需要）
    * @Param: []
    * @return: void
    * @Date: 2024/4/12
    */
    void heartBeat();

    /**
    * 监听（消费端，消费端进行服务发现时，对获取的服务节点进行监听，消费端监听注册中心，保持消费者本地缓存和注册中心数据一致性）
    * @Param: []
    * @return: void
    * @Date: 2024/4/12
    */
    void watch(String serviceNodeKey);

}
