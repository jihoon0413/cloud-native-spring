package com.polarbookshop.catalog_service.domain;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface BookRepository extends CrudRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);

    @Modifying // 데이터베이스 상태를 수정할 연산임을 나타냄 @LastModifiedDate를 활성화하기 위해 사용
    @Transactional
    @Query("delete from Book where isbn = :isbn") // jdbc에서는 필드 이름으로 매핑해주는 기능이 없어 query를 적어줘야함
    void deleteByIsbn(String isbn);

}
