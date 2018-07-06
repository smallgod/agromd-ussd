package com.agromarketday.ussd.datamodel.json;

/**
 *
 * @author smallgod
 */
import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.sharedInterface.MenuItem;
import com.agromarketday.ussd.util.GeneralUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ScreenPagination {

    /*
        {
    "screens":[
        {
            "screen":1,
            "items":[
                {
                    "id":44,
                    "name":"Tilapia",
                    "extra":""
                },
                {
                    "id":46,
                    "name":"Fish",
                    "extra":"4000Ush/Kg"
                }

            ]
        },
        {
            "screen":2,
            "items":[
                {
                    "id":45,
                    "name":"Farmed Fish",
                    "extra":"3000Ush/Kg"
                },
                {
                    "id":48,
                    "name":"another_category",
                    "extra":""
                }

            ]
        }
    ]
}
     */
    @Expose
    @SerializedName("screens")
    private List<Data> screenData = Collections.synchronizedList(new LinkedList<>());

    public ScreenPagination() {
    }

    public List<Data> getScreenData() {
        return screenData;
    }

    public void setScreenData(List<Data> screenData) {
        this.screenData = screenData;
    }

    public class Data {

        @Expose
        @SerializedName("screen")
        private int screen;

        @Expose
        @SerializedName("items")
        private List<Item> items = Collections.synchronizedList(new LinkedList<>());

        public int getScreen() {
            return screen;
        }

        public void setScreen(int screen) {
            this.screen = screen;
        }

        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
            this.items = items;
        }

    }

    public class Item implements MenuItem {

        @Expose
        @SerializedName("id")
        private String id;

        @Expose
        @SerializedName("name")
        private String name;

        public Item() {
        }

        @Override
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public String getName() {
            return name;
        }
        
        @Override
        public int getCount() {
            return -2;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Override
    public String toString() {

        String historyString;

        try {
            historyString = GeneralUtils.convertToJson(this, ScreenPagination.class);

        } catch (MyCustomException ex) {

            System.err.println("Error occurred while converting toString(): "
                    + ex.getMessage());

            historyString = "{}";
        }

        GeneralUtils.toPrettyJson(historyString);

        return historyString;
    }

}
