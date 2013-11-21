package com.fig.domain;

import java.lang.management.ManagementFactory;
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
    private String hostName;
    private String processId;
    private String requestId;
    private final Date requestedTime;
    private String message;

    public SuccessResponse(String message) {
        this.message = message;
        this.requestedTime = new Date();
    }

    public String getRequestId() {
        if(requestId == null){
            //Attaching hostname and processId to avoid collisions due to the locality of the servers
            requestId = new StringBuilder()
                    .append(getHostName()).append("-")
                    .append(getProcessId()).append("-")
                    .append(getUniqueId())
                    .toString();
        }
        return requestId;
    }

    public Date getRequestedTime() {
        return requestedTime;
    }

    public String getMessage() {
        return message;
    }

    String getHostName(){
        if(hostName==null){
            try {
                hostName = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return hostName;
    }

    String getProcessId(){
        if(processId==null){
            processId = ManagementFactory.getRuntimeMXBean().getName();
        }
        return processId;
    }

    String getUniqueId(){
        return UUID.randomUUID().toString();
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
