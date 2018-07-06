package com.agromarketday.ussd.datamodel.json;

/**
 *
 * @author smallgod
 */
import com.agromarketday.ussd.sharedInterface.MenuItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.HashSet;
import java.util.Set;

public class MarketPriceResponse {

    /*
       {
        "success": true,
        "data": [
            {
                "district_id": 4,
                "district_name": "Mbale",
                "default_product": {
                    "id": 396,
                    "name": "Tilapia",
                    "price": "Ugx 13,000/=",
                    "measure_unit": "kg",
                    "market": "Mbale Central Market"
                }
            }, 
            {
                "district_id": 12,
                "district_name": "Iganga",
                "default_product": {
                    "id": 427,
                    "name": "Tilapia",
                    "price": "Ugx 12,000/=",
                    "measure_unit": "kg",
                    "market": "Iganga Main Market"
                }
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

    private MarketPriceResponse(boolean success) {
        this.success = success;
    }

    public MarketPriceResponse() {
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

        @SerializedName("district_id")
        @Expose
        private String id;

        @SerializedName("district_name")
        @Expose
        private String districtName;

        @SerializedName("default_product")
        @Expose
        private Product defaultProduct;

        public String getDistrictName() {
            return districtName;
        }

        public void setDistrictName(String districtName) {
            this.districtName = districtName;
        }

        public Product getDefaultProduct() {
            return defaultProduct;
        }

        public void setDefaultProduct(Product defaultProduct) {
            this.defaultProduct = defaultProduct;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public String getName() {
            // e.g. Kampala - Dried fish - 25,000/Kg
            return this.districtName + " -" + this.getDefaultProduct().getName()
                    + " -" + this.getDefaultProduct().getPrice().split("/")[0] // to remove '/='
                    + "/" + this.getDefaultProduct().getMeasureUnit();
            //return name;
        }

        @Override
        public int getCount() {
            return -2;
        }

        public class Product {

            @SerializedName("id")
            @Expose
            private int productId;

            @SerializedName("name")
            @Expose
            private String name;

            @SerializedName("price")
            @Expose
            private String price;

            @SerializedName("measure_unit")
            @Expose
            private String measureUnit;

            @SerializedName("market")
            @Expose
            private String marketName;

            public String getPrice() {
                return price;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getMeasureUnit() {
                return measureUnit;
            }

            public void setMeasureUnit(String measureUnit) {
                this.measureUnit = measureUnit;
            }

            public int getProductId() {
                return productId;
            }

            public void setProductId(int productId) {
                this.productId = productId;
            }

            public String getName() {
                return name;
            }

            public String getMarketName() {
                return marketName;
            }

            public void setMarketName(String marketName) {
                this.marketName = marketName;
            }

        }
    }
}
