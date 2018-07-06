package com.agromarketday.ussd.constant;

import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.sharedInterface.Constants;
import com.agromarketday.ussd.util.GeneralUtils;

/**
 *
 * @author smallgod
 */
public enum FarmingTipsCategories implements Constants {

    FISH("FISH"),
    UNKNOWN("UNKNOWN");

    private final String enumValue;

    FarmingTipsCategories(String enumValue) {
        this.enumValue = enumValue;
    }

    @Override
    public String getValue() {
        return this.enumValue;
    }

    public static FarmingTipsCategories convertToEnum(String value) throws MyCustomException {

        if (value != null) {

            for (FarmingTipsCategories availableValue : FarmingTipsCategories.values()) {

                if (value.equalsIgnoreCase(availableValue.getValue())) {
                    return availableValue;
                }
            }
        }

        MyCustomException error = GeneralUtils.getSingleError(ErrorCode.NOT_SUPPORTED_ERR, "Unsupported TIP CATEGORY", "Failed to convert: " + value + "to Enum");
        throw error;
    }
}
