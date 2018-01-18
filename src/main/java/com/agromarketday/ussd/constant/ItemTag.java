package com.agromarketday.ussd.constant;

import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.sharedInterface.Constants;
import com.agromarketday.ussd.util.GeneralUtils;

/**
 *
 * @author smallgod
 */
public enum ItemTag implements Constants {

    VAP("VAP"),
    INPUT("INPUT"),
    PRODUCE("PRODUCE"),
    UNKNOWN("UNKNOWN");

    private final String enumValue;

    ItemTag(String enumValue) {
        this.enumValue = enumValue;
    }

    @Override
    public String getValue() {
        return this.enumValue;
    }

    public static ItemTag convertToEnum(String value) throws MyCustomException {

        if (value != null) {

            for (ItemTag availableValue : ItemTag.values()) {

                if (value.equalsIgnoreCase(availableValue.getValue())) {
                    return availableValue;
                }
            }
        }

        MyCustomException error = GeneralUtils.getSingleError(ErrorCode.NOT_SUPPORTED_ERR, "Unsupported ITEM TAG", "Failed to convert menu processor: " + value + "to Enum");
        throw error;
    }
}
