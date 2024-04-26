package com.gs.rpc.fault.retry;

import com.gs.rpc.model.RpcResponse;
import org.junit.Test;

import java.util.concurrent.Callable;

/**
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-22 15:21
 **/
public class RetryStrategyTest {

    RetryStrategy retryStrategy = new FixedIntervalRetryStrategy();

    @Test
    public void doRetry() {
        try {
            RpcResponse rpcResponse = retryStrategy.doRetry(new Callable<RpcResponse>() {
                @Override
                public RpcResponse call() throws Exception {
                    System.out.println("测试重试");
                    throw new RuntimeException("模拟重试失败");
                }
            });
        } catch (Exception e) {
            System.out.println("重试多次异常");
            e.printStackTrace();
        }
    }
}
