package com.agromarketday.ussd.constant;

import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.sharedInterface.Constants;
import com.agromarketday.ussd.util.GeneralUtils;

/**
 *
 * @author smallgod
 */
public enum BuyerLocation implements Constants {

    NATIONAL("NATIONAL"),
    INTERNATIONAL("INTERNATIONAL"),
    NEARBY("NEARBY"),
    UNKNOWN("UNKNOWN");

    private final String enumValue;

    BuyerLocation(String enumValue) {
        this.enumValue = enumValue;
    }

    @Override
    public String getValue() {
        return this.enumValue;
    }

    public static BuyerLocation convertToEnum(String value) throws MyCustomException {

        if (value != null) {

            for (BuyerLocation availableValue : BuyerLocation.values()) {

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
