package io.github.mugishajc.library.book;

/**
 * ISBN normalisation and checksum validation. Hyphens and spaces are removed,
 * and both ISBN-10 (with an optional trailing X) and ISBN-13 are checked.
 */
public final class Isbn {

    private Isbn() {
    }

    public static String normalise(String raw) {
        return raw == null ? "" : raw.replaceAll("[\\s-]", "").toUpperCase();
    }

    public static boolean isValid(String raw) {
        String isbn = normalise(raw);
        return switch (isbn.length()) {
            case 10 -> isValidIsbn10(isbn);
            case 13 -> isValidIsbn13(isbn);
            default -> false;
        };
    }

    private static boolean isValidIsbn10(String isbn) {
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            char c = isbn.charAt(i);
            int value;
            if (i == 9 && c == 'X') {
                value = 10;
            } else if (Character.isDigit(c)) {
                value = c - '0';
            } else {
                return false;
            }
            sum += value * (10 - i);
        }
        return sum % 11 == 0;
    }

    private static boolean isValidIsbn13(String isbn) {
        int sum = 0;
        for (int i = 0; i < 13; i++) {
            char c = isbn.charAt(i);
            if (!Character.isDigit(c)) {
                return false;
            }
            sum += (c - '0') * (i % 2 == 0 ? 1 : 3);
        }
        return sum % 10 == 0;
    }
}
