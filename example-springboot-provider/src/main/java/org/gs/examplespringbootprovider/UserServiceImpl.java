package org.gs.examplespringbootprovider;

import com.gs.example.common.model.User;
import com.gs.example.common.service.UserService;
import com.gs.rpc.springboot.starter.RpcService;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-23 16:26
 **/
@Service
@RpcService
public class UserServiceImpl implements UserService {
    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }
}
