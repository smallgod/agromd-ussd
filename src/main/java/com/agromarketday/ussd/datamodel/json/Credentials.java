package com.agromarketday.ussd.datamodel.json;

import com.google.gson.annotations.SerializedName;

public class Credentials {

    /*
        "credentials":{
            "app_id":"ADER6864g25644777",
            "api_password":"sLA84009rw2",
            "token_id": "" //25-xter optional field, if user not yet logged in, not mandatory
         }
     */
    @SerializedName(value = "app_id")
    private String appId;

    @SerializedName(value = "api_password")
    private String apiPassword;

    @SerializedName(value = "token_id")
    private String tokenId;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getApiPassword() {
        return apiPassword;
    }

    public void setApiPassword(String apiPassword) {
        this.apiPassword = apiPassword;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

}
