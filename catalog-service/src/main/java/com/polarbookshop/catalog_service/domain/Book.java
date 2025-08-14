package com.polarbookshop.catalog_service.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

import java.time.Instant;

public record Book(

        @Id
        Long id,

        @NotNull(message = "The book ISBN must be defined.")
        @Pattern(
                regexp = "^([0-9]{10}|[0-9]{13})$",
                message = "The ISBN format must be valid."
        ) //ISBN 형식에 맞는 규칙 설정
        String isbn,

        @NotNull(message = "The book title must be defined.")
        String title,

        @NotNull(message = "The book author must be defined.")
        String author,

        @NotNull(message = "The book price must be defined.") // null이면 안됨
        @Positive(message = "The book price must be greater than zero") // 0 보다 큰 수
        Double price,

        String publisher,

        @CreatedDate
        Instant createdDate,

        @LastModifiedDate
        Instant lastModifiedDate,

        @Version // 업데이트 마다 자동으로 숫자를 증가
        int version
) {

        public static Book of(String isbn, String title, String author, Double price, String publisher) {
                return new Book(null, isbn, title, author, price, publisher, null, null, 0);
                // id 가 null이고 version이 0이여야 새로운 객체로 간주
        }
}
