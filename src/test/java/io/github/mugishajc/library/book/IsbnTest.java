package io.github.mugishajc.library.book;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class IsbnTest {

    @ParameterizedTest
    @ValueSource(strings = {"9780132350884", "978-0-13-235088-4", "0-306-40615-2", "080442957X", "0 8044 2957 x"})
    void acceptsValidIsbns(String isbn) {
        assertThat(Isbn.isValid(isbn)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"9780132350885", "0-306-40615-3", "12345", "97801323508841", "X804429570", "abcdefghij", ""})
    void rejectsBadChecksumsAndShapes(String isbn) {
        assertThat(Isbn.isValid(isbn)).isFalse();
    }

    @Test
    void normaliseStripsSeparatorsAndUppercases() {
        assertThat(Isbn.normalise("0-8044-2957-x")).isEqualTo("080442957X");
        assertThat(Isbn.normalise(null)).isEmpty();
    }
}
