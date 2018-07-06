package com.agromarketday.ussd.datamodel.json;

/**
 *
 * @author smallgod
 */
import com.agromarketday.ussd.sharedInterface.HasChildrenItems;
import com.agromarketday.ussd.sharedInterface.HasCount;
import com.agromarketday.ussd.sharedInterface.MenuItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
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
                "category_id": 50,
                "category_name": "Farmed Fish",
                "sub_categories": [
                    {
                        "id": 21,
                        "name": "Tilapia",
                        "count":4
                    },
                    {
                        "id": 22,
                        "name": "Mukene",
                        "count":4
                    }
                ]
            },
            {
                "category_id": 51,
                "category_name": "Cereals",
                "sub_categories": [
                    {
                        "id": 30,
                        "name": "Beans",
                        "count":4
                    },
                    {
                        "id": 31,
                        "name": "G.Nuts",
                        "count":4
                    }
                ]
            },
            {
                "category_id": 52,
                "category_name": "Fruits",
                "sub_categories": [
                    {
                        "id": 32,
                        "name": "Mangoes",
                        "count":4
                    },
                    {
                        "id": 33,
                        "name": "Oranges",
                        "count":4
                    },
                    {
                        "id": 34,
                        "name": "Bananas",
                        "count":4
                    },
                    {
                        "id": 35,
                        "name": "Onions",
                        "count":4
                    },
                    {
                        "id": 36,
                        "name": "Grapes",
                        "count":4
                    },
                    {
                        "id": 37,
                        "name": "Mangada",
                        "count":4
                    },
                    {
                        "id": 38,
                        "name": "Mpaafu",
                        "count":4
                    },
                    {
                        "id": 39,
                        "name": "Gonja",
                        "count":4
                    },
                    {
                        "id": 40,
                        "name": "Dates",
                        "count":4
                    },
                    {
                        "id": 41,
                        "name": "Sugar canes",
                        "count":4
                    },
                    {
                        "id": 32,
                        "name": "Mangoes",
                        "count":4
                    },
                    {
                        "id": 33,
                        "name": "Oranges",
                        "count":4
                    },
                    {
                        "id": 34,
                        "name": "Bananas",
                        "count":4
                    },
                    {
                        "id": 35,
                        "name": "Onions",
                        "count":4
                    },
                    {
                        "id": 36,
                        "name": "Grapes",
                        "count":4
                    },
                    {
                        "id": 37,
                        "name": "Mangada",
                        "count":4
                    },
                    {
                        "id": 38,
                        "name": "Mpaafu",
                        "count":4
                    },
                    {
                        "id": 39,
                        "name": "Gonja",
                        "count":4
                    },
                    {
                        "id": 40,
                        "name": "Dates",
                        "count":4
                    },
                    {
                        "id": 41,
                        "name": "Sugar canes",
                        "count":4
                    },
                    {
                        "id": 32,
                        "name": "Mangoes",
                        "count":4
                    },
                    {
                        "id": 33,
                        "name": "Oranges",
                        "count":4
                    },
                    {
                        "id": 34,
                        "name": "Bananas",
                        "count":4
                    },
                    {
                        "id": 35,
                        "name": "Onions",
                        "count":4
                    }
                ]
            },
            {
                "category_id": 53,
                "category_name": "Coffee",
                "sub_categories": [
                    {
                        "id": 42,
                        "name": "Arabic",
                        "count":4
                    }
                ]
            },
            {
                "category_id": 54,
                "category_name": "Legumes",
                "sub_categories": [
                    {
                        "id": 43,
                        "name": "Peas",
                        "count":4
                    }
                ]
            },
            {
                "category_id": 55,
                "category_name": "Malakwang",
                "sub_categories": [
                    {
                        "id": 46,
                        "name": "keng",
                        "count":4
                    }
                ]
            },
            {
                "category_id": 56,
                "category_name": "Greens",
                "sub_categories": [
                    {
                        "id": 47,
                        "name": "Ddodo",
                        "count":4
                    },
                    {
                        "id": 48,
                        "name": "Nakkati",
                        "count":4
                    }
                ]
            },
            {
                "category_id": 57,
                "category_name": "Nsujju",
                "sub_categories": [
                    {
                        "id": 48,
                        "name": "subcategory",
                        "count":4
                    }
                ]
            }
        ],
        "vap": [
            {
                "category_id": 58,
                "category_name": "vap cat",
                "sub_categories": [
                    {
                        "id": 49,
                        "name": "vap subcat",
                        "count":4
                    }
                ]
            }
        ],
        "inputs": [
            {
                "category_id": 59,
                "category_name": "input cat",
                "sub_categories": [
                    {
                        "id": 51,
                        "name": "input subcat",
                        "count":4
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
        private String id;

        @SerializedName("category_name")
        @Expose
        private String name;

        @SerializedName("extra")
        @Expose
        private String extra;

        @SerializedName("sub_categories")
        @Expose
        private Set<SubCategory> subCategories = new HashSet<>();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {

            int totalCount = 0;
            for (SubCategory sub : subCategories) {
                totalCount += sub.getCount();
            }
            return name + "(" + totalCount + ")";
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
            return subCategories;
        }

        @Override
        public int getCount() {
            
            int totalCount = 0;
            for (SubCategory sub : subCategories) {
                totalCount += sub.getCount();
            }
            return totalCount;
        }

        public class SubCategory implements MenuItem, HasCount {

            @SerializedName("id")
            @Expose
            private String id;

            @SerializedName("name")
            @Expose
            private String name;

            @SerializedName("count")
            @Expose
            private int count;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            @Override
            public String getName() {

                return name + "(" + getCount() + ")";
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }
        }
    }
}
