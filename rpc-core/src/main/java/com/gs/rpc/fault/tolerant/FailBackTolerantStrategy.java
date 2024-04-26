package com.gs.rpc.fault.tolerant;

import com.gs.rpc.model.RpcResponse;

import java.util.Map;

/**
 * 降级到其他服务-容错策略
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-22 16:19
 **/
public class FailBackTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // TODO: 获取降级服务并调用
        // 参考思路：参考Dubbo的Mock能力，让消费端指定调用失败后要执行的本地服务方法
        return null;
    }
}
