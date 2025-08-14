package com.polarbookshop.catalog_service.demo;

import com.polarbookshop.catalog_service.domain.Book;
import com.polarbookshop.catalog_service.domain.BookRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("testdata")  // testdata 프로파일이 활성활 될 때만 로드 됨
public class BookDataLoader {
    private final BookRepository bookRepository;

    public BookDataLoader(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @EventListener(ApplicationReadyEvent.class) // ApplicationReadyEvent가 발생하면 다음 메소드(테스트 데이터 생성)를 실행
    public void loadBookTestData() {
        bookRepository.deleteAll();
        var book1 = Book.of("1234567891", "Northern Lights", "Lyra Silverstar", 9.90, "Super boy");
        var book2 = Book.of("1234567892", "Polar Journey", "Iorek Polarson", 12.90, "Zombie");
        bookRepository.saveAll(List.of(book1, book2));
    }

}
