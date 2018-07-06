package com.agromarketday.ussd.datamodel.json;

/**
 *
 * @author smallgod
 */
import com.agromarketday.ussd.sharedInterface.DistrictMarket;
import com.agromarketday.ussd.sharedInterface.MenuItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MarketPriceResponse2 {

    /*
        {
        "success": true,
        "data": [
            {
                "region": "central",
                "districts": [
                    {
                        "id": 2,
                        "name": "Kampala",
                        "products": [
                            {
                                "id": 21,
                                "name": "Beans",
                                "markets": [
                                    {
                                        "id": 65,
                                        "name": "Owino",
                                        "prices": [
                                            {
                                                "item_name": "Kanyebwa",
                                                "price": 2300,
                                                "measure_unit": "KG"
                                            },
                                            {
                                                "item_name": "Nambale",
                                                "price": 2250,
                                                "measure_unit": "KG"
                                            }
                                        ]
                                    },
                                    {
                                        "id": 66,
                                        "name": "Wandegeya",
                                        "prices": [
                                            {
                                                "item_name": "Kawula",
                                                "price": 2000,
                                                "measure_unit": "KG"
                                            },
                                            {
                                                "item_name": "kajanjalo",
                                                "price": 1550,
                                                "measure_unit": "KG"
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        "id": 3,
                        "name": "Mukono",
                        "products": []
                    }
                ]
            },
            {
                "region": "eastern",
                "districts": [
                    {
                        "id": 4,
                        "name": "Busia",
                        "products": [
                            {
                                "id": 25,
                                "name": "Rice",
                                "markets": [
                                    {
                                        "id": 68,
                                        "name": "Owino",
                                        "prices": [
                                            {
                                                "item_name": "Pakistani",
                                                "price": 2300,
                                                "measure_unit": "KG"
                                            },
                                            {
                                                "item_name": "Nambale",
                                                "price": 2250,
                                                "measure_unit": "KG"
                                            }
                                        ]
                                    },
                                    {
                                        "id": 69,
                                        "name": "Wandegeya",
                                        "prices": [
                                            {
                                                "item_name": "Basmati",
                                                "price": 2000,
                                                "measure_unit": "KG"
                                            },
                                            {
                                                "item_name": "Kaiso",
                                                "price": 1550,
                                                "measure_unit": "KG"
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        "id": 2,
                        "name": "Mukono",
                        "products": []
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

    private MarketPriceResponse2(boolean success) {
        this.success = success;
    }

    public MarketPriceResponse2() {
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

    public class Data {

        @SerializedName("region")
        @Expose
        private String region;

        @SerializedName("districts")
        @Expose
        private Set<District> districts = new HashSet<>();

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public Set<District> getDistricts() {
            return districts;
        }

        public void setDistricts(Set<District> districts) {
            this.districts = districts;
        }

        public class District implements DistrictMarket {

            @SerializedName("id")
            @Expose
            private String id;

            @SerializedName("name")
            @Expose
            private String name;

            @SerializedName("products")
            @Expose
            private Set<Product> products = new HashSet<>();

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Set<Product> getProducts() {
                return products;
            }

            public void setProducts(Set<Product> products) {
                this.products = products;
            }

            public String getId() {
                return id;
            }

            @Override
            public int getCount() {
                return -2;
            }

            public void setId(String id) {
                this.id = id;
            }

            public class Product implements MenuItem {

                @SerializedName("id")
                @Expose
                private String id;

                @SerializedName("name")
                @Expose
                private String name;

                @SerializedName("markets")
                @Expose
                private Set<Market> markets = new HashSet<>();

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public Set<Market> getMarkets() {
                    return markets;
                }

                public void setMarkets(Set<Market> markets) {
                    this.markets = markets;
                }

                public class Market implements MenuItem {

                    @SerializedName("id")
                    @Expose
                    private String id;

                    @SerializedName("name")
                    @Expose
                    private String name;

                    @SerializedName("prices")
                    private Set<Price> prices = new HashSet<>();

                    public String getId() {
                        return id;
                    }

                    @Override
                    public int getCount() {
                        return -2;
                    }

                    public void setId(String id) {
                        this.id = id;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public Set<Price> getPrices() {
                        return Collections.unmodifiableSet(prices);
                    }

                    public void setPrices(Set<Price> prices) {
                        this.prices = prices;
                    }

                    public class Price implements MenuItem {

                        @SerializedName("item_name")
                        @Expose
                        private String itemName;

                        @SerializedName("price")
                        @Expose
                        private int price;

                        @SerializedName("measure_unit")
                        @Expose
                        private String measureUnit;

                        public String getItemName() {
                            return itemName;
                        }

                        public void setItemName(String itemName) {
                            this.itemName = itemName;
                        }

                        public int getPrice() {
                            return price;
                        }

                        public void setPrice(int price) {
                            this.price = price;
                        }

                        public String getMeasureUnit() {
                            return measureUnit;
                        }

                        public void setMeasureUnit(String measureUnit) {
                            this.measureUnit = measureUnit;
                        }

                        @Override
                        public String getId() {
                            return "0";
                        }

                        @Override
                        public String getName() {
                            return itemName + " - " + price + "/" + measureUnit;
                        }

                        @Override
                        public int getCount() {
                            return -2;
                        }
                    }
                }

                @Override
                public int getCount() {
                    return -2;
                }
            }
        }
    }
}
