package com.agromarketday.ussd.sharedInterface;

import java.util.Set;

/**
 *
 * @author smallgod
 */
public interface HasChildrenItems extends MenuItem {

    @Override
    public String getId();

    @Override
    public String getName();
    
    
    public String getExtra();

    public Set<? extends MenuItem> getChildrenItems();
    
}
