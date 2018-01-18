package com.agromarketday.ussd.datamodel;

import com.agromarketday.ussd.constant.BuyerLocation;
import com.agromarketday.ussd.constant.ItemTag;
import com.agromarketday.ussd.constant.Region;
import com.agromarketday.ussd.constant.TransportArea;
import com.agromarketday.ussd.constant.UssdFunction;
import com.agromarketday.ussd.sharedInterface.Auditable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.DynamicUpdate;
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
@Table(name = "ag_product")
@Entity
public class AgProduct extends BaseEntity implements Auditable, Serializable {

    private static final long serialVersionUID = -712432774470329834L;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Expose
    @SerializedName(value = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    @Column(name = "session_Id")
    private String sessionId;

    @Column(name = "user_contact")
    private String userContact;

    @Column(name = "category_id")
    private int categoryId;

    @Column(name = "sub_category_id")
    private int subCategoryId;

    @Column(name = "product_id")
    private int productId;

    @Column(name = "tag")
    @Enumerated(EnumType.STRING)
    private ItemTag tag;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "item_description")
    private String itemDescription;

    @Column(name = "buyer_id")
    private int buyerId;

    @Column(name = "buyer_name")
    private String buyerName;

    @Column(name = "seller_id")
    private int sellerId;

    @Column(name = "seller_name")
    private String sellerName; // in case of contacting sellers

    @Column(name = "quantity")
    private String quantity;

    @Column(name = "seller_price")
    private int sellerPrice;

    @Column(name = "buyer_price")
    private int buyerPrice;

    @Column(name = "measure_unit")
    private String measureUnit = "KG";

    @Expose
    @SerializedName(value = "transport_area")
    @Column(name = "transport_area")
    @Enumerated(EnumType.STRING)
    private TransportArea transportArea;

    @Expose
    @SerializedName(value = "region")
    @Column(name = "region")
    @Enumerated(EnumType.STRING)
    private Region region;

    @Column(name = "district_id")
    private int districtId;

    @Column(name = "district")
    private String districtName;

    @Column(name = "market_id")
    private int marketId;

    @Column(name = "market_name")
    private String marketName;

    @Column(name = "place")
    private String place;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "is_submitted")
    private boolean isSubmitted;

    @Column(name = "matched_buyer")
    private boolean isMatched;

    @Expose
    @SerializedName(value = "ussd_function")
    @Column(name = "ussd_function")
    @Enumerated(EnumType.STRING)
    private UssdFunction ussdFunction;

    @Expose
    @SerializedName(value = "buyer_location")
    @Column(name = "buyer_location")
    @Enumerated(EnumType.STRING)
    private BuyerLocation buyerLocation;

    public AgProduct() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserContact() {
        return userContact;
    }

    public void setUserContact(String userContact) {
        this.userContact = userContact;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }

    public boolean isIsSubmitted() {
        return isSubmitted;
    }

    public void setIsSubmitted(boolean isSubmitted) {
        this.isSubmitted = isSubmitted;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String getUsername() {
        return this.getLastModifiedBy();
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public int getSellerPrice() {
        return sellerPrice;
    }

    public void setSellerPrice(int sellerPrice) {
        this.sellerPrice = sellerPrice;
    }

    public int getBuyerPrice() {
        return buyerPrice;
    }

    public void setBuyerPrice(int buyerPrice) {
        this.buyerPrice = buyerPrice;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(int subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public TransportArea getTransportArea() {
        return transportArea;
    }

    public void setTransportArea(TransportArea transportArea) {
        this.transportArea = transportArea;
    }

    public UssdFunction getUssdFunction() {
        return ussdFunction;
    }

    public void setUssdFunction(UssdFunction ussdFunction) {
        this.ussdFunction = ussdFunction;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (int) (this.id ^ (this.id >>> 32));
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
        final AgProduct other = (AgProduct) obj;
        return other.getId() == this.id;
    }

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    public int getMarketId() {
        return marketId;
    }

    public void setMarketId(int marketId) {
        this.marketId = marketId;
    }

    public ItemTag getTag() {
        return tag;
    }

    public void setTag(ItemTag tag) {
        this.tag = tag;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getMeasureUnit() {
        return measureUnit;
    }

    public void setMeasureUnit(String measureUnit) {
        this.measureUnit = measureUnit;
    }

    public boolean isIsMatched() {
        return isMatched;
    }

    public void setIsMatched(boolean isMatched) {
        this.isMatched = isMatched;
    }

    public BuyerLocation getBuyerLocation() {
        return buyerLocation;
    }

    public void setBuyerLocation(BuyerLocation buyerLocation) {
        this.buyerLocation = buyerLocation;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

}
