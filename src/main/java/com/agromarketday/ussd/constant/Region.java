package com.agromarketday.ussd.constant;

import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.sharedInterface.Constants;
import com.agromarketday.ussd.util.GeneralUtils;

/**
 *
 * @author smallgod
 */
public enum Region implements Constants {

    CENTRAL("CENTRAL"),
    EASTERN("EASTERN"),
    WESTERN("WESTERN"),
    NORTHERN("NORTHERN"),
    SOUTHERN("SOUTHERN"),
    INTERNATIONAL("INTERNATIONAL"),
    ALL("ALL"),
    UNKNOWN("UNKNOWN");

    private final String enumValue;

    Region(String enumValue) {
        this.enumValue = enumValue;
    }

    @Override
    public String getValue() {
        return this.enumValue;
    }

    public static Region convertToEnum(String value) throws MyCustomException {

        if (value != null) {

            for (Region availableValue : Region.values()) {

                if (value.equalsIgnoreCase(availableValue.getValue())) {
                    return availableValue;
                }
            }
        }
        MyCustomException error
                = GeneralUtils.getSingleError(ErrorCode.NOT_SUPPORTED_ERR,
                        "Unsupported Region", "Failed to convert: "
                        + value + "to Enum");
        throw error;
    }
}
