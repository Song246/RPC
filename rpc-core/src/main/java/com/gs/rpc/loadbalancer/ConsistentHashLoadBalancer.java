package com.gs.rpc.loadbalancer;

import com.gs.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性哈希环负载均衡器
 * @program: cong-rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-22 13:26
 **/
public class ConsistentHashLoadBalancer implements LoadBalancer{

    /**
     * 一致性哈希环，存放虚拟节点
     */
    private final TreeMap<Integer,ServiceMetaInfo> virtualNodes = new TreeMap<>();

    /**
     * 虚拟节点数
     */
    private static final int VIRTUAL_NODE_NUM = 100;

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList==null || serviceMetaInfoList.isEmpty()) {
            return null;
        }

        // 构建虚拟节点环
        // 注意：每次调用都会一致性哈希负载均衡器都会重新生成哈希环，为了能够及时处理节点的变化
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
             for (int i=0; i<VIRTUAL_NODE_NUM;i++) {
                 int hash = getHash(serviceMetaInfo.getServiceAddress()+"#"+i);
                 virtualNodes.put(hash,serviceMetaInfo);
             }

        }

        // 获取调用请求的hash值
        int hash = getHash(requestParams);
        // 选择最接近且大于等于调用请求hash值的虚拟节点
        // ceilingEntry返回大于或等于给定键的最小键关联的键值映射，如果没有此类键，则为 null。
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if (entry==null) {
            // 如果没有大于等于调用请求hash值的虚拟节点，则返回环首部的节点
            entry = virtualNodes.firstEntry();
        }
        return entry.getValue();


    }

    /** 
    * Hash 算法
    * @Param: [key]
    * @return: int
    * @Date: 2024/4/22
    */
    private int getHash(Object key) {
        // TODO: 哈希算法优化
        return key.hashCode();
    }
}
