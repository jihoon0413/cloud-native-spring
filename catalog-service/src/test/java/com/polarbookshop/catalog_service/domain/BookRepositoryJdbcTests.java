package com.polarbookshop.catalog_service.domain;

import com.polarbookshop.catalog_service.config.DataConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest // 모든 스프링 데이터 JDBC 엔티티, repository, 애플리케이션 콘텍스트를 로드, 각 테스트에서 설정한 데이터들을 다시 롤백하여 다른 테스트들에 영향을 미치지 않도록 함
@Import(DataConfig.class) // 데이터 설정 임포트 (감사를 활성화 하기 위해)
@AutoConfigureTestDatabase( // 테스트 컨테이너 사용을 위해 내장 테스트 데이터베이스 비활성화
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@ActiveProfiles("integration") //application-integration.yml 로드
public class BookRepositoryJdbcTests {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private JdbcAggregateTemplate jdbcAggregateTemplate; // 데이터베이스롸 상호작용을 위한 레파지토리 하위 레벨 객체

    @Test
    void findBookByIsbnWhenExisting() {
        var bookIsbn = "1234561237";
        var book = Book.of(bookIsbn, "Title", "Author", 9.90, "publisher");
        jdbcAggregateTemplate.insert(book); // 테스트에 필요한 데이터를 준비하는데 사용
        Optional<Book> actualBook = bookRepository.findByIsbn(bookIsbn);

        assertThat(actualBook).isPresent();
        assertThat(actualBook.get().isbn()).isEqualTo(bookIsbn);

    }

}
