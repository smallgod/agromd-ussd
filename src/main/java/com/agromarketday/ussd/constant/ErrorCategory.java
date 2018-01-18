package com.agromarketday.ussd.constant;

/**
 *
 * @author smallgod
 */
public enum ErrorCategory implements Constant {

    SERVER_ERR_TYPE("SERVER_ERROR_TYPE"),
    CLIENT_ERR_TYPE("CLIENT_ERROR_TYPE"),
    EXTERNALSYSTEM_ERR_TYPE("EXTERNALSYSTEM_ERROR_TYPE");

    private final String errorCategoryValue;

    ErrorCategory(String errorCategoryValue) {
        this.errorCategoryValue = errorCategoryValue;
    }

    @Override
    public String getValue() {
        return this.errorCategoryValue;
    }
}
