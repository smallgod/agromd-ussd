package com.agromarketday.ussd.datamodel.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MarketPriceRequest {

    /*
    
//////// GET MARKET PRICES Request ////////////////////

    {
    "method": "GET_MARKET_PRICES",
    "localise":"english",
    "credentials": {
        "app_id": "ADER6864g25644777",//u may ignore this
        "api_password": "sLA84009rw2",//u may ignore this
        "token_id": "84938urj9338203u349393" //u may ignore this
    },
    "params": {
        "category_id":13,
        "subcategory_id":123, // mandatory  //change from commodity id to subcategory_id
        "category_class":"produce",
        "market_id":123, // -1  // ignore
        "district_id":"", // ignore
        "transport":"", // NATIONAL | ANY
        "region":"central" // mandatory
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
        private int categoryId = -1;

        @SerializedName("subcategory_id")
        @Expose
        private int subcategoryId;

        @SerializedName("category_class")
        @Expose
        private String categoryClass;

        @SerializedName("market_id")
        @Expose
        private int marketId = -1;

        @SerializedName("district_id")
        @Expose
        private int districtId = -1;

        @SerializedName("transport_area")
        @Expose
        private String transportArea;

        @SerializedName("region")
        @Expose
        private String region;
        
         @SerializedName("region_id")
        @Expose
        private int regionId;

        public Params() {
        }

        public String getTransportArea() {
            return transportArea;
        }

        public void setTransportArea(String transportArea) {
            this.transportArea = transportArea;
        }

        public int getSubcategoryId() {
            return subcategoryId;
        }

        public void setSubcategoryId(int subcategoryId) {
            this.subcategoryId = subcategoryId;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public int getMarketId() {
            return marketId;
        }

        public void setMarketId(int marketId) {
            this.marketId = marketId;
        }

        public int getDistrictId() {
            return districtId;
        }

        public void setDistrictId(int districtId) {
            this.districtId = districtId;
        }

        public int getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(int categoryId) {
            this.categoryId = categoryId;
        }

        public String getCategoryClass() {
            return categoryClass;
        }

        public void setCategoryClass(String categoryClass) {
            this.categoryClass = categoryClass;
        }

        public int getRegionId() {
            return regionId;
        }

        public void setRegionId(int regionId) {
            this.regionId = regionId;
        }
    }
}
