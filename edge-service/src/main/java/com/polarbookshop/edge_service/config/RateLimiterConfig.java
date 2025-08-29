package com.polarbookshop.edge_service.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver keyResolver() { // 사용할 버킷을 결정해주는 bean
        return exchange -> Mono.just("anonymous");
        // 사용자 마다 버킷이 있어야 하지만 인증 서비스가 아직 없어서 모든 사용자가 같은 버킷을 사용하도록 설정
    }


}
