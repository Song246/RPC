package com.gs.rpc.fault.retry;

import com.gs.rpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
* 重试策略
* @Date: 2024/4/22
*/

public interface RetryStrategy {

    /**
    * 重试
    * @Param: [callable]
    * @return: com.gs.rpc.model.RpcResponse
    * @Date: 2024/4/22
    */
    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}
