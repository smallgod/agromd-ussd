package com.agromarketday.ussd.datamodel.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContactRequest {

    /*
    
//////// GET_SELLERS or Get_BUYERS Request ////////////////////

{
  "method": "CONTACT", //
  "localise": "english",
  "credentials": {
    "app_id": "",
    "api_password": "",
    "token_id": ""
  },
  "params": {
    "customer_msisdn":"2567876577",
    "contact_person_id": "23", //can be seller or buyer or doctor etc
    "category_id": 23,
    "sub_category_id": 76,
    "product_id":45, //e.g. Mukene omusiike's ID
    "category_class":"inputs", //INPUT | VAP
    "person_to_contact":"BUYER", //SELLER | DOCTOR | 
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
        private String contactPersonId;

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

        @SerializedName("person_to_contact")
        @Expose
        private String personToContact;

        @SerializedName("price")
        @Expose
        private Price price;

        public Params() {
        }

        public String getContactPersonId() {
            return contactPersonId;
        }

        public void setContactPersonId(String contactPersonId) {
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

        public String getPersonToContact() {
            return personToContact;
        }

        public void setPersonToContact(String personToContact) {
            this.personToContact = personToContact;
        }
    }
}
