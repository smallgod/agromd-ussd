package com.agromarketday.ussd.constant;

import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.sharedInterface.Constants;
import com.agromarketday.ussd.util.GeneralUtils;

/**
 *
 * @author smallgod
 */
public enum NavigationBar implements Constants {

    NEXT("NEXT"),
    NEXT_AND_MAIN("NEXT_AND_MAIN"),
    NEXT_PREVIOUS_MAIN("NEXT_PREVIOUS_MAIN"),
    PREVIOUS_AND_MAIN("PREVIOUS_AND_MAIN"), //ADD both 0: Previous and 00: Main menu buttons
    MAIN("MAIN"),
    NONE("NONE");

    private final String enumValue;

    NavigationBar(String enumValue) {
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

        MyCustomException error = GeneralUtils.getSingleError(ErrorCode.NOT_SUPPORTED_ERR, "Unsupported Navigation button", "Failed to convert navigation button: " + value + "to Enum");
        throw error;
    }
}
