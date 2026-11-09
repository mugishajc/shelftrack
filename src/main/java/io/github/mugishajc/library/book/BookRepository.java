package io.github.mugishajc.library.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    @Query("""
            select b from Book b
            where lower(b.title) like lower(concat('%', :q, '%'))
               or lower(b.author) like lower(concat('%', :q, '%'))
               or b.isbn = :q
            """)
    Page<Book> search(@Param("q") String query, Pageable pageable);

    Page<Book> findByAvailableCopiesGreaterThan(int copies, Pageable pageable);
}
