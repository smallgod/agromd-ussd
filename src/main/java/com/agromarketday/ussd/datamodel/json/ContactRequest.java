package com.agromarketday.ussd.datamodel.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContactRequest {

    /*
    
//////// GET_SELLERS or Get_BUYERS Request ////////////////////

{
  "method": "CONTACT_BUYER",
  "localise": "english",
  "credentials": {
        "app_id": "",
        "api_password": "",
        "token_id": ""
  },
  "params": {
    "customer_msisdn":"2567876577",
    "contact_person_id": 23, //can be seller or buyer
    "category_id": 23,
    "sub_category_id": 76,
    "product_id":45, //e.g. Mukene omusiike's ID
    "category_class":"PRODUCE", //INPUT | VAP
    "price":{
        "amount":4565,
        "measure_unit":"KG"
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

        @SerializedName("customer_msisdn")
        @Expose
        private String customerMsisdn;

        @SerializedName("contact_person_id")
        @Expose
        private int contactPersonId;

        @SerializedName("category_id")
        @Expose
        private int categoryId;

        @SerializedName("sub_category_id")
        @Expose
        private int subCategoryId;

        @SerializedName("product_id")
        @Expose
        private int productId; //ID OF product e.g. Mukene omusiike

        @SerializedName("category_class")
        @Expose
        private String categoryClass;

        @SerializedName("price")
        @Expose
        private Price price;

        public Params() {
        }

        public int getContactPersonId() {
            return contactPersonId;
        }

        public void setContactPersonId(int contactPersonId) {
            this.contactPersonId = contactPersonId;
        }

        public int getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(int categoryId) {
            this.categoryId = categoryId;
        }

        public String getCustomerMsisdn() {
            return customerMsisdn;
        }

        public void setCustomerMsisdn(String customerMsisdn) {
            this.customerMsisdn = customerMsisdn;
        }

        public int getSubCategoryId() {
            return subCategoryId;
        }

        public void setSubCategoryId(int subCategoryId) {
            this.subCategoryId = subCategoryId;
        }

        public Price getPrice() {
            return price;
        }

        public void setPrice(Price price) {
            this.price = price;
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public String getCategoryClass() {
            return categoryClass;
        }

        public void setCategoryClass(String categoryClass) {
            this.categoryClass = categoryClass;
        }
    }
}
