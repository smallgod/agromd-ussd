/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agromarketday.ussd.datamodel.datawrappers;

import com.agromarketday.ussd.datamodel.jaxb.config.Remoteunittype;
import com.agromarketday.ussd.sharedInterface.RemoteRequest;
import java.util.Map;



/**
 *
 * @author smallgod
 */
public class RemoteUnitWrapper implements RemoteRequest {

    private final Remoteunittype remoteUnitType;
    private Map<String, Object> httpParams;

    public RemoteUnitWrapper(Remoteunittype remoteUnitType) {
        this.remoteUnitType = remoteUnitType;
    }

    @Override
    public String getJsonUrl() {
        return this.remoteUnitType.getJsonurl();
    }

    @Override
    public String getXmlUrl() {
        return this.remoteUnitType.getXmlurl();
    }

    @Override
    public String getUserName() {
        return this.remoteUnitType.getUsername();
    }

    @Override
    public String getPassWord() {
        return this.remoteUnitType.getPassword();
    }

    @Override
    public String getUnitName() {
        return this.remoteUnitType.getName();
    }

    @Override
    public String getPreviewUrl() {
        return this.remoteUnitType.getPreviewurl();
    }

    @Override
    public Map<String, Object> getHttpParams() {
        return httpParams;
    }

    @Override
    public void setHttpParams(Map<String, Object> httpParams) {
        this.httpParams = httpParams;
    }

}
