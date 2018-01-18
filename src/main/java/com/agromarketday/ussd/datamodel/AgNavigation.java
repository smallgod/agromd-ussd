package com.agromarketday.ussd.datamodel;

import com.agromarketday.ussd.sharedInterface.Auditable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.jadira.usertype.dateandtime.joda.PersistentLocalDate;
import org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime;

@TypeDefs({
    @TypeDef(name = "jodalocaldatetime", typeClass = PersistentLocalDateTime.class,
            parameters = {
                @Parameter(value = "UTC", name = "databaseZone"),
                @Parameter(value = "UTC", name = "javaZone")
            }
    ),
    @TypeDef(name = "jodalocaldate", typeClass = PersistentLocalDate.class,
            parameters = {
                @Parameter(value = "UTC", name = "databaseZone"),
                @Parameter(value = "UTC", name = "javaZone")
            }
    )
})

@DynamicUpdate(value = true)
@SelectBeforeUpdate(value = true)
@Table(name = "ag_navigation",
        uniqueConstraints = @UniqueConstraint(columnNames = {"msisdn"}))
@Entity
@NamedQueries({
    @NamedQuery(name = AgNavigation.FETCH_NAVIG_BY_MSISDN,
            query = AgNavigation.FETCH_NAVIG_BY_MSISDN_QUERY),
    @NamedQuery(name = AgNavigation.FETCH_NAVIG_BY_SESSIONID,
            query = AgNavigation.FETCH_NAVIG_BY_SESSIONID_QUERY)
})
public class AgNavigation extends BaseEntity implements Auditable, Serializable {

    public static final String FETCH_NAVIG_BY_MSISDN_QUERY
            = "SELECT DISTINCT navig FROM AgNavigation navig INNER JOIN "
            + "navig.client client where client.msisdn=:msisdn";

    public static final String FETCH_NAVIG_BY_MSISDN = "FETCH_NAVIG_BY_MSISDN";

    public static final String FETCH_NAVIG_BY_SESSIONID_QUERY
            = "SELECT DISTINCT navig FROM AgNavigation navig INNER JOIN "
            + "navig.client client where navig.sessionId=:sessionId";

    public static final String FETCH_NAVIG_BY_SESSIONID
            = "FETCH_NAVIG_BY_MSISDN_SESSIONID";

    private static final long serialVersionUID = -712432774470329834L;

    @Expose
    @SerializedName(value = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    @Column(name = "session_id")
    private String sessionId; //Transaction Id from aggregator

    @Column(name = "menu_history", columnDefinition = "text")
    private String menuHistory;

    @Column(name = "categories_data", columnDefinition = "text")
    private String allCategoriesData;

    @Column(name = "category_navigation", columnDefinition = "text")
    private String categoryNav;

    @Column(name = "sub_category_navigation", columnDefinition = "text")
    private String subCategoryNav;

    @Column(name = "market_info", columnDefinition = "text")
    private String allMarketInfo;

    @Column(name = "district_markets", columnDefinition = "text")
    private String districtMarketsNav;

    @Column(name = "markets", columnDefinition = "text")
    private String marketsNav;

    @Column(name = "market_prices", columnDefinition = "text")
    private String marketPriceNav;

    @Column(name = "sellers_info", columnDefinition = "text")
    private String allSellersInfo;

    @Column(name = "district_sellers", columnDefinition = "text")
    private String districtSellersNav;

    @Column(name = "sellers", columnDefinition = "text")
    private String sellersNav;
    
    
    @Column(name = "buyers_info", columnDefinition = "text")
    private String allBuyersInfo;

    @Column(name = "matched_products", columnDefinition = "text")
    private String matchedProductsNav;
    
    @Column(name = "matched_buyers", columnDefinition = "text")
    private String matchedBuyersNav;

    @Expose
    @SerializedName(value = "msisdn")
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumns({
        @JoinColumn(name = "msisdn", referencedColumnName = "msisdn")
    })
    @Cascade(CascadeType.ALL)
    private AgClient client;

    public AgNavigation() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public AgClient getClient() {
        return client;
    }

    public void setClient(AgClient client) {
        this.client = client;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getMenuHistory() {
        return menuHistory;
    }

    public void setMenuHistory(String menuHistory) {
        this.menuHistory = menuHistory;
    }

    public String getCategoryNav() {
        return categoryNav;
    }

    public void setCategoryNav(String categoryNav) {
        this.categoryNav = categoryNav;
    }

    public String getSubCategoryNav() {
        return subCategoryNav;
    }

    public void setSubCategoryNav(String subCategoryNav) {
        this.subCategoryNav = subCategoryNav;
    }

    public String getAllCategoriesData() {
        return allCategoriesData;
    }

    public void setAllCategoriesData(String allCategoriesData) {
        this.allCategoriesData = allCategoriesData;
    }

    public String getAllMarketInfo() {
        return allMarketInfo;
    }

    public void setAllMarketInfo(String allMarketInfo) {
        this.allMarketInfo = allMarketInfo;
    }

    public String getDistrictMarketsNav() {
        return districtMarketsNav;
    }

    public void setDistrictMarketsNav(String districtMarketsNav) {
        this.districtMarketsNav = districtMarketsNav;
    }

    public String getMarketsNav() {
        return marketsNav;
    }

    public void setMarketsNav(String marketsNav) {
        this.marketsNav = marketsNav;
    }

    public String getMarketPriceNav() {
        return marketPriceNav;
    }

    public void setMarketPriceNav(String marketPriceNav) {
        this.marketPriceNav = marketPriceNav;
    }
    
    

    @Override
    public String getUsername() {
        return this.getLastModifiedBy();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 17 * hash + Objects.hashCode(this.client);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AgNavigation other = (AgNavigation) obj;
        if (this.id != other.getId()) {
            return false;
        }
        return Objects.equals(this.client, other.getClient());
    }

    public String getAllSellersInfo() {
        return allSellersInfo;
    }

    public void setAllSellersInfo(String allSellersInfo) {
        this.allSellersInfo = allSellersInfo;
    }

    public String getDistrictSellersNav() {
        return districtSellersNav;
    }

    public void setDistrictSellersNav(String districtSellersNav) {
        this.districtSellersNav = districtSellersNav;
    }

    public String getSellersNav() {
        return sellersNav;
    }

    public void setSellersNav(String sellersNav) {
        this.sellersNav = sellersNav;
    }

    public String getAllBuyersInfo() {
        return allBuyersInfo;
    }

    public void setAllBuyersInfo(String allBuyersInfo) {
        this.allBuyersInfo = allBuyersInfo;
    }

    public String getMatchedProductsNav() {
        return matchedProductsNav;
    }

    public void setMatchedProductsNav(String matchedProductsNav) {
        this.matchedProductsNav = matchedProductsNav;
    }

    public String getMatchedBuyersNav() {
        return matchedBuyersNav;
    }

    public void setMatchedBuyersNav(String matchedBuyersNav) {
        this.matchedBuyersNav = matchedBuyersNav;
    }

}
