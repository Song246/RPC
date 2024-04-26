package com.gs.rpc.loadbalancer;

import com.gs.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 随机负载均衡器
 * @program: cong-rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-22 13:23
 **/
public class RandomLoadBalancer implements LoadBalancer{

    private final Random random = new Random();

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList==null||serviceMetaInfoList.isEmpty()) return null;
        int size = serviceMetaInfoList.size();
        // 只有一个服务，不用随机
        if (size==1) {
            return serviceMetaInfoList.get(0);
        }
        return serviceMetaInfoList.get(random.nextInt(size));
    }
}
