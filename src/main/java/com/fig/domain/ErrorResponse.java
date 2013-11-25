package com.fig.domain;

import com.fig.annotations.Immutable;

/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/20/13
 * Time: 6:43 PM
 */
@Immutable
public class ErrorResponse {
    private String reason;
    private String message;

    public ErrorResponse(String reason, String message) {
        this.reason = reason;
        this.message = message;
    }

    public String getReason() {
        return reason;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ErrorResponse{");
        sb.append("reason='").append(reason).append('\'');
        sb.append(", message='").append(message).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
