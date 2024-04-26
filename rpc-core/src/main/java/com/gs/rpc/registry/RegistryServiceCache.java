package com.gs.rpc.registry;

import com.gs.rpc.model.ServiceMetaInfo;

import java.util.List;

/**
* 注册中心本地服务缓存（消费端），（存在消费者本地上的，用户去调用服务时先去本地缓存查询，没有再去注册中心服务发现并加入本地）
* @Param:
* @return:
* @Date: 2024/4/12
*/
public class RegistryServiceCache {

    /**
     * 存储本地服务缓存
     */
    List<ServiceMetaInfo> serviceCache;

    /**
    * 写缓存
    * @Param: [newServiceCache]
    * @return: void
    * @Date: 2024/4/12
    */
    void writeCache(List<ServiceMetaInfo> newServiceCache) {
        this.serviceCache = newServiceCache;
    }

    /**
    * 读缓存
    * @Param: []
    * @return: java.util.List<com.gs.rpc.model.ServiceMetaInfo>
    * @Date: 2024/4/12
    */
    List<ServiceMetaInfo> readCache() {
        return serviceCache;
    }

    /**
    * 清空缓存
    * @Param: []
    * @return: void
    * @Date: 2024/4/12
    */
    void clearCache() {
        this.serviceCache = null;   // Null help GC
    }
}
