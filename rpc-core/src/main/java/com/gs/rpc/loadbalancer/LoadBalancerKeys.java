package com.gs.rpc.loadbalancer;

/**
* 负载均衡器键名常量
* @Date: 2024/4/22
*/

public interface LoadBalancerKeys {

    /**
     * 轮询
     */
    String ROUND_ROBIN = "roundRobin";

    /**
     * 随机
     */
    String RANDOM = "random";

    /**
     * 一致性哈希环
     */
    String CONSISTENT_HASH = "consistentHash";
}
