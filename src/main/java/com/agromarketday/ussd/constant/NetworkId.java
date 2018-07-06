package com.agromarketday.ussd.constant;

import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.sharedInterface.Constants;
import com.agromarketday.ussd.util.GeneralUtils;

/**
 *
 * @author smallgod
 */
public enum NetworkId implements Constants {

    MTN_UG("MTN_UG"),
    AIRTEL_UG("AIRTEL_UG"),
    AFRICELL_UG("AFRICELL_UG"),
    UNKOWN("UNKOWN"),
    UTL("UTL");

    private final String enumValue;

    NetworkId(String enumValue) {
        this.enumValue = enumValue;
    }

    @Override
    public String getValue() {
        return this.enumValue;
    }

    public static NetworkId convertToEnum(String value) throws MyCustomException {

        if (value != null) {

            for (NetworkId availableValue : NetworkId.values()) {

                if (value.equalsIgnoreCase(availableValue.getValue())) {
                    return availableValue;
                }
            }
        }
        MyCustomException error
                = GeneralUtils.getSingleError(ErrorCode.NOT_SUPPORTED_ERR,
                        "Unsupported Network", "Failed to convert: "
                        + value + "to Enum");
        throw error;
    }
}
