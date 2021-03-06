package com.agromarketday.ussd.datamodel.json;

import com.google.gson.annotations.SerializedName;

public class CreateAccountRequest {

    /*
        {
            "method": "CREATE_ACCOUNT",
            "localise": "english",
            "credentials": {
              "app_id": "",
              "api_password": "",
              "token_id": ""
            },
            "params": {
              "name": "SmallG",
              "districtId": "Kampala",//why should a user belong to a districtId/region ?? Commodities should but users dont have to!!
              "msisdn": "256784725338"
            }
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

        @SerializedName(value = "name")
        private String name;

        @SerializedName(value = "msisdn")
        private String msisdn;

        @SerializedName(value = "district_id")
        private int districtId;

        public Params() {
        }

        public String getMsisdn() {
            return msisdn;
        }

        public void setMsisdn(String msisdn) {
            this.msisdn = msisdn;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getDistrictId() {
            return districtId;
        }

        public void setDistrictId(int districtId) {
            this.districtId = districtId;
        }

    }

}
