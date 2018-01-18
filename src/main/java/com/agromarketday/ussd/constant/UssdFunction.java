package com.agromarketday.ussd.constant;

import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.sharedInterface.Constants;
import com.agromarketday.ussd.util.GeneralUtils;

/**
 *
 * @author smallgod
 */
public enum UssdFunction implements Constants {

    FIND_BUYERS("FIND_BUYERS"),
    MARKET_PRICES("MARKET_PRICES"),
    INPUT_TOOLS("INPUT_TOOLS"),
    MATCHED_BUYERS("MATCHED_BUYERS"),
    SELLERS_TRADERS("SELLERS_TRADERS"),
    UNKNOWN("UNKNOWN"), 
    SELL("SELL");

    private final String enumValue;

    UssdFunction(String enumValue) {
        this.enumValue = enumValue;
    }

    @Override
    public String getValue() {
        return this.enumValue;
    }

    public static UssdFunction convertToEnum(String value) throws MyCustomException {

        if (value != null) {

            for (UssdFunction availableValue : UssdFunction.values()) {

                if (value.equalsIgnoreCase(availableValue.getValue())) {
                    return availableValue;
                }
            }
        }
        MyCustomException error
                = GeneralUtils.getSingleError(ErrorCode.NOT_SUPPORTED_ERR,
                        "Unsupported function", "Failed to convert: "
                        + value + "to Enum");
        throw error;
    }
}
