package com.polarbookshop.edge_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            ReactiveClientRegistrationRepository clientRegistrationRepository
    ) {
        return http
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/", "/*.css", "/*.js", "/favicon.ico").permitAll()    // SPA의 정적 리소스에 대한 접근 허용
                        .pathMatchers(HttpMethod.GET, "/books/**").permitAll()     // 도서 검색의 경우 인증 없이 가능
                        .anyExchange().authenticated()
                )
                .exceptionHandling(exceptionHandling ->  // 원래는 자동으로 인증을 안하면 302에러로 로그인 화면으로 리다이렉트 해줬음
                        exceptionHandling.authenticationEntryPoint(         // 그런데 위의 과정은 프론트에서 처리함으로 인증하지 않으면 401에러를 주는게 맞음
                                new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)))
                .oauth2Login(Customizer.withDefaults())
                .logout(logout -> logout.logoutSuccessHandler( // 로그 아웃이 성공하면 사용자 지정 핸들러 정의
                        oidcLogoutSuccessHandler(clientRegistrationRepository)))
                .csrf(csrf -> csrf.csrfTokenRepository(
                        CookieServerCsrfTokenRepository.withHttpOnlyFalse()))
                .build();
    }

    private ServerLogoutSuccessHandler oidcLogoutSuccessHandler(ReactiveClientRegistrationRepository clientRegistrationRepository) {
        var oidcLogoutSuccessHandler = new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");
        return oidcLogoutSuccessHandler;
    }

    @Bean
    WebFilter csrfWebFilter() {
        return (exchange, chain) -> {
            exchange.getResponse().beforeCommit(() -> Mono.defer(() -> {
                Mono<CsrfToken> csrfToken = exchange.getAttribute(CsrfToken.class.getName());
                return csrfToken != null ? csrfToken.then() : Mono.empty();
            }));
            return chain.filter(exchange);
        };
    }


}
