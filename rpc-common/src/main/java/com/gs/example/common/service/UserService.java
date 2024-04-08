package com.gs.example.common.service;

import com.gs.example.common.model.User;

/**
* 用户服务接口
* @author tckry
* @Param:
* @return:
* @Date: 2024/4/2
*/
public interface UserService {

    /**
    * 获取用户
    * @Param: [user]
    * @return: USer
    * @Date: 2024/4/1
    */
    User getUser(User user);


    /**
    * 获取数字
    * @Param: []
    * @return: short
    * @Date: 2024/4/8
    */
    default short getNumber() {
        return 0;
    }
}
