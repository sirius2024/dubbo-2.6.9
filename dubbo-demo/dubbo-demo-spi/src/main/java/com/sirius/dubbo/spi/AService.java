package com.sirius.dubbo.spi;

import com.alibaba.dubbo.common.extension.SPI;

@SPI
//@SPI("a1")
public interface AService {

    void sayHello();
}
