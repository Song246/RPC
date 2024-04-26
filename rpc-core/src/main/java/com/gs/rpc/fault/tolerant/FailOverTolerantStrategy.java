package com.gs.rpc.fault.tolerant;

import com.gs.rpc.model.RpcResponse;

import java.util.Map;

/**
 * 转移到其他服务节点-容错策略
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-22 16:20
 **/
public class FailOverTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // TODO: 获取其他服务节点并调用
        // 参考思路：利用容错方法的上下文参数传递所有的服务节点和本次调用的服务节点，选择一个其他节点再次发起调用
        return null;
    }
}
