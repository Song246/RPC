package com.gs.rpc.fault.tolerant;

import com.gs.rpc.model.RpcResponse;

import java.util.Map;

/**
* 容错策略
* @Date: 2024/4/22
*/
public interface TolerantStrategy {

    /**
    * 容错
    * @Param: [context, e] 上下文用户传递数据；异常
    * @return: com.gs.rpc.model.RpcResponse
    * @Date: 2024/4/22
    */
    RpcResponse doTolerant(Map<String,Object> context,Exception e);
}
