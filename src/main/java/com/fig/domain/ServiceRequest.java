package com.fig.domain;

import java.io.Serializable;

/**
 * Comment here about the class
 * User: Fizal
 * Date: 12/9/13
 * Time: 10:59 PM
 */
public class ServiceRequest implements Serializable{
    private ServiceRequestType requestType;
    private String json;
    private ValidationResponse validationResponse;

    private static final long serialVersionUID = 1L;

    public ServiceRequestType getRequestType() {
        return requestType;
    }

    public ServiceRequest setRequestType(ServiceRequestType requestType) {
        this.requestType = requestType;
        return this;
    }

    public String getJson() {
        return json;
    }

    public ServiceRequest setJson(String json) {
        this.json = json;
        return this;
    }

    public ValidationResponse getValidationResponse() {
        return validationResponse;
    }

    public ServiceRequest setValidationResponse(ValidationResponse validationResponse) {
        this.validationResponse = validationResponse;
        return this;
    }

}
