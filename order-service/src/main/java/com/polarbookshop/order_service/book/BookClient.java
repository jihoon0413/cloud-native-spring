package com.polarbookshop.order_service.book;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
public class BookClient {
    private static final String BOOKS_ROOT_API="/books/";
    private final WebClient webClient;

    public BookClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Book> getBookByIsbn(String isbn) {
        return webClient
                .get()
                .uri(BOOKS_ROOT_API + isbn)
                .retrieve() // 요청을 보내고 응답을 받는다.
                .bodyToMono(Book.class)
                .timeout(Duration.ofSeconds(3), Mono.empty())  // get 요청에 대해 3초 타임아웃 설정, 에러 대신 풀백으로 mono 빈객체 반환
                .onErrorResume(WebClientResponseException.NotFound.class,
                        e -> Mono.empty())
                .retryWhen(
                        Retry.backoff(3, Duration.ofMillis(100))
                )
                .onErrorResume(Exception.class,
                        e -> Mono.empty());
    }

}
