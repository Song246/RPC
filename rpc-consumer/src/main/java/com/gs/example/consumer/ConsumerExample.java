package com.gs.example.consumer;

import com.gs.example.common.model.User;
import com.gs.example.common.service.UserService;
import com.gs.rpc.config.RpcConfig;
import com.gs.rpc.proxy.ServiceProxyFactory;
import com.gs.rpc.utils.ConfigUtils;

/**
 * 消费者，通过代理将请求转发到服务器
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-01 20:54
 **/
public class ConsumerExample {

    public static void main(String[] args) {

        // 加载配置类，服务器已经先启动进行初始化了
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);

        // TODO: 需要获取UserService 的实现类对象，通过RPC框架获取到一个支持远程调用服务提供者的的代理对象

        // 获取代理类对象，负责转发请求, 静态代理太麻烦，每个服务接口都要写一个实现类
        // UserService userService = new UserServiceProxy();

        //动态代理方式调用
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);


        User user = new User();
        user.setName("zhang san");
        // 通过代理对象调用远程服务
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }

        long number = userService.getNumber();  // 代理返回的是1，userService内的默认方法返回的是0，最终打印1，说明代理和mock数据成功
        System.out.println(" 代理 mock 数据："+number);


    }
}
