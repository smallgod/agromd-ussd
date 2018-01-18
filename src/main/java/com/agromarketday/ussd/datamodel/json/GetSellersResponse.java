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

public class GetSellersResponse {

    /*
        {
            "success": true,
            "data": {
                "category_id": 123,
                "sub_category_id": 22,
                "category_class": "inputs",
                "region": "CENTRAL",
                "districts": [
                    {
                        "id": 21,
                        "name": "Kampala",
                        "sellers": [
                            {
                                "id": 3,
                                "name": "Ozeki",
                                "contact": "256785243798",
                                "products": [
                                    {
                                        "id": 44,
                                        "item_name": "Dried tilapia",
                                        "products": 2300,
                                        "measure_unit": "KG"
                                    },
                                    {
                                        "id": 45,
                                        "item_name": "Fresh tilapia",
                                        "products": 2250,
                                        "measure_unit": "KG"
                                    }
                                ]
                            },
                            {
                                "id": 4,
                                "name": "SmallG",
                                "contact": "25674990990",
                                "products": [
                                    {
                                        "id": 46,
                                        "item_name": "mukene omusiike",
                                        "products": 500,
                                        "measure_unit": "omukono"
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        "id": 23,
                        "name": "Mukono",
                        "sellers": [
                            {
                                "id": 3,
                                "name": "SmallG",
                                "contact": "25674990990",
                                "products": [
                                    {
                                        "id": 44,
                                        "item_name": "Gonja",
                                        "products": 15000,
                                        "measure_unit": "enkota"
                                    }
                                ]
                            }
                        ]
                    }
                ]
            }
        }
     */
    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("data")
    @Expose
    private Data data;

    private GetSellersResponse(boolean success) {
        this.success = success;
    }

    public GetSellersResponse() {
        this(Boolean.TRUE);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("category")
        @Expose
        private String categoryId;

        @SerializedName("sub_category_id")
        @Expose
        private String subCategoryId;

        @SerializedName("category_class")
        @Expose
        private String categoryClass;

        @SerializedName("region")
        @Expose
        private String region;

        @SerializedName("districts")
        @Expose
        private Set<SellerDistrict> districts = new HashSet<>();

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public Set<SellerDistrict> getDistricts() {
            return Collections.unmodifiableSet(districts);
        }

        public void setDistricts(Set<SellerDistrict> districts) {
            this.districts = districts;
        }

        public String getSubCategoryId() {
            return subCategoryId;
        }

        public void setSubCategoryId(String subCategoryId) {
            this.subCategoryId = subCategoryId;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getCategoryClass() {
            return categoryClass;
        }

        public void setCategoryClass(String categoryClass) {
            this.categoryClass = categoryClass;
        }

        public class SellerDistrict implements MenuItem {

            @SerializedName("id")
            @Expose
            private int id;

            @SerializedName("name")
            @Expose
            private String name;

            @SerializedName("sellers")
            @Expose
            private Set<Seller> sellers = new HashSet<>();

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Set<Seller> getSellers() {
                return Collections.unmodifiableSet(sellers);
            }

            public void setSellers(Set<Seller> sellers) {
                this.sellers = sellers;
            }

            public class Seller implements MenuItem {

                @SerializedName("id")
                @Expose
                private int id;

                @SerializedName("name")
                @Expose
                private String name;

                @SerializedName("contact")
                @Expose
                private String contact;

                @SerializedName("products")
                @Expose
                private Set<Product> products = new HashSet<>();

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public String getName() {
                    //return name + " - Shs." + products.getAmount() + "/" + products.getMeasureUnit();
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getContact() {
                    return contact;
                }

                public void setContact(String contact) {
                    this.contact = contact;
                }

                public Set<Product> getProducts() {
                    return Collections.unmodifiableSet(products);
                }

                public void setProducts(Set<Product> products) {
                    this.products = products;
                }

                public class Product implements MenuItem {

                    @SerializedName("id")
                    @Expose
                    private int id;

                    @SerializedName("item_name")
                    @Expose
                    private String name;

                    @SerializedName("price")
                    @Expose
                    private int price;

                    @SerializedName("measure_unit")
                    @Expose
                    private String measureUnit;

                    public int getId() {
                        return id;
                    }

                    public void setId(int id) {
                        this.id = id;
                    }

                    @Override
                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
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
                }
            }
        }
    }
}
