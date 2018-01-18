package com.agromarketday.ussd.exception;

import com.agromarketday.ussd.constant.ErrorCategory;
import com.agromarketday.ussd.constant.ErrorCode;



/**
 *
 * @author smallgod
 */
public class ErrorWrapper {

    private ErrorCategory errorCategory;
    private ErrorCode errorCode;
    private String description;
    private String errorDetails;

    public ErrorWrapper() {
    }

    public ErrorWrapper(ErrorCode errorCode, String description, String errorDetails) {
        this.errorCode = errorCode;
        this.description = description;
        this.errorDetails = errorDetails;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }

    public ErrorCategory getErrorCategory() {
        return errorCategory;
    }

    public void setErrorCategory(ErrorCategory errorCategory) {
        this.errorCategory = errorCategory;
    }

}
