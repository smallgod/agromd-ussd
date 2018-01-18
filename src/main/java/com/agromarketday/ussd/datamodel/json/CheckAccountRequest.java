package com.agromarketday.ussd.datamodel.json;

import com.google.gson.annotations.SerializedName;

public class CheckAccountRequest {

    /*
        {
            "method": "ACCOUNT_EXISTS",
            "credentials": {
              "app_id": "ADER6864g25644777",
              "api_password": "sLA84009rw2",
              "token_id": "y0lhfdety90jfdsa223sxbrj9" //this is like a session id
            },
            "params": {
              "msisdn": 256774983602
            }
        }
     */
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

    public class Params {

        @SerializedName(value = "msisdn")
        private String msisdn;

        public Params() {
        }

        public String getMsisdn() {
            return msisdn;
        }

        public void setMsisdn(String msisdn) {
            this.msisdn = msisdn;
        }

    }

}
