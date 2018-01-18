package com.agromarketday.ussd.datamodel;

/**
 *
 * @author smallgod
 */
public class DataItem {

    private String dataId;
    private String dataValue;
    private int menuOptionId;

    public DataItem(String dataId, String dataValue, int menuOptionId) {
        this.dataId = dataId;
        this.dataValue = dataValue;
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

    public int getMenuOptionId() {
        return menuOptionId;
    }

    public void setMenuOptionId(int menuOptionId) {
        this.menuOptionId = menuOptionId;
    }

}
