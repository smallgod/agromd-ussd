package com.agromarketday.ussd.exception;

import com.agromarketday.ussd.datamodel.ErrorResponse;
import com.agromarketday.ussd.logger.LoggerUtil;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author smallgod
 */
public class MyCustomException extends Throwable {

    private static final long serialVersionUID = 3599015588889622755L;
    
    
    private static final LoggerUtil logger = new LoggerUtil(MyCustomException.class);

    private String requestId;
    private Set<ErrorWrapper> errors;

    public MyCustomException(String requestId, Set<ErrorWrapper> errors) {

        super();
        this.requestId = requestId;
        this.errors = errors;

    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    /**
     * Create error response
     *
     * @return
     */
    public ErrorResponse createErrorResponse() {
        //create an error object to send to the error-page

        ErrorResponse response = new ErrorResponse();
        ErrorResponse.Data data = response.new Data();

        Set<ErrorResponse.Data.Error> responseErrors = new HashSet<>();

        for (ErrorWrapper errorWrapper : this.errors) {

            ErrorResponse.Data.Error error = data.new Error();
            error.setErrorCode(errorWrapper.getErrorCode().getValue());
            error.setDescription(errorWrapper.getDescription());
            error.setAdditionalDetails(errorWrapper.getErrorDetails());

            logger.error("Error Code: " + errorWrapper.getErrorCode());
            logger.error("Details:    " + errorWrapper.getErrorDetails());

            responseErrors.add(error);
        }

        data.setRequestId(this.requestId);
        data.setErrors(responseErrors);

        response.setData(data);

        return response;
    }

    public Set<ErrorWrapper> getErrors() {
        return errors;
    }

    public void setErrors(Set<ErrorWrapper> errors) {
        this.errors = errors;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

}
