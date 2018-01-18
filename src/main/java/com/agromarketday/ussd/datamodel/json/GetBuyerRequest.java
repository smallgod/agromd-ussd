package com.agromarketday.ussd.datamodel.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetBuyerRequest {

    /*
    
//////// GET BUYERS Request ////////////////////

{
    "method": "GET_BUYERS",
    "localise":"english",
    "credentials": {
        "app_id": "ADER6864g25644777",//u may ignore this
        "api_password": "sLA84009rw2",//u may ignore this
        "token_id": "84938urj9338203u349393" //u may ignore this
    },
    "params": {
        "customer_msisdn":"256785243798",
        "category_id": 123, //can be -1 (empty)
        "sub_category_id": -1, //can be -1 (empty)
        "category_class":"produce",
        "transport":"", //NATIONAL | INTERNATIONAL | ANY
        "district":"",
        "region":"",
        "buyer":"",//such as - 256774983602
        "matched_buyers":true
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

        @SerializedName("category_id")
        @Expose
        private int categoryId;

        @SerializedName("sub_category_id")
        @Expose
        private int subCategoryId;

        @SerializedName("customer_msisdn")
        @Expose
        private String customerMsisdn;

        @SerializedName("category_class")
        @Expose
        private String categoryClass;

        @SerializedName("transport")
        @Expose
        private String transport;

        @SerializedName("region")
        @Expose
        private String region;

        @SerializedName("district")
        @Expose
        private String district;

        @SerializedName("buyer")
        @Expose
        private String buyer;

        @SerializedName("matched_buyers")
        @Expose
        private boolean matchedBuyers;

        public Params() {
            this.matchedBuyers = Boolean.FALSE; //default
        }

        public String getBuyer() {
            return buyer;
        }

        public void setBuyer(String buyer) {
            this.buyer = buyer;
        }

        public boolean isMatchedBuyers() {
            return matchedBuyers;
        }

        public void setMatchedBuyers(boolean matchedBuyers) {
            this.matchedBuyers = matchedBuyers;
        }

        public int getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(int categoryId) {
            this.categoryId = categoryId;
        }

        public int getSubCategoryId() {
            return subCategoryId;
        }

        public void setSubCategoryId(int subCategoryId) {
            this.subCategoryId = subCategoryId;
        }

        public String getCustomerMsisdn() {
            return customerMsisdn;
        }

        public void setCustomerMsisdn(String customerMsisdn) {
            this.customerMsisdn = customerMsisdn;
        }

        public String getCategoryClass() {
            return categoryClass;
        }

        public void setCategoryClass(String categoryClass) {
            this.categoryClass = categoryClass;
        }

        public String getTransport() {
            return transport;
        }

        public void setTransport(String transport) {
            this.transport = transport;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }
    }
}
