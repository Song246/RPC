package com.gs.example.provider;
import com.gs.example.common.model.User;
import com.gs.example.common.service.UserService;

/**
 * 服务提供
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-01 20:35
 **/
public class UserServiceImpl implements UserService {

    @Override
    public User getUser(User user) {
        System.out.println("user name:" + user.getName());
        return user;
    }
}
