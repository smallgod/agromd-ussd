/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agromarketday.ussd.sharedInterface;

import java.util.Map;

/**
 *
 * @author smallgod
 */
public interface RemoteRequest {

    public String getJsonUrl();
    
    public String getXmlUrl();

    public String getUserName();

    public String getPassWord();
    
    public String getUnitName();
    
    public String getPreviewUrl();
    
    public Map<String, Object> getHttpParams();
    
    public void setHttpParams(Map<String, Object> httpParams);
    
    
}
