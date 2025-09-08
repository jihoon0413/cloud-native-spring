package com.polarbookshop.edge_service.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver keyResolver() { // 사용할 버킷을 결정해주는 bean
        return exchange -> exchange.getPrincipal()
                .map(Principal::getName) // principal를 통해 개인 별로 버킷을 따로 할당함
                .defaultIfEmpty("anonymous"); // 익명의 사람들은 공통의 anonymous 버킷을 사용해야 함

    }
}
