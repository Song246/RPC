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
    * 服务注册（服务端）
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
    * 服务发现（获取某服务的所有节点，消费端）
    * @Param: [serviceKey] 服务键名
    * @return: java.util.List<com.gs.rpc.model.ServiceMetaInfo>
    * @Date: 2024/4/10
    */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    /** 
    * 服务销毁
    * @Param: []
    * @return: void
    * @Date: 2024/4/10
    */
    void destroy();
}
