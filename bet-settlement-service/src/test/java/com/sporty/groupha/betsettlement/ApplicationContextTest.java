package com.sporty.groupha.betsettlement;

import org.junit.jupiter.api.Test;
import org.apache.rocketmq.spring.support.RocketMQMessageListenerContainerRegistrar;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class ApplicationContextTest {

    @MockitoBean
    private RocketMQMessageListenerContainerRegistrar rocketMQMessageListenerContainerRegistrar;

    @Test
    void contextLoads() {
    }
}
