package com.agromarketday.ussd.constant;

import com.agromarketday.ussd.logger.LoggerUtil;
import com.agromarketday.ussd.sharedInterface.Constants;



/**
 *
 * @author smallgod
 */
public enum HTTPMethod implements Constants {

    GET("GET"),
    POST("POST");

    private final String httpMethodStr;

    private static final LoggerUtil logger = new LoggerUtil(HTTPMethod.class);

    HTTPMethod(String wrapperNodeStr) {
        this.httpMethodStr = wrapperNodeStr;
    }

    @Override
    public String getValue() {
        return this.httpMethodStr;
    }

    public static HTTPMethod convertToEnum(String methodName) {

        if (methodName != null) {

            for (HTTPMethod availableMethodName : HTTPMethod.values()) {

                if (methodName.equalsIgnoreCase(availableMethodName.getValue())) {
                    return availableMethodName;
                }
            }
        }
        logger.warn("No constant with text " + methodName + " found");
        throw new IllegalArgumentException("No constant with text " + methodName + " found");
        //throw new MyCustomException("Unsupported Status Exception", ErrorCode.NOT_SUPPORTED_ERR, "Unsupported status value :: " + methodName, ErrorCategory.CLIENT_ERR_TYPE);

    }
}
