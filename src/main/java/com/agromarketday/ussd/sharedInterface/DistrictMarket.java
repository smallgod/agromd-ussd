package com.agromarketday.ussd.sharedInterface;

import com.agromarketday.ussd.datamodel.json.MarketPriceResponse;
import com.agromarketday.ussd.datamodel.json.MarketPriceResponse2;
import java.util.Set;

/**
 *
 * @author smallgod
 */
public interface DistrictMarket extends MenuItem {

    @Override
    public int getId();

    @Override
    public String getName();

    public Set<MarketPriceResponse2.Data.District.Product> getProducts();

}
