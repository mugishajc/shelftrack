package io.github.mugishajc.library.loan;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class FineCalculatorTest {

    private static final LocalDate DUE = LocalDate.of(2026, 11, 20);

    private final FineCalculator calculator = new FineCalculator(policy(0));

    @ParameterizedTest(name = "returned {0} days after due date -> {1}")
    @CsvSource({
            "-3, 0.00",
            "0, 0.00",
            "1, 100.00",
            "7, 700.00",
            "50, 5000.00",
            "51, 5000.00",
            "365, 5000.00"
    })
    void finesGrowDailyUpToTheCap(int daysLate, String expected) {
        assertThat(calculator.fineFor(DUE, DUE.plusDays(daysLate))).isEqualByComparingTo(expected);
    }

    @Test
    void fineAlwaysHasTwoDecimalPlaces() {
        assertThat(calculator.fineFor(DUE, DUE).scale()).isEqualTo(2);
        assertThat(calculator.fineFor(DUE, DUE.plusDays(3)).scale()).isEqualTo(2);
    }

    @Test
    void gracePeriodDelaysTheFirstCharge() {
        FineCalculator withGrace = new FineCalculator(policy(2));
        assertThat(withGrace.fineFor(DUE, DUE.plusDays(2))).isEqualByComparingTo("0");
        assertThat(withGrace.fineFor(DUE, DUE.plusDays(3))).isEqualByComparingTo("100");
        assertThat(withGrace.chargeableDays(DUE, DUE.plusDays(10))).isEqualTo(8);
    }

    @Test
    void fractionalRatesRoundHalfUp() {
        LoanPolicy policy = new LoanPolicy(14, 3, 1, 0, new BigDecimal("0.125"), new BigDecimal("100"));
        assertThat(new FineCalculator(policy).fineFor(DUE, DUE.plusDays(1))).isEqualByComparingTo("0.13");
    }

    @Test
    void lateReturnAcrossMonthAndYearBoundaries() {
        LocalDate due = LocalDate.of(2026, 12, 30);
        assertThat(calculator.chargeableDays(due, LocalDate.of(2027, 1, 2))).isEqualTo(3);
    }

    static LoanPolicy policy(int graceDays) {
        return new LoanPolicy(14, 3, 1, graceDays, new BigDecimal("100.00"), new BigDecimal("5000.00"));
    }
}
