package com.gs.rpc.fault.retry;

import com.gs.rpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 不重试-重试策略
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-22 15:11
 **/
public class NoRetryStrategy implements RetryStrategy {
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        // 不重试，就是直接执行一次任务
        return callable.call();
    }
}
