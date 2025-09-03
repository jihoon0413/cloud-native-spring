package com.polarbookshop.dispatcher_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.cloud.function.context.test.FunctionalSpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.function.Function;

@FunctionalSpringBootTest
public class DispatchingFunctionsIntegrationTests {

    @Autowired
    private FunctionCatalog catalog;

    @Test
    void packAndLabelOrder() {
        Function<OrderAcceptedMessage, Flux<OrderDispatchedMessage>>
                packAndLabel = catalog.lookup(
                        Function.class,
                "pack|label"); //FunctionCatalog로부터 합성 함수를 가져옴
        long orderId = 121;

        StepVerifier.create(packAndLabel.apply( // 함수 자체가 자체가 올바르게 동작하는지 확인
                new OrderAcceptedMessage(orderId) // 함수에 대한 입력 정의
        ))
                .expectNextMatches(dispatchedOrder ->  // 출력 결과가 일지하는지 확인
                        dispatchedOrder.equals(new OrderDispatchedMessage(orderId)))
                .verifyComplete();
    }

}
