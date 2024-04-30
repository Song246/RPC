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
     * 一致性哈希环，存放虚拟节点（Dubbo源码中也是采用TreeMap结构，将虚拟Invoker对象分布在环上）
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


        // 要求虚拟节点都构建到同一个机器上（虽然调用还是这个服务，但是通过添加虚拟节点解决节点少的数据倾斜问题）
        // 首先一个服务器根据需要可以有多个虚拟节点。假设一台服务器有n个虚拟节点。那么哈希计算时，可以使用IP+端口+编号的形式进行哈希值计算。其中的编号就是0到n的数字。由于IP+端口是一样的，所以这n个节点都是指向的同一台机器。

        // 构建虚拟节点环，一个服务100个虚拟节点（Dubbo默认160个虚拟节点，虚拟节点的节点hash不同，但是val都是存相同服务）
        // 注意：每次调用都会一致性哈希负载均衡器都会重新生成哈希环，为了能够及时处理节点的变化
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {   // 实现服务的不同实例对象
             for (int i=0; i<VIRTUAL_NODE_NUM;i++) {
                 // 添加虚拟节点，通过拼接数字，将一个大区间尽量多拆分为小区间，是请求尽量分散均匀，由于负载均衡都是针对同一服务，当前节点宕机去请求下一个节点服务也是一样的，目的为了分散请求
                 int hash = getHash(serviceMetaInfo.getServiceAddress()+"#"+i);
                 virtualNodes.put(hash,serviceMetaInfo);    // 虚拟节点，hahs不同，但是服务都相同
             }

        }


        // 选择过程：下面代码参考Dubbo
        // 获取调用请求的hash值
        int hash = getHash(requestParams);
        // 选择最接近且大于等于调用请求hash值的虚拟节点
        // ceilingEntry返回大于或等于给定键的最小键关联的键值映射，如果没有此类键，则为 null。
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if (entry==null) {
            // 如果没有大于等于调用请求hash值的虚拟节点，则返回TreeMap首部节点， 相当于取模实现环
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
