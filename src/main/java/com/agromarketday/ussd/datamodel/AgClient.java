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
@Table(name = "ag_client", uniqueConstraints = @UniqueConstraint(columnNames = {"msisdn"}))
@Entity
@NamedQueries({
    @NamedQuery(name = AgClient.FETCH_CLIENT_MSISDN, query = AgClient.FETCH_CLIENT_MSISDN_QUERY)
})
public class AgClient extends BaseEntity implements Auditable, Serializable {

    public static final String FETCH_CLIENT_MSISDN_QUERY = "SELECT DISTINCT client FROM AgClient client INNER JOIN client.language language where client.msisdn=:msisdn";
    public static final String FETCH_CLIENT_MSISDN = "FETCH_CLIENT_MSISDN";
    private static final long serialVersionUID = -3327891616036866994L;

    @Expose
    @SerializedName(value = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    @Column(name = "msisdn", nullable = false)
    private String msisdn;

    @Column(name = "name")
    private String name;

    @Column(name = "district")
    private String district;

    @Column(name = "is_registered")
    private boolean isRegistered;

    @Expose
    @SerializedName(value = "language_code")
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumns({
        @JoinColumn(name = "language_code", referencedColumnName = "language_code")
    })
    @Cascade(CascadeType.ALL)
    private AgLanguage language;

//    @Expose
//    @SerializedName(value = "navigation_id")
//    @OneToOne(fetch = FetchType.EAGER)
//    @JoinColumns({
//        @JoinColumn(name = "navigation_id", referencedColumnName = "id")
//    })
//    @Cascade(CascadeType.ALL)
//    private AgNavigation clientNavigation;
    public AgClient() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public AgLanguage getLanguage() {
        return language;
    }

    public void setLanguage(AgLanguage language) {
        this.language = language;
    }

    @Override
    public String getUsername() {
        return this.getLastModifiedBy();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 13 * hash + Objects.hashCode(this.msisdn);
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
        final AgClient other = (AgClient) obj;
        if (this.id != other.getId()) {
            return false;
        }
        return Objects.equals(this.msisdn, other.getMsisdn());
    }

    public boolean isIsRegistered() {
        return isRegistered;
    }

    public void setIsRegistered(boolean isRegistered) {
        this.isRegistered = isRegistered;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

}
