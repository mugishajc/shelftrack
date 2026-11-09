package io.github.mugishajc.library.loan;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    long countByMemberIdAndReturnedOnIsNull(Long memberId);

    boolean existsByMemberIdAndReturnedOnIsNullAndDueOnBefore(Long memberId, LocalDate date);

    boolean existsByBookIdAndReturnedOnIsNull(Long bookId);

    boolean existsByBookIdAndMemberIdAndReturnedOnIsNull(Long bookId, Long memberId);

    @EntityGraph(attributePaths = {"book", "member"})
    Page<Loan> findByMemberId(Long memberId, Pageable pageable);

    @EntityGraph(attributePaths = {"book", "member"})
    Page<Loan> findByReturnedOnIsNull(Pageable pageable);

    @EntityGraph(attributePaths = {"book", "member"})
    Page<Loan> findByReturnedOnIsNotNull(Pageable pageable);

    @EntityGraph(attributePaths = {"book", "member"})
    @Query("select l from Loan l where l.returnedOn is null and l.dueOn < :today")
    Page<Loan> findOverdue(@Param("today") LocalDate today, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"book", "member"})
    Page<Loan> findAll(Pageable pageable);
}
