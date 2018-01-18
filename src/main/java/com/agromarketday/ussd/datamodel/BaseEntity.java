/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agromarketday.ussd.datamodel;

import com.agromarketday.ussd.sharedInterface.DBInterface;
import com.google.gson.annotations.SerializedName;


import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.jadira.usertype.dateandtime.joda.PersistentLocalDate;
import org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime;
import org.jadira.usertype.dateandtime.joda.PersistentLocalTime;
import org.joda.time.LocalDateTime;

/**
 *
 * @author smallgod
 */
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
    ),
    @TypeDef(name = "jodalocaltime", typeClass = PersistentLocalTime.class,
            parameters = {
                @Parameter(value = "UTC", name = "databaseZone"),
                @Parameter(value = "UTC", name = "javaZone")
            }
    )
})
@MappedSuperclass
public class BaseEntity implements Serializable, DBInterface {

    private static final long serialVersionUID = -8171202186820119866L;

//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    @Column(name = "id", updatable = false, nullable = false)
//    @SerializedName(value = "id")
//    private long id;
    
    @Column(name = "created_by")
    @SerializedName(value = "created_by")
    private String createdBy;

    @Type(type = "jodalocaldatetime")
    @Column(name = "created_on")
    @SerializedName(value = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "last_modified_by")
    @SerializedName(value = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "date_last_modified")
    @Type(type = "jodalocaldatetime")
    @SerializedName(value = "date_last_modified")
    private LocalDateTime dateLastModified;

    @Column(name = "date_modified_history", length = 7000)
    @SerializedName(value = "date_modified_history")
    private String dateModifiedHistory; // '|' separated strings

    @Column(name = "modified_by_history", length = 7000)
    @SerializedName(value = "modified_by_history")
    private String modifiedByHistory; // '|' separated strings    

    @Column(name = "description")
    @SerializedName(value = "description")
    private String description;

    /**
     * Get Id of the Object
     *
     * @return
     */
//    public long getId() {
//        return id;
//    }
//
//    /**
//     * Set Id of the object
//     *
//     * @param id
//     */
//    public void setId(long id) {
//        this.id = id;
//    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getDateLastModified() {
        return dateLastModified;
    }

    public void setDateLastModified(LocalDateTime dateLastModified) {
        this.dateLastModified = dateLastModified;
    }

    public String getDateModifiedHistory() {
        return dateModifiedHistory;
    }

    public void setDateModifiedHistory(String dateModifiedHistory) {
        this.dateModifiedHistory = dateModifiedHistory;
    }

    public String getModifiedByHistory() {
        return modifiedByHistory;
    }

    public void setModifiedByHistory(String modifiedByHistory) {
        this.modifiedByHistory = modifiedByHistory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
