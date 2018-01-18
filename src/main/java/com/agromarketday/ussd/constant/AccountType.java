package com.agromarketday.ussd.constant;

import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.sharedInterface.Constants;
import com.agromarketday.ussd.util.GeneralUtils;



/**
 *
 * @author smallgod
 */
public enum AccountType implements Constants {

    PREPAID("PREPAID"),
    POSTPAID("POSTPAID");

    private final String enumValue;

    AccountType(String enumValue) {
        this.enumValue = enumValue;
    }

    @Override
    public String getValue() {
        return this.enumValue;
    }

    public static AccountType convertToEnum(String value) throws MyCustomException {

        if (value != null) {

            for (AccountType availableValue : AccountType.values()) {

                if (value.equalsIgnoreCase(availableValue.getValue())) {
                    return availableValue;
                }
            }
        }

        MyCustomException error = GeneralUtils.getSingleError(ErrorCode.NOT_SUPPORTED_ERR, "Unsupported Account type", "Failed to convert account type: " + value + "to Enum");
        throw error;
    }
}
