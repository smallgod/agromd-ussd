package com.agromarketday.ussd.datamodel;

import com.agromarketday.ussd.constant.MenuName;
import com.agromarketday.ussd.constant.MenuType;
import com.agromarketday.ussd.sharedInterface.Auditable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
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
@Table(name = "ag_ussd_menu", uniqueConstraints = @UniqueConstraint(columnNames = {"menu_name"}))
@Entity
@NamedQueries({
    @NamedQuery(name = AgUssdMenu.FETCH_BY_MENU_NAME, query = AgUssdMenu.FETCH_BY_MENU_NAME_QUERY)
})
public class AgUssdMenu extends BaseEntity implements Auditable, Serializable {

    private static final long serialVersionUID = -8680066482124886895L;

    //LEFT JOIN ensures that even if there are no elements in the set we are joining to, we still return AgUssdMenu object
    public static final String FETCH_BY_MENU_NAME_QUERY = "SELECT DISTINCT menu FROM AgUssdMenu menu LEFT JOIN menu.menuItems menuItems where menu.menuName=:menuName";
    public static final String FETCH_BY_MENU_NAME = "FETCH_BY_MENU_NAME";

    @Expose
    @SerializedName(value = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    @Column(name = "menu_title_text")
    private String menuTitleText;

    @Expose
    @SerializedName(value = "menu_name")
    @Column(name = "menu_name", nullable = false)
    @Enumerated(EnumType.STRING)
    private MenuName menuName;

    @Expose
    @SerializedName(value = "menu_type")
    @Column(name = "menu_type")
    @Enumerated(EnumType.STRING)
    private MenuType menuType;

    @Expose
    @SerializedName(value = "menu_items")
    @OneToMany(fetch = FetchType.EAGER)//LAZY works especially with HQL though with criteria it was throwing the exception  org.hibernate.LazyInitializationException: failed to lazily initialize a collection
    @JoinTable(name = "menu_items",//EAGER works with criteria but throws the infamous NullPointer exceptin while trying to execute the select query

            joinColumns = {
                @JoinColumn(name = "menu_name", referencedColumnName = "menu_name")

            },
            inverseJoinColumns = {
                @JoinColumn(name = "menu_item_code", referencedColumnName = "menu_item_code")
            }
    )
    @Cascade({CascadeType.ALL})
    private Set<AgMenuItemIndex> menuItems = new HashSet<>();

    public AgUssdMenu() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<AgMenuItemIndex> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(Set<AgMenuItemIndex> menuItems) {
        this.menuItems = menuItems;
    }

    public String getMenuTitleText() {
        return menuTitleText;
    }

    public void setMenuTitleText(String menuTitleText) {
        this.menuTitleText = menuTitleText;
    }

    @Override
    public String getUsername() {
        return this.getLastModifiedBy();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 97 * hash + Objects.hashCode(this.menuName);
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
        final AgUssdMenu other = (AgUssdMenu) obj;
        if (this.id != other.getId()) {
            return false;
        }
        return Objects.equals(this.menuName, other.getMenuName());
    }
    
    public MenuName getMenuName() {
        return menuName;
    }

    public void setMenuName(MenuName menuName) {
        this.menuName = menuName;
    }

    public MenuType getMenuType() {
        return menuType;
    }

    public void setMenuType(MenuType menuType) {
        this.menuType = menuType;
    }
}

//
//@Expose
//    @SerializedName(value = "child_nodes")
//    @OneToMany(fetch = FetchType.EAGER)//LAZY works especially with HQL though with criteria it was throwing the exception  org.hibernate.LazyInitializationException: failed to lazily initialize a collection
//    @JoinTable(name = "child_nodes",//EAGER works with criteria but throws the infamous NullPointer exceptin while trying to execute the select query
//
//            joinColumns = {
//                @JoinColumn(name = "parent_node", referencedColumnName = "current_node")
//
//            },
//            inverseJoinColumns = {
//                @JoinColumn(name = "child_node", referencedColumnName = "node_code")
//            }
//    )
//    @Cascade({CascadeType.ALL})
//    private Set<AgNodeIndex> menuItems = new HashSet<>();
