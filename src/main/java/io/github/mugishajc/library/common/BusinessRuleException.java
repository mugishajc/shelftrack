package io.github.mugishajc.library.common;

/** A well-formed request that breaks a lending rule, e.g. too many open loans. */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
