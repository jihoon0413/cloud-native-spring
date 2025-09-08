package com.polarbookshop.edge_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;

@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            ReactiveClientRegistrationRepository clientRegistrationRepository
    ) {
        return http
                .authorizeExchange(exchange ->
                    exchange.anyExchange().authenticated())
                .oauth2Login(Customizer.withDefaults())
                .logout(logout -> logout.logoutSuccessHandler( // 로그 아웃이 성공하면 사용자 지정 핸들러 정의
                        oidcLogoutSuccessHandler(clientRegistrationRepository)))
                .build();
    }

    private ServerLogoutSuccessHandler oidcLogoutSuccessHandler(
            ReactiveClientRegistrationRepository clientRegistrationRepository
    ) {
        var oidcLogoutSuccessHandler =
                new OidcClientInitiatedServerLogoutSuccessHandler(
                        clientRegistrationRepository);
        oidcLogoutSuccessHandler
                .setPostLogoutRedirectUri("{baseUrl}");
        return oidcLogoutSuccessHandler;
    }


}
