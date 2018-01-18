package com.agromarketday.ussd.datamodel;

/**
 *
 * @author smallgod
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.HashSet;
import java.util.Set;

public class ErrorResponse {

    /*
    
        {
            "success":false,
            "data":{
               "request_id":89383838,
               "errors":[
                  {
                     "error_code":"DUPLICATE_USER_ID",
                     "description":"A similar account with user id already exists. Please use a different phone number or if password forgotten, reset it.",
                     "additional_details":"Get this info from the code, e.g. exception.getMessage()"
                  },
                  {
                     "error_code":"INACTIVE_USER_ID",
                     "description":"User ID was inactivaed due to pending OTP verificaion",
                     "additional_details":"Get this info from the code, e.g. exception.getMessage()"
                  }
               ]
            }
         }
    
     */
    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("data")
    @Expose
    private Data data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    private ErrorResponse(boolean success) {
        this.success = success;
    }

    public ErrorResponse() { //init success to False for all error responses
        this(Boolean.FALSE);
    }

    public class Data {

        @SerializedName("request_id")
        @Expose
        private String requestId;

        @SerializedName("errors")
        @Expose
        private Set<Error> errors = new HashSet<>();

        public String getRequestId() {
            return requestId;
        }

        public void setRequestId(String requestId) {
            this.requestId = requestId;
        }

        public Set<Error> getErrors() {
            return errors;
        }

        public void setErrors(Set<Error> errors) {
            this.errors = errors;
        }

        public class Error {

            @SerializedName("error_code")
            @Expose
            private String errorCode;

            @SerializedName("description")
            @Expose
            private String description;

            @SerializedName("additional_details")
            @Expose
            private String additionalDetails;

            public String getErrorCode() {
                return errorCode;
            }

            public void setErrorCode(String errorCode) {
                this.errorCode = errorCode;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getAdditionalDetails() {
                return additionalDetails;
            }

            public void setAdditionalDetails(String additionalDetails) {
                this.additionalDetails = additionalDetails;
            }

        }
    }
}
