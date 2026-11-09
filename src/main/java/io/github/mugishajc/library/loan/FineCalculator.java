package io.github.mugishajc.library.loan;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Overdue fines: {@code dailyFine} for every day past the due date (after any
 * grace period), capped at {@code maxFine}. Money is kept as BigDecimal with
 * two decimal places.
 */
@Component
public class FineCalculator {

    private final LoanPolicy policy;

    public FineCalculator(LoanPolicy policy) {
        this.policy = policy;
    }

    public long chargeableDays(LocalDate dueOn, LocalDate returnedOn) {
        long late = ChronoUnit.DAYS.between(dueOn, returnedOn) - policy.gracePeriodDays();
        return Math.max(0, late);
    }

    public BigDecimal fineFor(LocalDate dueOn, LocalDate returnedOn) {
        long days = chargeableDays(dueOn, returnedOn);
        if (days == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY);
        }
        BigDecimal fine = policy.dailyFine().multiply(BigDecimal.valueOf(days));
        return fine.min(policy.maxFine()).setScale(2, RoundingMode.HALF_UP);
    }
}
