package com.agromarketday.ussd.datamodel.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ItemUploadRequest {

    /*
    {
      "method": "UPLOAD_ITEM_FOR_SALE",
      "localise": "english",
      "credentials": {
        "app_id": "",
        "api_password": "",
        "token_id": ""
      },
      "params": {

        "seller_contact": "256784725338",
        "category_id":103,
        "sub_category_id":487,
        "tag":"PRODUCE", //INPUT | VAP
        "transport":"NATIONAL", //NONE | INTERNATIONAL | ANY
        "item_name":"", //If selected OTHER cat or sub-cat
        "item_description":"Rice from Kibimba wetlands",
        "payment_method":"MOMO",
        "item_location":{
            "region":"WESTERN",
            "district":"Kampala",
            "place":"Kamudini"
        },
        "quantity":{
            "measure":4,
            "unit":"TON"
        },
        "selling_price":{
            "amount":10000,
            "measureUnit":"KG" //TON
        }
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

        @SerializedName("seller_contact")
        @Expose
        private String sellerContact;

        @SerializedName("category_id")
        @Expose
        private int categoryId;

        @SerializedName("sub_category_id")
        @Expose
        private int subCategoryId;

        @SerializedName("tag")
        private String tag;

        @SerializedName("item_name")
        private String itemName;

        @SerializedName("item_description")
        private String itemDescription;

        @SerializedName("payment_method")
        private String paymentMethod;

        @SerializedName("transport_area")
        private String transportArea;

        @SerializedName("item_location")
        @Expose
        private ItemLocation itemLocation;

        @SerializedName("quantity")
        @Expose
        private Quantity quantity;

        @SerializedName("selling_price")
        @Expose
        private Price sellingPrice;

        public Params() {
        }

        public String getSellerContact() {
            return sellerContact;
        }

        public void setSellerContact(String sellerContact) {
            this.sellerContact = sellerContact;
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

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public String getItemDescription() {
            return itemDescription;
        }

        public void setItemDescription(String itemDescription) {
            this.itemDescription = itemDescription;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public ItemLocation getItemLocation() {
            return itemLocation;
        }

        public void setItemLocation(ItemLocation itemLocation) {
            this.itemLocation = itemLocation;
        }

        public Quantity getQuantity() {
            return quantity;
        }

        public void setQuantity(Quantity quantity) {
            this.quantity = quantity;
        }

        public Price getSellingPrice() {
            return sellingPrice;
        }

        public void setSellingPrice(Price sellingPrice) {
            this.sellingPrice = sellingPrice;
        }

        public String getTransportArea() {
            return transportArea;
        }

        public void setTransportArea(String transportArea) {
            this.transportArea = transportArea;
        }

        public class ItemLocation {

            @SerializedName("region")
            @Expose
            private String region;

            @SerializedName("district")
            @Expose
            private String district;

            @SerializedName("place")
            @Expose
            private String place;

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

            public String getPlace() {
                return place;
            }

            public void setPlace(String place) {
                this.place = place;
            }
        }

        public class Quantity {

            @SerializedName("measure")
            @Expose
            private String measure;

            @SerializedName("unit")
            @Expose
            private String unit;

            public String getMeasure() {
                return measure;
            }

            public void setMeasure(String measure) {
                this.measure = measure;
            }

            public String getUnit() {
                return unit;
            }

            public void setUnit(String unit) {
                this.unit = unit;
            }
        }
    }
}
