package com.gs.rpc.fault.tolerant;

import com.gs.rpc.model.RpcResponse;

import java.util.Map;

/**
 * 快速失败-容错策略（立刻通知外层调用方）
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-22 16:14
 **/
public class FailFastTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // 快速失败容错策略，遇到异常再次抛出错误，交给外层处理
        throw new RuntimeException("服务报错",e);
    }
}
