package io.github.mugishajc.library.loan.dto;

import io.github.mugishajc.library.loan.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @param fineAmount the final fine for returned loans, or the fine accrued so
 *                   far for loans that are still out
 */
public record LoanResponse(
        Long id,
        Long bookId,
        String bookTitle,
        Long memberId,
        String memberName,
        LocalDate borrowedOn,
        LocalDate dueOn,
        LocalDate returnedOn,
        int renewals,
        LoanStatus status,
        BigDecimal fineAmount) {
}
