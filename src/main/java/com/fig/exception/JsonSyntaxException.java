package com.fig.exception;

/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/26/13
 * Time: 8:01 PM
 */
public class JsonSyntaxException extends RuntimeException {
    public JsonSyntaxException() {
    }

    public JsonSyntaxException(String message) {
        super(message);
    }

    public JsonSyntaxException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonSyntaxException(Throwable cause) {
        super(cause);
    }

    public JsonSyntaxException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
