package com.agromarketday.ussd.datamodel;

import com.agromarketday.ussd.sharedInterface.Auditable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
@Entity
@DynamicUpdate(value = true)
@SelectBeforeUpdate(value = true)
@Table(name = "ag_menu_item_index", uniqueConstraints = @UniqueConstraint(columnNames = {"menu_item_code"}))

public class AgMenuItemIndex extends BaseEntity implements Auditable, Serializable {

    private static final long serialVersionUID = 2632496870661356836L;

    @Expose
    @SerializedName(value = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    
    @Expose
    @SerializedName(value = "menu_item_code")
    @Column(name = "menu_item_code", nullable = false)
    protected String menuItemCode; //e.g. 1001

    @Expose
    @SerializedName(value = "item_index")
    @Column(name = "item_index")
    protected int menuItemIndex; //e.g. 1

    /**
     * no-arg constructor
     *
     */
    public AgMenuItemIndex() {
    }

    /**
     * Constructor
     *
     * @param nodeName
     * @param nodeIndex
     */
    public AgMenuItemIndex(String nodeName, int nodeIndex) {
        this.menuItemCode = nodeName;
        this.menuItemIndex = nodeIndex;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the value of the menuItemCode property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getMenuItemCode() {
        return menuItemCode;
    }

    /**
     * Sets the value of the menuItemCode property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setMenuItemCode(String value) {
        this.menuItemCode = value;
    }

    /**
     * Gets the value of the menuItemIndex property.
     *
     */
    public int getMenuItemIndex() {
        return menuItemIndex;
    }

    /**
     * Sets the value of the menuItemIndex property.
     *
     * @param value
     */
    public void setMenuItemIndex(int value) {
        this.menuItemIndex = value;
    }

    @Override
    public String getUsername() {
        return this.getLastModifiedBy();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 53 * hash + Objects.hashCode(this.menuItemCode);
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
        final AgMenuItemIndex other = (AgMenuItemIndex) obj;
        if (this.id != other.getId()) {
            return false;
        }
        return Objects.equals(this.menuItemCode, other.getMenuItemCode());
    }

}
