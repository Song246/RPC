package com.gs.example.common.model;

import java.io.Serializable;

/**
 *
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-01 20:29
 **/
public class User implements Serializable {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;


}
