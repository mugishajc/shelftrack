package io.github.mugishajc.library.common;

/** The request clashes with existing data, e.g. a duplicate ISBN or email. */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
