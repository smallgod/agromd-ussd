package com.agromarketday.ussd.datamodel.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContactBuyerRequest {

    /*

    
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

        @SerializedName("contact_buyer")
        @Expose
        private boolean isContactBuyer; //contact buyer or just uploading new item

        @SerializedName("buyer_id")
        @Expose
        private String buyerId;

        @SerializedName("seller_contact")
        @Expose
        private String sellerContact;

        @SerializedName("selling")
        @Expose
        private Selling selling;

        public Params() {
        }

        public String getBuyerId() {
            return buyerId;
        }

        public void setBuyerId(String buyerId) {
            this.buyerId = buyerId;
        }

        public String getSellerContact() {
            return sellerContact;
        }

        public void setSellerContact(String sellerContact) {
            this.sellerContact = sellerContact;
        }

        public Selling getSelling() {
            return selling;
        }

        public void setSelling(Selling selling) {
            this.selling = selling;
        }

        public boolean isIsContactBuyer() {
            return isContactBuyer;
        }

        public void setIsContactBuyer(boolean isContactBuyer) {
            this.isContactBuyer = isContactBuyer;
        }
    }

    public class Selling {

        @SerializedName("item_category")
        @Expose
        private String itemCategory;

        @SerializedName("item_name")
        @Expose
        private String itemName;

        @SerializedName("item_description")
        @Expose
        private String itemDescription;

        @SerializedName("quantity")
        @Expose
        private String quantity;

        @SerializedName("item_location")
        @Expose
        private String itemLocation;

        @SerializedName("transport_area")
        @Expose
        private String transportArea;

        @SerializedName("pay_method")
        @Expose
        private String paymentMethod;

        @SerializedName("seller_price")
        @Expose
        private int sellerPrice;

        public Selling() {
        }

        public String getItemCategory() {
            return itemCategory;
        }

        public void setItemCategory(String itemCategory) {
            this.itemCategory = itemCategory;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
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

        public String getItemLocation() {
            return itemLocation;
        }

        public void setItemLocation(String itemLocation) {
            this.itemLocation = itemLocation;
        }

        public String getTransportArea() {
            return transportArea;
        }

        public void setTransportArea(String transportArea) {
            this.transportArea = transportArea;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public int getSellerPrice() {
            return sellerPrice;
        }

        public void setSellerPrice(int sellerPrice) {
            this.sellerPrice = sellerPrice;
        }
    }
}
