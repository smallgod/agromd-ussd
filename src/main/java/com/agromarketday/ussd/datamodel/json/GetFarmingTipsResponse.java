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

public class GetFarmingTipsResponse {

    /*
    
        {
            "success": true,
            "data": [
                {
                    "tip_id": 1,
                    "tip_name": "Ponds",
                    "topics": [
                        {
                            "id": 1,
                            "name": "Site Selection",
                            "chapters": [
                                {
                                    "id": 1,
                                    "name": "Pond Levees",
                                    "content": "The pond levees must be well compacted with a slope of atleast 2:1"

                                },
                                {
                                    "id": 2,
                                    "name": "Water depth",
                                    "content": "The Water depth is supposed..."
                                }
                            ]
                        },
                        {
                            "id": 2,
                            "name": "Pond Construction",
                            "chapters": [
                                {
                                    "id": 1,
                                    "name": "Pond Levees",
                                    "content": "The pond levees must be well compacted with a slope of atleast 2:1"

                                },
                                {
                                    "id": 2,
                                    "name": "Water depth",
                                    "content": "The Water depth is supposed..."
                                },
                                {
                                    "id": 3,
                                    "name": "Inlet pipe",
                                    "content": "The pond levees must be well compacted with a slope of atleast 2:1"

                                },
                                {
                                    "id": 4,
                                    "name": "Outlet pipe",
                                    "content": "The Water depth is supposed..."
                                },
                                {
                                    "id": 5,
                                    "name": "Free Board Height",
                                    "content": "The pond levees must be well compacted with a slope of atleast 2:1"

                                },
                                {
                                    "id": 6,
                                    "name": "Harvesting Basin",
                                    "content": "The Water depth is supposed..."
                                },
                                {
                                    "id": 7,
                                    "name": "Pond Bottom",
                                    "content": "The pond levees must be well compacted with a slope of atleast 2:1"

                                },
                                {
                                    "id": 8,
                                    "name": "Pond Shape",
                                    "content": "The Water depth is supposed..."
                                }
                            ]
                        },
                        {
                            "id": 3,
                            "name": "Stocking",
                            "chapters": [
                                {
                                    "id": 1,
                                    "name": "Pond Levees",
                                    "content": "The pond levees must be well compacted with a slope of atleast 2:1"

                                },
                                {
                                    "id": 2,
                                    "name": "Water depth",
                                    "content": "The Water depth is supposed..."
                                }
                            ]
                        },
                        {
                            "id": 4,
                            "name": "Sampling",
                            "chapters": [
                                {
                                    "id": 1,
                                    "name": "Pond Levees",
                                    "content": "The pond levees must be well compacted with a slope of atleast 2:1"

                                },
                                {
                                    "id": 2,
                                    "name": "Water depth",
                                    "content": "The Water depth is supposed..."
                                }
                            ]
                        },
                        {
                            "id": 5,
                            "name": "Water quality mgt.",
                            "chapters": [
                                {
                                    "id": 1,
                                    "name": "Pond Levees",
                                    "content": "The pond levees must be well compacted with a slope of atleast 2:1"

                                },
                                {
                                    "id": 2,
                                    "name": "Water depth",
                                    "content": "The Water depth is supposed..."
                                }
                            ]
                        },
                        {
                            "id": 6,
                            "name": "Feeding",
                            "chapters": [
                                {
                                    "id": 1,
                                    "name": "Pond Levees",
                                    "content": "The pond levees must be well compacted with a slope of atleast 2:1"

                                },
                                {
                                    "id": 2,
                                    "name": "Water depth",
                                    "content": "The Water depth is supposed..."
                                }
                            ]
                        },
                        {
                            "id": 7,
                            "name": "Disease Control",
                            "chapters": [
                                {
                                    "id": 1,
                                    "name": "Pond Levees",
                                    "content": "The pond levees must be well compacted with a slope of atleast 2:1"

                                },
                                {
                                    "id": 2,
                                    "name": "Water depth",
                                    "content": "The Water depth is supposed..."
                                }
                            ]
                        },
                        {
                            "id": 8,
                            "name": "Harvesting",
                            "chapters": [
                                {
                                    "id": 1,
                                    "name": "Pond Levees",
                                    "content": "The pond levees must be well compacted with a slope of atleast 2:1"

                                },
                                {
                                    "id": 2,
                                    "name": "Water depth",
                                    "content": "The Water depth is supposed..."
                                }
                            ]
                        }
                    ]
                },
                {
                    "tip_id": 2,
                    "tip_name": "Cages",
                    "topics": [
                        {
                            "id": 1,
                            "name": "Site Selection",
                            "chapters": [
                                {
                                    "id": 1,
                                    "name": "Pond Levees",
                                    "content": "The pond levees must be well compacted with a slope of atleast 2:1"

                                },
                                {
                                    "id": 2,
                                    "name": "Water depth",
                                    "content": "The Water depth is supposed..."
                                }
                            ]
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

    private GetFarmingTipsResponse(boolean success) {
        this.success = success;
    }

    public GetFarmingTipsResponse() {
        this(Boolean.TRUE);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Set<Data> getData() {
        return Collections.unmodifiableSet(data);
    }

    public void setData(Set<Data> data) {
        this.data = data;
    }

    public class Data implements MenuItem {

        @SerializedName("tip_id")
        @Expose
        private String id;

        @SerializedName("tip_name")
        @Expose
        private String name;

        @SerializedName("topics")
        @Expose
        private Set<Topic> topics = new HashSet<>();

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

        public Set<Topic> getTopics() {
            return Collections.unmodifiableSet(topics);
        }

        public void setTopics(Set<Topic> topics) {
            this.topics = topics;
        }

    }

    public class Topic implements MenuItem {

        @SerializedName("id")
        @Expose
        private String id;

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("chapters")
        @Expose
        private Set<Chapter> chapters = new HashSet<>();

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

        public Set<Chapter> getChapters() {
            return Collections.unmodifiableSet(chapters);
        }

        public void setChapters(Set<Chapter> chapters) {
            this.chapters = chapters;
        }
    }

    public class Chapter implements MenuItem {

        @SerializedName("id")
        @Expose
        private String id;

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("content")
        @Expose
        private String content;

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

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

}
