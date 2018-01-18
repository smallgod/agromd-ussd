/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agromarketday.ussd.config;

/**
 *
 * @author smallgod
 */
public class HibernateConfig {

    private final String hibernateFilePath;

    public HibernateConfig(String hibernateFilePath) {
        this.hibernateFilePath = hibernateFilePath;
    }

    public String getHibernateFilePath() {
        return hibernateFilePath;
    }

}
