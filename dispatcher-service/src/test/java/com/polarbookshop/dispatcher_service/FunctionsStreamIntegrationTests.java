package com.polarbookshop.dispatcher_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class) // 테스트 빌더 설정
public class FunctionsStreamIntegrationTests {

    @Autowired
    private InputDestination input; // 입력 바인딩 -> packlabel-in-0

    @Autowired
    private OutputDestination output; // 출력 바인딩 -> packlabel-out-0

    @Autowired
    private ObjectMapper objectMapper; // Json 메세지를 객체로 역직렬화하기 위해 사용

    @Test
    void whenOrderAcceptedThenDispatched() throws IOException {
        long orderId = 121;
        Message<OrderAcceptedMessage> inputMessage = MessageBuilder
                .withPayload(new OrderAcceptedMessage(orderId)).build();
        Message<OrderDispatchedMessage> expectedOutputMessage = MessageBuilder
                .withPayload(new OrderDispatchedMessage(orderId)).build();

        this.input.send(inputMessage); // 입역 체널로 inputMessage 보냄

        assertThat(objectMapper.readValue(output.receive().getPayload(),
                OrderDispatchedMessage.class))
                .isEqualTo(expectedOutputMessage.getPayload()); // 결과와 outMessage의 결과를 비교


    }


}
