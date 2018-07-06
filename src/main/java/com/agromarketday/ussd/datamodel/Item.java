/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agromarketday.ussd.datamodel;

import com.agromarketday.ussd.sharedInterface.MenuItem;

/**
 *
 * @author smallgod
 */
public class Item implements MenuItem {
    
    private String id;
    private String name;

    public Item(String id, String name) {
        this.id = id;
        this.name = name;
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
