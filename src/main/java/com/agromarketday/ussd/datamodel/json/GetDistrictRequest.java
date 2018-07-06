package com.agromarketday.ussd.datamodel.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetDistrictRequest {

    /*
    

//////// GET_DISTRICTS ////////////////////

{
    "method": "GET_DISTRICTS",
    "localise":"english",
    "credentials": {
        "app_id": "ADER6864g25644777",//u may ignore this
        "api_password": "sLA84009rw2",//u may ignore this
        "token_id": "84938urj9338203u349393" //u may ignore this
    },
    "params": {
        "region_id":43 // mandatory
    }
} 
     */
    @SerializedName(value = "method")
    private String methodName;

    @SerializedName(value = "localise")
    private String localise;

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

        @SerializedName("region_id")
        @Expose
        private int regionId;

        public Params() {
        }

        public int getRegionId() {
            return regionId;
        }

        public void setRegionId(int regionId) {
            this.regionId = regionId;
        }

    }
}
