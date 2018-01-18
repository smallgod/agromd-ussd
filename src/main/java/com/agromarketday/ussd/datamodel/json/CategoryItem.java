package com.agromarketday.ussd.datamodel.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 *
 * @author smallgod
 */
public class CategoryItem {

    @SerializedName("id")
    @Expose
    private String id; //unique identifier
    
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("contact")
    @Expose
    private String contact;
    
    @SerializedName("cost_per_kg")
    @Expose
    private String costPerKg;
   

    @SerializedName("region")
    @Expose
    private String region;

    @SerializedName("district")
    @Expose
    private String district;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDistrict() {
        return district;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCostPerKg() {
        return costPerKg;
    }

    public void setCostPerKg(String costPerKg) {
        this.costPerKg = costPerKg;
    }

}
