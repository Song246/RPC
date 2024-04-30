package com.gs.rpc.loadbalancer;

import com.gs.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
* 负载均衡器（消费端使用）
* @Param:
* @return:
* @Date: 2024/4/22
*/
public interface LoadBalancer {
    
    /** 
    * 
    * @Param: [requestParams, serviceMetaInfoList] 请求参数、可用服务列表（实现同一服务的实例对象）
    * @return: com.cong.rpc.core.model.ServiceMetaInfo
    * @Date: 2024/4/22
    */
    ServiceMetaInfo select(Map<String,Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);
}
