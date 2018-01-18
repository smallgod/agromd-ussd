package com.agromarketday.ussd.datamodel.json;

import com.google.gson.annotations.SerializedName;

public class SMSOneUSSDResponse {

    /*
    {
        "USSDResponseString":"1. select me \n2. Or me", //response message is what will be displayed in the users phone. 
        "USSDAction":"request" //or you can use "end" to terminate session.
    }
     */
    @SerializedName(value = "USSDResponseString")
    private String ussdResponseString;

    @SerializedName(value = "USSDAction")
    private String ussdAction;

    public String getUssdAction() {
        return ussdAction;
    }

    public void setUssdAction(String ussdAction) {
        this.ussdAction = ussdAction;
    }

    public String getUssdResponseString() {
        return ussdResponseString;
    }

    public void setUssdResponseString(String ussdResponseString) {
        this.ussdResponseString = ussdResponseString;
    }

}
