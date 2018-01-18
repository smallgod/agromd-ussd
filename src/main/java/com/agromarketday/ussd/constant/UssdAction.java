package com.agromarketday.ussd.constant;

import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.sharedInterface.Constants;
import com.agromarketday.ussd.util.GeneralUtils;

/**
 *
 * @author smallgod
 */
public enum UssdAction implements Constants {

    REQUEST("request"),
    END("end");

    private final String enumValue;

    UssdAction(String enumValue) {
        this.enumValue = enumValue;
    }

    @Override
    public String getValue() {
        return this.enumValue;
    }

    public static UssdAction convertToEnum(String value) throws MyCustomException {

        if (value != null) {

            for (UssdAction availableValue : UssdAction.values()) {

                if (value.equalsIgnoreCase(availableValue.getValue())) {
                    return availableValue;
                }
            }
        }

        MyCustomException error = GeneralUtils.getSingleError(ErrorCode.NOT_SUPPORTED_ERR, "Unsupported Ussd Action", "Failed to convert Ussd Action: " + value + "to Enum");
        throw error;
    }
}
