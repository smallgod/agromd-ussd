package com.agromarketday.ussd.datamodel.json;

import com.google.gson.annotations.SerializedName;

public class GetStatsRequest {

    /*
        {
            "method": "GET_STATS",
            "localise": "english",
            "credentials": {
              "app_id": "",
              "api_password": "",
              "token_id": ""
            },
            "params": {
              "msisdn": "256774893602", //optional
              "start_date":"2017-02-09",
              "end_date":"2017-02-30"
            }
        }
     */
    @SerializedName(value = "localise")
    private String localise;

    @SerializedName(value = "method")
    private String methodName;

    @SerializedName(value = "credentials")
    private Credentials credentials;

    @SerializedName(value = "params")
    private Params params;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public String getLocalise() {
        return localise;
    }

    public void setLocalise(String localise) {
        this.localise = localise;
    }

    public class Params {

        @SerializedName(value = "msisdn")
        private String msisdn;
        
        @SerializedName(value = "start_date")
        private String startDate;
        
        @SerializedName(value = "end_date")
        private String endDate;

        public Params() {
        }

        public String getMsisdn() {
            return msisdn;
        }

        public void setMsisdn(String msisdn) {
            this.msisdn = msisdn;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

    }

}
