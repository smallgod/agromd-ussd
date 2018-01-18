package com.agromarketday.ussd.datamodel.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 *
 * @author smallgod
 */
public class Price {

    @SerializedName(value = "amount")
    @Expose
    private int amount;
    @SerializedName(value = "measure_unit")
    @Expose
    private String measureUnit;

    public Price(int amount, String measureUnit) {
        this.amount = amount;
        this.measureUnit = measureUnit;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getMeasureUnit() {
        return measureUnit;
    }

    public void setMeasureUnit(String measureUnit) {
        this.measureUnit = measureUnit;
    }

}
