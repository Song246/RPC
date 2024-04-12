package com.gs.rpc.config;

import lombok.Data;

/**
 * RPC注册中心配置，用户配置注册中心所需信息
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-10 20:28
 **/
@Data
public class RegistryConfig {

    /**
     * 注册中心类别
     */
    private String registry = "etcd";

    /**
     * 注册中心地址
     */
    private String address = "http://localhost:2380";

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 超时时间（单位毫秒）
     */
    private long timeout = 10000L;
}
