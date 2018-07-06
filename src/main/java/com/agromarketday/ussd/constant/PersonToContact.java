package com.agromarketday.ussd.constant;

import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.sharedInterface.Constants;
import com.agromarketday.ussd.util.GeneralUtils;

/**
 *
 * @author smallgod
 */
public enum PersonToContact implements Constants {

    BUYER("BUYER"),
    SELLER("SELLER"),
    DOCTOR("DOCTOR");

    private final String enumString;

    PersonToContact(String value) {
        this.enumString = value;
    }

    @Override
    public String getValue() {
        return this.enumString;
    }

    public static PersonToContact convertToEnum(String enumValue)
            throws MyCustomException {

        if (enumValue != null) {

            for (PersonToContact value : PersonToContact.values()) {

                if (enumValue.equalsIgnoreCase(value.getValue())) {
                    return value;
                }
            }
        }

        MyCustomException error
                = GeneralUtils.getSingleError(ErrorCode.NOT_SUPPORTED_ERR,
                        "Unsupported enum", "Failed to convert enum: " + enumValue + " to Enum");
        throw error;
    }
}
