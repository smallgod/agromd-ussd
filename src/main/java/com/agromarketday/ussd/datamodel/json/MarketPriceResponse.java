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
                "district_id":34,
                "district_name": "Kampala",
                "default_product": {
                    "id":1,
                    "name": "Dried tilapia",
                    "price": 6300,
                    "measure_unit": "KG"
                }
            },
            {
                "district_id":35,
                "district_name": "Mukono",
                "default_product": {
                    "id":2,
                    "name": "Mukene",
                    "price": 2500,
                    "measure_unit": "KG"
                }
            },
            {
                "district_id":36,
                "district_name": "Kumi",
                "default_product": {
                    "id":3,
                    "name": "Mukene",
                    "price": 2800,
                    "measure_unit": "KG"
                }
            },
            {
                "district_id":37,
                "district_name": "Gulu",
                "default_product": {
                    "id":4,
                    "name": "Fresh tilapia",
                    "price": 5400,
                    "measure_unit": "whole"
                }
            },
            {
                "district_id":38,
                "district_name": "Kabale",
                "default_product": {
                    "id":6,
                    "name": "Dried tilapia",
                    "price": 6300,
                    "measure_unit": "KG"
                }
            },
            {
                "district_id":39,
                "district_name": "Kiboga",
                "default_product": {
                    "id":7,
                    "name": "Mukene",
                    "price": 2500,
                    "measure_unit": "KG"
                }
            },
            {
                "district_id":40,
                "district_name": "Nakapiripirit",
                "default_product": {
                    "id":8,
                    "name": "Nkejje",
                    "price": 3800,
                    "measure_unit": "KG"
                }
            },
            {
                "district_id":41,
                "district_name": "Soroti",
                "default_product": {
                    "id":9,
                    "name": "Fresh tilapia",
                    "price": 4400,
                    "measure_unit": "whole"
                }
            },
            {
                "district_id":42,
                "district_name": "Masaka",
                "default_product": {
                    "id":10,
                    "name": "Tilapia",
                    "price": 4300,
                    "measure_unit": "Kg"
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
        private int id;

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

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public String getName() {
            // e.g. Kampala - Dried fish - 25,000/Kg
            return this.districtName + " -" + this.getDefaultProduct().getName()
                    + " -" + this.getDefaultProduct().getPrice()
                    + "/" + this.getDefaultProduct().getMeasureUnit();
            //return name;
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
            private int price;

            @SerializedName("measure_unit")
            @Expose
            private String measureUnit;

            public int getPrice() {
                return price;
            }

            public void setPrice(int price) {
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

        }
    }
}
