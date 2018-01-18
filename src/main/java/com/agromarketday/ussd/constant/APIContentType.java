package com.agromarketday.ussd.constant;

import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.sharedInterface.Constants;
import com.agromarketday.ussd.util.GeneralUtils;



/**
 * Represents the different states a processing unit such as a monitoring unit
 * or AdDisplay unit could be in at any one moment
 *
 * @author smallgod
 */
public enum APIContentType implements Constants {

    JSON("application/json"),
    XML("application/xml"),
    HTTP("HTTP");

    private final String apiType;

    APIContentType(String apiType) {
        this.apiType = apiType;
    }

    @Override
    public String getValue() {
        return this.apiType;
    }

    public static APIContentType convertToEnum(String givenApiType) throws MyCustomException {

        if (givenApiType != null) {

            for (APIContentType knownApiType : APIContentType.values()) {

                if (knownApiType.getValue().equalsIgnoreCase(givenApiType)) {
                    return knownApiType;
                }
            }
        }

        MyCustomException error = GeneralUtils.getSingleError(ErrorCode.NOT_SUPPORTED_ERR, "Unsupported API Content Type", "Failed to convert APIContentType: " + givenApiType + "to Enum");

        throw error;
    }
}
