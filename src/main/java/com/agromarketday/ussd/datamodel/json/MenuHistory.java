package com.agromarketday.ussd.datamodel.json;

/**
 *
 * @author smallgod
 */
import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.util.GeneralUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MenuHistory {

    /*
 {
    "menu_history": [
        {
            "menu_string": "menu string goes here..",
            "menu_name": "LANGUAGE_MENU",
            "is_end": false,
            "screen_no":1,
            "total_screens":1,
            "menu_options": [
                {
                    "option_id": 1,//display num on menu
                    "data_id": 30,//id from DB  
                    "data_value":"Tilapia"
                }
            ]
        },
        {
            "menu_string": "menu string goes here..",
            "menu_name": "LANGUAGE_MENU",
            "is_end": false,
            "screen_no":1,
            "total_screens":1,
            "menu_options": [
                {
                    "option_id": 1,
                    "data_id": 30,
                    "data_value":"Tilapia"
                }
            ]
        }
    ]
}
     */
    @Expose
    @SerializedName("menu_history")
    private List<Data> menuHistoryData = Collections.synchronizedList(new LinkedList<>());

    public MenuHistory() {
    }

    public List<Data> getMenuHistoryData() {
        return menuHistoryData;
    }

    public void setMenuHistoryData(List<Data> menuHistoryData) {
        this.menuHistoryData = menuHistoryData;
    }

    public class Data {

        @Expose
        @SerializedName("menu_string")
        private String menuString;

        @Expose
        @SerializedName("menu_name")
        private String menuName;

        @Expose
        @SerializedName("is_end")
        private boolean isEnd;

        @Expose
        @SerializedName("screen_no")
        private int screenNum;

        @Expose
        @SerializedName("total_screens")
        private int totalScreens;

        @Expose
        @SerializedName("menu_options")
        private List<MenuOption> menuOptions = Collections.synchronizedList(new LinkedList<>());

        public String getMenuString() {
            return menuString;
        }

        public void setMenuString(String menuString) {
            this.menuString = menuString;
        }

        public String getMenuName() {
            return menuName;
        }

        public void setMenuName(String menuName) {
            this.menuName = menuName;
        }

        public boolean isIsEnd() {
            return isEnd;
        }

        public void setIsEnd(boolean isEnd) {
            this.isEnd = isEnd;
        }

        public List<MenuOption> getMenuOptions() {
            return menuOptions;
        }

        public void setMenuOptions(List<MenuOption> menuOptions) {
            this.menuOptions = menuOptions;
        }

        public int getScreenNum() {
            return screenNum;
        }

        public void setScreenNum(int screenNum) {
            this.screenNum = screenNum;
        }

        public int getTotalScreens() {
            return totalScreens;
        }

        public void setTotalScreens(int totalScreens) {
            this.totalScreens = totalScreens;
        }

    }

    public class MenuOption {

        @Expose
        @SerializedName("option_id")
        private int menuOptionId;

        @Expose
        @SerializedName("data_id")
        private String dataId;

        @Expose
        @SerializedName("data_value")
        private String dataValue;

        public MenuOption() {
        }

        public int getMenuOptionId() {
            return menuOptionId;
        }

        public void setMenuOptionId(int menuOptionId) {
            this.menuOptionId = menuOptionId;
        }

        public String getDataId() {
            return dataId;
        }

        public void setDataId(String dataId) {
            this.dataId = dataId;
        }

        public String getDataValue() {
            return dataValue;
        }

        public void setDataValue(String dataValue) {
            this.dataValue = dataValue;
        }
    }

    @Override
    public String toString() {

        String historyString;

        try {
            historyString = GeneralUtils.convertToJson(this, MenuHistory.class);

        } catch (MyCustomException ex) {

            System.err.println("Error occurred while converting toString(): "
                    + ex.getMessage());

            historyString = "{}";
        }

        GeneralUtils.toPrettyJson(historyString);

        return historyString;
    }

}
