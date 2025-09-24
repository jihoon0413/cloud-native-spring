package com.polarbookshop.order_service.order.web;

import com.polarbookshop.order_service.config.SecurityConfig;
import com.polarbookshop.order_service.order.domain.Order;
import com.polarbookshop.order_service.order.domain.OrderService;
import com.polarbookshop.order_service.order.domain.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@WebFluxTest(OrderController.class)
@Import(SecurityConfig.class)
public class OrderControllerWebFluxTests {

    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    ReactiveJwtDecoder reactiveJwtDecoder; // accessToken를 해독하기 위한 객체(공개키를 굳이 가져오지 않아도 된다.)

    @Test
    void whenBookNotAvailableThenRejectOrder() {
        var orderRequest = new OrderRequest("1234567890", 3);
        var expectedOrder = OrderService.buildRejectedOrder(orderRequest.isbn(), orderRequest.quantity());

        given(orderService.submitOrder(orderRequest.isbn(), orderRequest.quantity()))
                .willReturn(Mono.just(expectedOrder));

        webClient
                .mutateWith(SecurityMockServerConfigurers
                        .mockJwt()  // customer 역할을 갖는 jwt 모의 토큰을 http 추가 요청
                        .authorities(new SimpleGrantedAuthority("ROLE_customer")))
                .post()
                .uri("/orders")
                .bodyValue(orderRequest)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Order.class).value(actualOrder -> {
                    assertThat(actualOrder).isNotNull();
                    assertThat(actualOrder.status()).isEqualTo(OrderStatus.REJECTED);
                });

    }


}
