package com.agromarketday.ussd.datamodel.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetCategoryRequest {

    /*
    
//////// GET CATEGORIES Request ////////////////////

{
    "method": "GET_CATEGORIES",
    "localise":"english",
    "credentials": {
        "app_id": "ADER6864g25644777",//u may ignore this
        "api_password": "sLA84009rw2",//u may ignore this
        "token_id": "84938urj9338203u349393" //u may ignore this
    },
    "params": {
        "category_id": 123, //can be -1 (empty)
        "sub_category_id": -1, //can be -1 (empty)
        "category_class":"produce", //PRODUCE || INPUT
        "function":"MATCHED_BUYERS"
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

        @SerializedName("category_class")
        @Expose
        private String categoryClass;
        
        @SerializedName("function")
        @Expose
        private String function;
        
        @SerializedName("msisdn")
        @Expose
        private String msisdn;

        public Params() {

        }

        public String getCategoryClass() {
            return categoryClass;
        }

        public void setCategoryClass(String categoryClass) {
            this.categoryClass = categoryClass;
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

        public String getFunction() {
            return function;
        }

        public void setFunction(String function) {
            this.function = function;
        }

        public String getMsisdn() {
            return msisdn;
        }

        public void setMsisdn(String msisdn) {
            this.msisdn = msisdn;
        }

    }
}
