package com.agromarketday.ussd.constant;

import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.sharedInterface.Constants;

/**
 *
 * @author smallgod
 */
public enum NavigationInput implements Constants {

    MAIN("00"),
    NEXT("99"),
    PREVIOUS("0"),
    CONTINUE("88"),
    USER_INPUT("?"); // user input can be anything

    private final String enumValue;

    NavigationInput(String enumValue) {
        this.enumValue = enumValue;
    }

    @Override
    public String getValue() {
        return this.enumValue;
    }

    public static NavigationInput convertToEnum(String value) throws MyCustomException {

        if (value != null) {

            for (NavigationInput availableValue : NavigationInput.values()) {

                if (value.equalsIgnoreCase(availableValue.getValue())) {
                    return availableValue;
                }
            }
        }
        return USER_INPUT; // if a user enters other input otherthan navigational
    }
}
