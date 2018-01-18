package com.agromarketday.ussd.constant;

import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.sharedInterface.Constants;
import com.agromarketday.ussd.util.GeneralUtils;

/**
 *
 * @author smallgod
 */
public enum APIMethodName implements Constants {

    GET_BUYERS("GET_BUYERS"),
    GET_SELLERS("GET_SELLERS"),
    ACCOUNT_EXISTS("ACCOUNT_EXISTS"),
    CREATE_ACCOUNT("CREATE_ACCOUNT"),
    GET_CATEGORIES("GET_CATEGORIES"),
    GET_MARKET_PRICES("GET_MARKET_PRICES"),
    UPLOAD_ITEM_FOR_SALE("UPLOAD_ITEM_FOR_SALE"),
    CONTACT_BUYER("CONTACT_BUYER"),
    CONTACT_SELLER("CONTACT_SELLER");

    private final String methodNameString;

    APIMethodName(String method) {
        this.methodNameString = method;
    }

    @Override
    public String getValue() {
        return this.methodNameString;
    }

    public static APIMethodName convertToEnum(String methodName) throws MyCustomException {

        if (methodName != null) {

            for (APIMethodName availableMethodName : APIMethodName.values()) {

                if (methodName.equalsIgnoreCase(availableMethodName.getValue())) {
                    return availableMethodName;
                }
            }
        }

        MyCustomException error = GeneralUtils.getSingleError(ErrorCode.NOT_SUPPORTED_ERR, "Unsupported API Method Name", "Failed to convert API Method Name: " + methodName + " to Enum");
        throw error;
    }
}
