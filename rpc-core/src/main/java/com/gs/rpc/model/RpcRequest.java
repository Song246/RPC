package com.gs.rpc.model;

import com.gs.rpc.constant.RpcConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * RPC请求
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-01 21:37
 **/
@Data
@Builder    // 和下面两个注解一起链式构建
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {

    /**
     * 服务名称，Service接口
     */
    private String serviceName;

    /**
     * 方法名称，Service接口中的方法
     */
    private String methodName;

    private String serviceVersion = RpcConstant.DEFAULT_SERVICE_VERSION;

    /**
     * 参数类型列表
     */
    private Class<?>[] parameterTypes;

    /**
     * 参数列表
     */
    private Object[] args;

}
