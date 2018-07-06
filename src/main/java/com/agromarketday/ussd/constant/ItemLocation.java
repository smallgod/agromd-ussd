package com.agromarketday.ussd.constant;

import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.sharedInterface.Constants;
import com.agromarketday.ussd.util.GeneralUtils;

/**
 *
 * @author smallgod
 */
public enum ItemLocation implements Constants {

    NATIONAL("National"),
    INTERNATIONAL("International"),
    NEARBY("Nearby"),
    UNKNOWN("UNKNOWN");

    private final String enumValue;

    ItemLocation(String enumValue) {
        this.enumValue = enumValue;
    }

    @Override
    public String getValue() {
        return this.enumValue;
    }

    public static ItemLocation convertToEnum(String value) throws MyCustomException {

        if (value != null) {

            for (ItemLocation availableValue : ItemLocation.values()) {

                if (value.equalsIgnoreCase(availableValue.getValue())) {
                    return availableValue;
                }
            }
        }
        MyCustomException error
                = GeneralUtils.getSingleError(ErrorCode.NOT_SUPPORTED_ERR,
                        "Unsupported Buyer location", "Failed to convert: "
                        + value + "to Enum");
        throw error;
    }
}
