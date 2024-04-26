package org.gs.examplespringbootconsumer;

import com.gs.example.common.model.User;
import com.gs.example.common.service.UserService;
import com.gs.rpc.springboot.starter.RpcReference;
import org.springframework.stereotype.Service;

/**
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-23 16:29
 **/
@Service
public class ExampleServiceImpl {

    @RpcReference
    private UserService userService;

    public void test() {
        User user = new User();
        user.setName("John");
        userService.getUser(user);
        System.out.println("test ="+user);
    }
}
