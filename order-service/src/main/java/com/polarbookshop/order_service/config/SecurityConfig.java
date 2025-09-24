package com.polarbookshop.order_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain filterChain(ServerHttpSecurity http) throws Exception {
        return http
                .authorizeExchange(exchange -> exchange
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .requestCache(requestCacheSpec -> // 각 요청이 토큰을 가지고 있어야하기 때문에 세션 캐시를 유지할 필요가 있음
                        requestCacheSpec.requestCache(NoOpServerRequestCache.getInstance()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

}
