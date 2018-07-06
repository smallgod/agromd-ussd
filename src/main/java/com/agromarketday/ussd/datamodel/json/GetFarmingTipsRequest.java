package com.agromarketday.ussd.datamodel.json;

import com.google.gson.annotations.SerializedName;

public class GetFarmingTipsRequest {

    /*
        {
            "method": "FARMING_TIPS", //
            "localise": "english",
            "credentials": {
              "app_id": "",
              "api_password": "",
              "token_id": ""
            },
            "params": {
              "farmingTipsCategory":"Fish Farming"
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

        @SerializedName(value = "category")
        private String farmingTipsCategory;

        public Params() {
        }

        public String getFarmingTipsCategory() {
            return farmingTipsCategory;
        }

        public void setFarmingTipsCategory(String farmingTipsCategory) {
            this.farmingTipsCategory = farmingTipsCategory;
        }

    }

}
