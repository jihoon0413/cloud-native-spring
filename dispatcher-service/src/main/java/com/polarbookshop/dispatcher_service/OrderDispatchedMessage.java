package com.polarbookshop.dispatcher_service;

public record OrderDispatchedMessage( // 포장된 주문의 식별자
        Long orderId
) {
}
