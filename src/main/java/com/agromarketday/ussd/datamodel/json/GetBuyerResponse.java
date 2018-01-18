package com.agromarketday.ussd.datamodel.json;

/**
 *
 * @author smallgod
 */
import com.agromarketday.ussd.sharedInterface.MenuItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GetBuyerResponse {

    /*
        {
            "success": true,
            "data": [
                {
                    "buyer_id": 23,
                    "buyer_name": "Ozeki Junior",
                    "contact": "256785243798",
                    "buying": [
                        {
                            "product_id": 7487,
                            "product_name": "Beans",
                            "category_class":"produce",
                            "transport": "NATIONAL",
                            "region": "WESTERN",
                            "district": "Kampala",
                            "buying_price": {
                                "amount": 10000,
                                "measure_unit": "KG"
                            }
                        },
                        {
                            "product_id": 123,
                            "product_name": "Fish fingerlings",
                            "category_class":"inputs",
                            "transport": "NATIONAL",
                            "region": "WESTERN",
                            "district": "Kampala",
                            "buying_price": {
                                "amount": 10000,
                                "measure_unit": "KG"
                            }
                        }
                    ]
                },
                {
                    "buyer_id": 13,
                    "buyer_name": "Ozeki Senior",
                    "contact": "256774983602",
                    "buying": [
                        {
                            "product_id": 103,
                            "product_name": "Maize",
                            "category_class":"produce",
                            "transport": "NATIONAL",
                            "region": "WESTERN",
                            "district": "Kampala",
                            "buying_price": {
                                "amount": 10000,
                                "per": "KG"
                            }
                        }

                    ]
                }
            ]
        }
     */
    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("data")
    @Expose
    private Set<Data> data = new HashSet<>();

    private GetBuyerResponse(boolean success) {
        this.success = success;
    }

    public GetBuyerResponse() {
        this(Boolean.TRUE);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Set<Data> getData() {
        return data;
    }

    public void setData(Set<Data> data) {
        this.data = data;
    }

    public class Data implements MenuItem {

        @SerializedName("buyer_id")
        @Expose
        private int buyerId;

        @SerializedName("buyer_name")
        @Expose
        private String buyerName;

        @SerializedName("contact")
        @Expose
        private String contact;

        @SerializedName("buying")
        @Expose
        private Set<Product> buying = new HashSet<>();

        public String getBuyerName() {
            return buyerName;
        }

        public void setBuyerName(String buyerName) {
            this.buyerName = buyerName;
        }

        public int getBuyerId() {
            return buyerId;
        }

        public void setBuyerId(int buyerId) {
            this.buyerId = buyerId;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public Set<Product> getBuying() {
            return Collections.unmodifiableSet(buying);
        }

        public void setBuying(Set<Product> buying) {
            this.buying = buying;
        }

        public class Product implements MenuItem {

            @SerializedName("product_id")
            @Expose
            private int productId;

            @SerializedName("product_name")
            @Expose
            private String productName;

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

            @SerializedName("buying_price")
            @Expose
            private Price buyingPrice;

            public int getProductId() {
                return productId;
            }

            public void setProductId(int productId) {
                this.productId = productId;
            }

            public String getProductName() {
                return productName;
            }

            public void setProductName(String productName) {
                this.productName = productName;
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

            public Price getBuyingPrice() {
                return buyingPrice;
            }

            public void setBuyingPrice(Price buyingPrice) {
                this.buyingPrice = buyingPrice;
            }

            @Override
            public int getId() {
                return productId;
            }

            @Override
            public String getName() {
                return productName;
            }

        }

        @Override
        public int getId() {
            return buyerId;
        }

        @Override
        public String getName() {
            return buyerName;
        }
    }
}
