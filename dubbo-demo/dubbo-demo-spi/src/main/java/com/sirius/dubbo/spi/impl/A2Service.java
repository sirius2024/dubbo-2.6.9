package com.sirius.dubbo.spi.impl;

import com.sirius.dubbo.spi.AService;

public class A2Service implements AService {

    @Override
    public void sayHello() {
        System.out.println("我是a2实现方式");
    }
}
