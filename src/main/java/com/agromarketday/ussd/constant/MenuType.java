package com.agromarketday.ussd.constant;

import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.sharedInterface.Constants;
import com.agromarketday.ussd.util.GeneralUtils;

/**
 *
 * @author smallgod
 */
public enum MenuType implements Constants {

    SELECT_STATIC_MENU("SELECT_STATIC_MENU"), //Whether menu is for selecting an item or typing in
    SELECT_STATIC_DYNAMIC_MENU("SELECT_STATIC_DYNAMIC_MENU"), //A static menu that has some dynamic content e.g. count (3)
    SELECT_DYNAMIC_MENU("SELECT_DYNAMIC_MENU"),
    INPUT_MENU("INPUT_MENU");

    private final String enumValue;

    MenuType(String enumValue) {
        this.enumValue = enumValue;
    }

    @Override
    public String getValue() {
        return this.enumValue;
    }

    public static MenuType convertToEnum(String value) throws MyCustomException {

        if (value != null) {

            for (MenuType availableValue : MenuType.values()) {

                if (value.equalsIgnoreCase(availableValue.getValue())) {
                    return availableValue;
                }
            }
        }

        MyCustomException error = GeneralUtils.getSingleError(ErrorCode.NOT_SUPPORTED_ERR, "Unsupported Menu type name", "Failed to convert menu type: " + value + "to Enum");
        throw error;
    }
}
