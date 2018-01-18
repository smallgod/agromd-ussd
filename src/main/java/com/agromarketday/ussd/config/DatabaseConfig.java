/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agromarketday.ussd.config;

import com.agromarketday.ussd.sharedInterface.RemoteRequest;


/**
 *
 * @author smallgod
 */
public class DatabaseConfig {

    private final RemoteRequest dbRemoteUnit;

    public DatabaseConfig(RemoteRequest dbRemoteUnit) {
        this.dbRemoteUnit = dbRemoteUnit;
    }

    public RemoteRequest getDbRemoteUnit() {
        return dbRemoteUnit;
    }

}
