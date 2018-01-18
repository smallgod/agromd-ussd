package com.agromarketday.ussd.constant;

import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.sharedInterface.Constants;
import com.agromarketday.ussd.util.GeneralUtils;

/**
 *
 * @author smallgod
 */
public enum TransportArea implements Constants {

    NATIONAL("NATIONAL"),
    INTERNATIONAL("INTERNATIONAL"),
    NONE("NONE"),
    UNKNOWN("UNKNOWN");

    private final String enumValue;

    TransportArea(String enumValue) {
        this.enumValue = enumValue;
    }

    @Override
    public String getValue() {
        return this.enumValue;
    }

    public static TransportArea convertToEnum(String value) throws MyCustomException {

        if (value != null) {

            for (TransportArea availableValue : TransportArea.values()) {

                if (value.equalsIgnoreCase(availableValue.getValue())) {
                    return availableValue;
                }
            }
        }
        MyCustomException error
                = GeneralUtils.getSingleError(ErrorCode.NOT_SUPPORTED_ERR,
                        "Unsupported Transport Area", "Failed to convert: "
                        + value + "to Enum");
        throw error;
    }
}
