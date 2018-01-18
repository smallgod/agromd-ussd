package com.agromarketday.ussd.datamodel;

import com.agromarketday.ussd.constant.UssdAction;

/**
 *
 * @author smallgod
 */
public class NextNavigation {

    private AgNavigation navig;
    private UssdAction ussdAction;
    private String responseString;

    public NextNavigation(AgNavigation navig, String responseString, boolean isMenuEnd) {

        this.navig = navig;
        this.responseString = responseString;

        if (isMenuEnd) {
            this.ussdAction = UssdAction.END;
        } else {
            this.ussdAction = UssdAction.REQUEST;
        }
    }

    public AgNavigation getNavig() {
        return navig;
    }

    public void setNavig(AgNavigation navig) {
        this.navig = navig;
    }

    public UssdAction getUssdAction() {
        return ussdAction;
    }

    public void setUssdAction(UssdAction ussdAction) {
        this.ussdAction = ussdAction;
    }

    public String getResponseString() {
        return responseString;
    }

    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }
}
