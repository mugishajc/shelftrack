package io.github.mugishajc.library.loan;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

/**
 * Lending rules, configurable under {@code library.loans} in application.yml.
 *
 * @param loanDays        how long a book may be kept
 * @param maxActiveLoans  how many books a member may hold at once
 * @param maxRenewals     how many times one loan may be extended
 * @param gracePeriodDays days after the due date before fines start
 * @param dailyFine       fine per late day
 * @param maxFine         upper limit for a single loan's fine
 */
@Validated
@ConfigurationProperties(prefix = "library.loans")
public record LoanPolicy(
        @Min(1) int loanDays,
        @Min(1) int maxActiveLoans,
        @Min(0) int maxRenewals,
        @Min(0) int gracePeriodDays,
        @NotNull @DecimalMin("0.00") BigDecimal dailyFine,
        @NotNull @DecimalMin("0.00") BigDecimal maxFine) {
}
