package com.fig.domain;

import com.fig.annotations.ThreadSafe;

import javax.ws.rs.core.Response;

import static com.fig.util.BindingUtil.toJson;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;

/**
 * Simple POJO to contain the request validation response along with the transformed output
 * User: Fizal
 * Date: 11/28/13
 * Time: 6:49 PM
 */
@ThreadSafe
public class ValidationResponse {
    private Response response;
    private Object output;
    private SuccessResponse successResponse;

    private ValidationResponse() {
    }

    public Response getResponse() {
        return response;
    }

    public Object getOutput() {
        return output;
    }

    public SuccessResponse getSuccessResponse() {
        return successResponse;
    }

    public boolean isResponseStatusOK(){
        return response.getStatus() == Response.Status.OK.getStatusCode();
    }

    /**
     *
     */
    public static class Builder{
        private ValidationResponse validationResponse;

        public static Builder response(){
            Builder builder = new Builder();
            builder.validationResponse = new ValidationResponse();
            return builder;
        }

        public Builder error(String reason, String message){
            validationResponse.response = status(BAD_REQUEST).entity(toJson(new ErrorResponse(reason, message))).build();;
            return this;
        }

        public Builder success(String message){
            final SuccessResponse successResponse = new SuccessResponse(message);
            validationResponse.successResponse = successResponse;
            validationResponse.response = ok().entity(toJson(successResponse)).build();
            return this;
        }

        public Builder output(Object output){
            validationResponse.output = output;
            return this;
        }

        public ValidationResponse build(){
            if(validationResponse.response == null){
                throw new RuntimeException("Response field cannot be null");
            }

            return validationResponse;
        }
    }
}
