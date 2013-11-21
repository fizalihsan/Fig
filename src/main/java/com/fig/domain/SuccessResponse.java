package com.fig.domain;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.UUID;

/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/20/13
 * Time: 6:57 PM
 */
public class SuccessResponse {
    private static String HOST_NAME;
    private final String requestId;
    private final Date requestedTime;
    private String message;

    static {
        try {
            HOST_NAME = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public SuccessResponse(String message) {
        this.message = message;
        this.requestedTime = new Date();
        //Attaching hostname to avoid collisions due to the locality of the servers
        this.requestId = HOST_NAME + "-" + UUID.randomUUID().toString();
    }

    public String getRequestId() {
        return requestId;
    }

    public Date getRequestedTime() {
        return requestedTime;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SuccessResponse{");
        sb.append("requestId=").append(requestId);
        sb.append(", requestedTime=").append(requestedTime);
        sb.append(", message='").append(message).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
