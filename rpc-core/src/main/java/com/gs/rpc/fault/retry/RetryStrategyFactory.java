package com.gs.rpc.fault.retry;

import com.gs.rpc.spi.SpiLoader;

/**
 * 重试策略工厂（用于获取重试器对象）
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-22 15:27
 **/
public class RetryStrategyFactory {

    /**
     * 类初始化通过SPI机制加载对应的重试策略对象
     */
    static {
        SpiLoader.load(RetryStrategy.class);
    }

    /**
     * 默认拒绝策略
     */
    private static final RetryStrategy DEFAULT_RETRY_STRATEGY = new NoRetryStrategy();

    /**
    * 获取实例
    * @Param: [key]
    * @return: com.gs.rpc.fault.retry.RetryStrategy
    * @Date: 2024/4/22
    */
    public static RetryStrategy getInstance(String key) {
        return SpiLoader.getInstance(RetryStrategy.class,key);
    }
}
