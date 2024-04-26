package com.gs.rpc.fault.tolerant;

import com.gs.rpc.spi.SpiLoader;

/**
 * 容错策略工厂（工厂模式，用于加载容错策略对象）
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-22 16:24
 **/
public class TolerantStrategyFactory {

    /**
     * 类初始化通过SPI机制加载容错策略对象
     */
    static {
        SpiLoader.load(TolerantStrategy.class);
    }

    /**
     * 默认容错策略
     */
    private static final TolerantStrategy DEFAULT_TOLERANT_STRATEGY = new FailFastTolerantStrategy();

    /**
    * 获取实例对象
    * @Param: [key]
    * @return: com.gs.rpc.fault.tolerant.TolerantStrategy
    * @Date: 2024/4/22
    */

    public static TolerantStrategy getInstance(String key) {
        return SpiLoader.getInstance(TolerantStrategy.class,key);
    }
}
