package com.polarbookshop.dispatcher_service;

public record OrderAcceptedMessage(  // 접수된 주문의 식별자
        Long orderId
) {
}
