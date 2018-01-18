package com.agromarketday.ussd.datamodel.json;

import com.google.gson.annotations.SerializedName;

public class SMSOneUSSDRequest {

    /*
    {
        "transactionId":"23454322",
        "transactionTime":"2018-01-15 13:14:00",//time of transaction in the form of 2018-01-15 13:14:00
        "serviceCode":"236",//the ussd code e.g. 236
        "ussdDailedCode":"*236*123#", //return dailed code e.g. *236*123# for new sessions or null for continuing sesseions
        "msisdn":"256779909090", ///phone number of user
        "ussdRequestString":"5", // //user input (what user enters in phone)
        "userInput":"5", //same as ussdRequestString
        "response":"false", //"false" for new session, "true" for continuing session
        "network":"MTN",//network through which user is using your service
        "newRequest":true//send boolean true for new ussd session or false for continuing session.
    }
     */
    @SerializedName(value = "transactionId")
    private String transactionId;

    @SerializedName(value = "transactionTime")
    private String transactionTime;

    @SerializedName(value = "serviceCode")
    private String serviceCode;

    @SerializedName(value = "ussdDailedCode")
    private String ussdDailedCode;

    @SerializedName(value = "msisdn")
    private String msisdn;

    @SerializedName(value = "ussdRequestString")
    private String ussdRequestString;

    @SerializedName(value = "userInput")
    private String userInput;

    @SerializedName(value = "response")
    private String response;

    @SerializedName(value = "network")
    private String network;

    @SerializedName(value = "newRequest")
    private boolean newRequest;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getUssdDailedCode() {
        return ussdDailedCode;
    }

    public void setUssdDailedCode(String ussdDailedCode) {
        this.ussdDailedCode = ussdDailedCode;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getUssdRequestString() {
        return ussdRequestString;
    }

    public void setUssdRequestString(String ussdRequestString) {
        this.ussdRequestString = ussdRequestString;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public boolean isNewRequest() {
        return newRequest;
    }

    public void setNewRequest(boolean newRequest) {
        this.newRequest = newRequest;
    }
}
