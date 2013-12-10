package com.fig.domain;

import com.fig.annotations.Immutable;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Type;
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
@Immutable
public class SuccessResponse implements JsonSerializer<SuccessResponse>, Serializable{
    private String hostName;
    private String processId;
    private String requestId;
    private final Date requestedTime;
    private String message;

    private static final long serialVersionUID = 1L;

    public SuccessResponse(String message) {
        this.message = message;
        this.requestedTime = new Date();
    }

    public String getRequestId() {
        if(requestId == null){
            //Attaching hostname and processId to avoid collisions due to the locality of the servers
            requestId = Joiner.on("-").join(getHostName(), getProcessId(), getUniqueId());
        }
        return requestId;
    }

    public Date getRequestedTime() {
        return requestedTime;
    }

    public String getMessage() {
        return message;
    }

    @VisibleForTesting
    String getHostName() {
        if(hostName==null){
            try {
                hostName = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                throw new RuntimeException("Error getting hostname: ", e);
            }
        }
        return hostName;
    }

    @VisibleForTesting
    String getProcessId(){
        if(processId==null){
            processId = ManagementFactory.getRuntimeMXBean().getName();
        }
        return processId;
    }

    @VisibleForTesting
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

    @Override
    public JsonElement serialize(SuccessResponse response, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("requestId", response.getRequestId());
        jsonObject.addProperty("requestedTime", response.getRequestedTime().toString());
        jsonObject.addProperty("message", response.getMessage());
        return jsonObject;
    }
}
