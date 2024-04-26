package com.gs.rpc.fault.retry;

import com.github.rholder.retry.*;
import com.gs.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 固定时间间隔-重试策略
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-22 15:13
 **/
@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy{
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)    // 重试条件
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS))   // 重试策略：固定时间间隔策略
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))   // 重试停止策略：超过最大重试次数停止
                .withRetryListener(new RetryListener() {    // 重试监听工作：每次重试时，除了重新执行任务，还打印重试次数
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("重试次数{}", attempt.getAttemptNumber());
                    }
                }).build();
        return retryer.call(callable);
    }
}
