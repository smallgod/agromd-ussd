package com.agromarketday.ussd.datamodel.json;

/**
 *
 * @author smallgod
 */
import com.agromarketday.ussd.sharedInterface.HasChildrenItems;
import com.agromarketday.ussd.sharedInterface.MenuItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GetCategoryResponse {

    /*
{
    "success": true,
    "data": {
            "produce": [
                {
                    "category_id": 0,
                    "category_name": "Other",
                    "sub_categories": []
                },
                {
                    "category_id": 44,
                    "category_name": "Farmed Fish",
                    "sub_categories": [
                        {
                            "id": 3,
                            "name": "Tilapia"
                        },
                        {
                            "id": 4,
                            "name": "Mukene"
                        }
                    ]
                },
                {
                    "category_id": 47,
                    "category_name": "Cereals",
                    "sub_categories": [
                        {
                            "id": 66,
                            "name": "Rice"
                        }
                    ]
                }
            ],

            "vap": [
                {
                    "category_id": 47,
                    "category_name": "Cereals",
                    "sub_categories": [
                        {
                            "id": 66,
                            "name": "Rice"
                        }
                    ]
                }
            ],

            "inputs": [
                {
                    "category_id": 47,
                    "category_name": "Cereals",
                    "sub_categories": [
                        {
                            "id": 66,
                            "name": "Rice"
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

    private GetCategoryResponse(boolean success) {
        this.success = success;
    }

    public GetCategoryResponse() {
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

        @SerializedName("produce")
        @Expose
        private Set<Categories> produce = new HashSet<>();

        @SerializedName("vap")
        @Expose
        private Set<Categories> vap = new HashSet<>();

        @SerializedName("inputs")
        @Expose
        private Set<Categories> inputs = new HashSet<>();

        public Set<Categories> getProduce() {
            return produce;
        }

        public void setProduce(Set<Categories> produce) {
            this.produce = produce;
        }

        public Set<Categories> getVap() {
            return vap;
        }

        public void setVap(Set<Categories> vap) {
            this.vap = vap;
        }

        public Set<Categories> getInputs() {
            return inputs;
        }

        public void setInputs(Set<Categories> inputs) {
            this.inputs = inputs;
        }

    }

    public class Categories implements HasChildrenItems {

        @SerializedName("category_id")
        @Expose
        private int id;

        @SerializedName("category_name")
        @Expose
        private String name;

        @SerializedName("extra")
        @Expose
        private String extra;

        @SerializedName("sub_categories")
        @Expose
        private Set<SubCategory> subCategories = new HashSet<>();

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

        public void setChildrenItems(Set<SubCategory> subCategories) {
            this.subCategories = subCategories;
        }

        @Override
        public String getExtra() {
            return extra;
        }

        public void setExtra(String extra) {
            this.extra = extra;
        }

        @Override
        public Set<SubCategory> getChildrenItems() {
            return Collections.unmodifiableSet(subCategories);
        }

        public class SubCategory implements MenuItem {

            @SerializedName("id")
            @Expose
            private int id;

            @SerializedName("name")
            @Expose
            private String name;

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
        }
    }
}
