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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.jadira.usertype.dateandtime.joda.PersistentLocalDate;
import org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime;
import org.joda.time.LocalDateTime;

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
@Table(name = "ag_session",
        uniqueConstraints = @UniqueConstraint(columnNames = {"session_id"})
)
@Entity
@NamedQueries({
    @NamedQuery(name = AgSession.FETCH_SESSION_BY_MSISDN,
            query = AgSession.FETCH_SESSION_BY_MSISDN_QUERY),
    @NamedQuery(name = AgSession.FETCH_SESSION_BY_SESSIONID,
            query = AgSession.FETCH_SESSION_BY_SESSIONID_QUERY),
    @NamedQuery(name = AgSession.FETCH_SESSION_BY_MSISDN_DATES,
            query = AgSession.FETCH_SESSION_BY_MSISDN_DATES_QUERY),
    @NamedQuery(name = AgSession.FETCH_SESSION_BY_DATES,
            query = AgSession.FETCH_SESSION_BY_DATES_QUERY)
})
public class AgSession extends BaseEntity implements Auditable, Serializable {

    public static final String FETCH_SESSION_BY_MSISDN_QUERY
            = "SELECT DISTINCT session FROM AgSession session INNER JOIN "
            + "session.client client where client.msisdn=:msisdn";

    public static final String FETCH_SESSION_BY_MSISDN_DATES_QUERY
            = "SELECT DISTINCT session FROM AgSession session INNER JOIN "
            + "session.client client where client.msisdn=:msisdn "
            + "AND session.sessionStartTime BETWEEN :startDate AND :endDate";

    public static final String FETCH_SESSION_BY_DATES_QUERY
            = "SELECT DISTINCT session FROM AgSession session where "
            + "session.sessionStartTime BETWEEN :startDate AND :endDate";

    public static final String FETCH_SESSION_BY_SESSIONID_QUERY
            = "SELECT DISTINCT session FROM AgSession session INNER JOIN "
            + "session.client client where session.sessionId=:sessionId";

    public static final String FETCH_SESSION_BY_MSISDN = "FETCH_SESSION_BY_MSISDN";

    public static final String FETCH_SESSION_BY_SESSIONID
            = "FETCH_SESSION_BY_MSISDN_SESSIONID";

    public static final String FETCH_SESSION_BY_MSISDN_DATES
            = "FETCH_SESSION_BY_MSISDN_DATES";

    public static final String FETCH_SESSION_BY_DATES = "FETCH_SESSION_BY_DATES";

    private static final long serialVersionUID = -7577926110554476915L;

    @Expose
    @SerializedName(value = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    @Column(name = "session_id")
    private String sessionId; //Transaction Id from aggregator

    @Type(type = "jodalocaldatetime")
    @Column(name = "session_start_time")
    @SerializedName(value = "session_start_time")
    private LocalDateTime sessionStartTime;

    @Type(type = "jodalocaldatetime")
    @Column(name = "session_end_time")
    @SerializedName(value = "session_end_time")
    private LocalDateTime sessionEndTime;

    @Column(name = "menu_history", columnDefinition = "text")
    private String menuHistory;

    @Expose
    @SerializedName(value = "msisdn")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({
        @JoinColumn(name = "msisdn", referencedColumnName = "msisdn")
    })
    @Cascade(CascadeType.ALL)
    private AgClient client;

    public AgSession() {
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

    public LocalDateTime getSessionStartTime() {
        return sessionStartTime;
    }

    public void setSessionStartTime(LocalDateTime sessionStartTime) {
        this.sessionStartTime = sessionStartTime;
    }

    public LocalDateTime getSessionEndTime() {
        return sessionEndTime;
    }

    public void setSessionEndTime(LocalDateTime sessionEndTime) {
        this.sessionEndTime = sessionEndTime;
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
        final AgSession other = (AgSession) obj;
        if (this.id != other.getId()) {
            return false;
        }
        return Objects.equals(this.client, other.getClient());
    }
}
