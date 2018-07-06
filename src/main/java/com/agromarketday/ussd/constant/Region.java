package com.agromarketday.ussd.constant;

import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.util.GeneralUtils;

/**
 *
 * @author smallgod
 */
public enum Region implements Constant {

    CENTRAL(7),
    EASTERN(6),
    WESTERN(9),
    NORTHERN(8),
    SOUTHERN(5),
    INTERNATIONAL(1),
    ALL(0),
    UNKNOWN(-1);

    private final int enumValue;

    Region(int enumValue) {
        this.enumValue = enumValue;
    }

    public int getIntValue() {
        return this.enumValue;
    }

    @Override
    public String getValue() {
        return this.toString();
    }

    public static Region convertToEnum(int value) throws MyCustomException {

        for (Region availableValue : Region.values()) {

            if (value == availableValue.getIntValue()) {
                return availableValue;
            }
        }

        MyCustomException error
                = GeneralUtils.getSingleError(ErrorCode.NOT_SUPPORTED_ERR,
                        "Unsupported Region", "Failed to convert: "
                        + value + "to Enum");
        throw error;
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
                        "Unsupported Transport Area", "Failed to convert: "
                        + value + "to Enum");
        throw error;
    }

}
