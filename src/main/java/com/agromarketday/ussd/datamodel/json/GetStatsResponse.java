package com.agromarketday.ussd.datamodel.json;

/**
 *
 * @author smallgod
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GetStatsResponse {

    /*
        {
    "success": true,
    "Data": [
        {
            "msisdn": "256790790491",
            "network": "",
            "session_id": "123404888",
            "session_length": 10,
            "start_time": "2018-03-22 17:23:20",
            "end_time": "2018-03-22 17:46:19",
            "visited_menus": [
                {
                    "order": 1,
                    "menu": "MAIN_MENU"
                },
                {
                    "order": 2,
                    "menu": "LEARNING_CATEGORIES"
                }
            ]
        },
        {
            "msisdn": "256790790491",
            "network": "",
            "session_id": "1234039075",
            "session_length": 10,
            "start_time": "2018-03-22 17:17:03",
            "end_time": "2018-03-22 17:24:20",
            "visited_menus": [
                {
                    "order": 2,
                    "menu": "MAIN_MENU"
                },
                {
                    "order": 1,
                    "menu": "FARMING_TIPS_CONTENT"
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

    private GetStatsResponse(boolean success) {
        this.success = success;
    }

    public GetStatsResponse() {
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

    public class Data {

        @SerializedName("msisdn")
        @Expose
        private String msisdn;

        @SerializedName("network")
        @Expose
        private String network;

        @SerializedName("session_id")
        @Expose
        private String sessionId;

        @SerializedName("session_length")
        @Expose
        private int sessionLength;

        @SerializedName("start_time")
        @Expose
        private String startTime;

        @SerializedName("end_time")
        @Expose
        private String endTime;

        @SerializedName("visited_menus")
        @Expose
        private Set<VisitedMenu> visitedMenus = new HashSet<>();

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public Set<VisitedMenu> getVisitedMenus() {
            return visitedMenus;
        }

        public void setVisitedMenus(Set<VisitedMenu> visitedMenus) {
            this.visitedMenus = visitedMenus;
        }

        public int getSessionLength() {
            return sessionLength;
        }

        public void setSessionLength(int sessionLength) {
            this.sessionLength = sessionLength;
        }

        public String getMsisdn() {
            return msisdn;
        }

        public void setMsisdn(String msisdn) {
            this.msisdn = msisdn;
        }

        public String getNetwork() {
            return network;
        }

        public void setNetwork(String network) {
            this.network = network;
        }

        public class VisitedMenu {

            @SerializedName("order")
            @Expose
            private int order;

            @SerializedName("menu")
            @Expose
            private String menu;

            public int getOrder() {
                return order;
            }

            public void setOrder(int order) {
                this.order = order;
            }

            public String getMenu() {
                return menu;
            }

            public void setMenu(String menu) {
                this.menu = menu;
            }

        }
    }

}
