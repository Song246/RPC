package org.gs.examplespringbootconsumer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
/**
 * 单元测试
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-23 16:31
 **/
@SpringBootTest
class ExampleServiceImplTest {

    @Resource
    private ExampleServiceImpl exampleService;

    @Test
    void test1() {
        exampleService.test();
    }
}