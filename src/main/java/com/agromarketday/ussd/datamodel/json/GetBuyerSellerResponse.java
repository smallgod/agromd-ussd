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

public class GetBuyerSellerResponse {

    /*
    
    Ivan N.
    > bankslip
    > market
    
     
    {
    "success": true,
    "data": {
        
        "category_id": 123,
        "sub_category_id": 22,
        "category_class":"inputs",
        "region_name":"CENTRAL",
        "region_id":7,
        
        "districts":[

            {
                "id":21, 
                "name":"Kampala",
                "item_location":"INTERNATIONAL", // NEARBY | NATIONAL these are buyer locations, may not be ITEM_location per say
                "count":4,
                "contacts":[ 
                    {
                        "id":3,
                        "name":"Ozeki",
                        "contact":"256785243798",
                        "products": [
                            {
                                "id":44,//this is the product id - e.g. dried mukene
                                "item_title": "Dried tilapia",
                                "price": 2300,
                                "measure_unit": "KG"
                            },
                            {
                                "id":45,
                                "item_title": "Fresh tilapia",
                                "price": 2250,
                                "measure_unit": "KG"
                            }
                        ]
                    },
                    {
                        "id":4,
                        "name":"SmallG",
                        "contact":"25674990990",
                        "products": [
                            {
                                "id":46,
                                "item_title": "mukene omusiike",
                                "price": 500,
                                "measure_unit": "omukono"
                            }
                        ]
                    }
                ]
            },
            {
                "id":23, 
                "name":"Mukono",
                "item_location":"NATIONAL",
                "count":4,
                "contacts":[
                    {
                        "id":3,
                        "name":"SmallG",
                        "contact":"25674990990",
                        "products": [
                            {
                                "id":44,//this is the product id - e.g. dried mukene
                                "item_title": "Gonja",
                                "price": 15000,
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

    private GetBuyerSellerResponse(boolean success) {
        this.success = success;
    }

    public GetBuyerSellerResponse() {
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

        @SerializedName("region_id")
        @Expose
        private String regionId;

        @SerializedName("region_name")
        @Expose
        private String regionName;

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

        public String getRegionName() {
            return regionName;
        }

        public void setRegionName(String regionName) {
            this.regionName = regionName;
        }

        public String getCategoryClass() {
            return categoryClass;
        }

        public void setCategoryClass(String categoryClass) {
            this.categoryClass = categoryClass;
        }

        public String getRegionId() {
            return regionId;
        }

        public void setRegionId(String regionId) {
            this.regionId = regionId;
        }

        public class SellerDistrict implements MenuItem {

            @SerializedName("id")
            @Expose
            private String id;

            @SerializedName("name")
            @Expose
            private String name;

            @SerializedName("item_location")
            @Expose
            private String itemLocation;

            @SerializedName("count")
            @Expose
            private int count;

            @SerializedName("contacts")
            @Expose
            private Set<Contacts> contacts = new HashSet<>();

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

            public Set<Contacts> getContacts() {
                return Collections.unmodifiableSet(contacts);
            }

            public void setContacts(Set<Contacts> contacts) {
                this.contacts = contacts;
            }

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }

            public String getItemLocation() {
                return itemLocation;
            }

            public void setItemLocation(String itemLocation) {
                this.itemLocation = itemLocation;
            }

            public class Contacts implements MenuItem {

                @SerializedName("id")
                @Expose
                private String id;

                @SerializedName("name")
                @Expose
                private String name;

                @SerializedName("contact")
                @Expose
                private String contact;

                @SerializedName("products")
                @Expose
                private Set<Product> products = new HashSet<>();

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
                    private String id;

                    @SerializedName("item_title")
                    @Expose
                    private String name = "nil";

                    @SerializedName("price")
                    @Expose
                    private String price;

                    @SerializedName("measure_unit")
                    @Expose
                    private String measureUnit;

                    public String getId() {
                        return id;
                    }

                    public void setId(String id) {
                        this.id = id;
                    }

                    @Override
                    public int getCount() {
                        return -2;
                    }

                    @Override
                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public String getPrice() {
                        return price;
                    }

                    public void setPrice(String price) {
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
