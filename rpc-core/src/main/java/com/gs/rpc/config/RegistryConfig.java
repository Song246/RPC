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
        private String registry = "etcd";   // 优先去配置文件中加载，配置文件没有，才来类中获取默认值
//        private String registry = "zookeeper";

    /**
     * 注册中心地址
     */
    private String address = "http://localhost:2380"; // etcd 格式，需要带http前缀，优先通过配置文件中加载，配置文件没有，才来类中获取默认值
//        private String address = "localhost:2181";    // zookeeper 格式不一样，不用带http前缀



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
