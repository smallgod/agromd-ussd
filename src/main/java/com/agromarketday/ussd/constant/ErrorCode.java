/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agromarketday.ussd.constant;


/**
 *
 * @author smallgod
 */
public enum ErrorCode  implements Constant{

    INVALID_USER_INPUT("INVALID_USER_INPUT"),
    FILE_NOT_FOUND_ERR("FILE_NOT_FOUND_ERROR"),
    BAD_STATE_ERR("BAD_STATE_ERROR"),
    BAD_REQUEST_ERR("BAD_REQUEST_ERROR"),
    BAD_RESPONSE_ERR("BAD_RESPONSE_ERROR"),
    PROCESSING_ERR("PROCESSING_ERROR"),
    FORMAT_ERR("FORMAT_ERROR"),
    INTERNAL_ERR("INTERNAL_ERROR"),
    ARITHMETIC_ERR("ARITHMETIC_ERROR"),
    UNAUTHORISED_CLIENT_ERR("UN-AUTHORISED_CLIENT_ERROR"),
    INVALID_CREDENTIALS_ERR("INVALID_CREDENTIALS_ERROR"),
    PASSWORDS_DONT_MATCH_ERR("PASSWORDS_DONT_MATCH_ERROR"),
    USER_NOT_EXIST_ERR("USER_NOT_EXIST_ERROR"),
    HANDSHAKE_ERR("HANDSHAKE_ERROR"),
    LOGIN_ERR("LOGIN_ERROR"),
    NO_SESSION_ERR("NO_SESSION_ERROR"),
    COMMUNICATION_ERR("COMMUNICATION_ERROR"),
    READ_TIMEOUT_ERR("READ_TIMEOUT_ERROR"),
    CONNECTION_TIMEOUT_ERR("CONNECTION_TIMEOUT_ERROR"),
    TIMEOUT_ERR("TIMEOUT_ERROR"),
    NOT_SUPPORTED_ERR("NOT_SUPPORTED_ERROR"),
    INVALID_REQUEST_ERR("INVALID_REQUEST_ERROR"),
    NOT_CONFIGURED_ERR("NOT_CONFIGURED_ERROR"),
    BENEFICIARY_ACCOUNT_NOT_FOUND_ERR("BENEFICIARY_ACCOUNT_NOT_FOUND_ERROR"),
    INVALID_ACCOUNT_DETAILS_ERR("INVALID_ACCOUNT_DETAILS_ERR0R"),
    THIRDPARTY_SYSTEM_ERR("THIRDPARTY_SYSTEM_ERROR"),
    CLIENT_ERR("CLIENT_ERROR"),
    SERVER_ERR("SERVER_ERROR"), 
    DATABASE_ERR("DATABASE_ERROR"), 
    BAD_LOGIN_TOKEN_ERR("BAD_LOGIN_TOKEN_ERROR"), 
    TERMS_AND_CONDITIONS_ERR("TERMS_AND_CONDITIONS_ERROR"), 
    ACCOUNT_VERIFICATION_ERR("ACCOUNT_VERIFICATION_ERROR"), 
    SECURITY_ERR("SECURITY_ERROR"), 
    GENERAL_ERR("GENERAL_ERROR"), 
    FILE_UPLOAD_ERR("FILE_UPLOAD_ERROR"); 

    private final String errorCodeValue;

    ErrorCode(String errorCodeValue) {
        this.errorCodeValue = errorCodeValue;
    }

    public String getValue() {
        return this.errorCodeValue;
    }
}