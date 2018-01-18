package com.agromarketday.ussd.database;

import com.agromarketday.ussd.config.HibernateConfig;
import com.agromarketday.ussd.constant.ErrorCode;
import com.agromarketday.ussd.constant.FetchStatus;
import com.agromarketday.ussd.constant.NamedConstants;
import com.agromarketday.ussd.datamodel.BaseEntity;
import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.logger.LoggerUtil;
import com.agromarketday.ussd.sharedInterface.DBInterface;
import com.agromarketday.ussd.util.GeneralUtils;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.naming.NamingException;
import javax.persistence.TypedQuery;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.joda.time.LocalDate;

/**
 *
 * @author smallgod
 */
public final class CustomHibernate {

    private static final LoggerUtil LOGGER = new LoggerUtil(CustomHibernate.class);
    private static String hibernateFilePath;
    private final HibernateConfig hibernateConfig;
    private SessionFactory sessionFactory;

    public CustomHibernate(HibernateConfig hibernateConfig) {
        this.hibernateConfig = hibernateConfig;
        CustomHibernate.hibernateFilePath = hibernateConfig.getHibernateFilePath();

    }

    private SessionFactory getSessionFactory() {

        if (sessionFactory == null) {
            sessionFactory = ConfigureHibernate.getInstance().createSessionFactory();
        }

        return sessionFactory;
    }

    /**
     * Explicitly initiate the DB resources
     *
     * @return
     */
    public boolean initialiseDBResources() {

        boolean initialised = Boolean.TRUE;

        sessionFactory = ConfigureHibernate.getInstance().createSessionFactory();

        if (sessionFactory == null) {
            initialised = Boolean.FALSE;
        }

        return initialised;
    }

    /**
     * Close the hibernate session factory after use
     */
    public void releaseDBResources() {

        if (getSessionFactory() != null && !getSessionFactory().isClosed()) {
            getSessionFactory().close();

            LOGGER.debug("Closing Hibernate SessionFactory...");
        } else {
            LOGGER.debug(">>>>>> called closeHibernateSessionFactory() but SessionFactory already CLOSED!!!!");
        }
    }

    //Session methods
    private Session getSession() {

        Session session = null;

        try {

            session = getSessionFactory().getCurrentSession();

            if (!session.isOpen()) {
                session = getSessionFactory().openSession();
            }

        } catch (HibernateException he) {
            LOGGER.error("Hibernate exception: " + he.toString());

        }
        return session;
    }

    private StatelessSession getStatelessSession() throws NullPointerException {

        StatelessSession statelessSession;

        try {
            statelessSession = getSessionFactory().openStatelessSession();
            LOGGER.debug("openned stateless session");
        } catch (HibernateException he) {
            LOGGER.error("Hibernate exception openning stateless session: " + he.toString());
            throw new NullPointerException("Could not create open a statelesssession");
        }
        return statelessSession;
    }

    private static void closeSession(Session session) {

        LOGGER.warn("Closing session..");

        if (session != null) {

            try {

                if (session.isConnected()) {
                    session.disconnect();
                }
                if (session.isOpen()) {
                    session.close();
                }

            } catch (HibernateException hbe) {
                LOGGER.error("Couldn't close Session: " + hbe.toString());
            }
        }
    }

    private static void closeSession(StatelessSession statelessSession) {

        if (statelessSession != null) {
            statelessSession.close();
        }
    }

    //CRUD methods
    /**
     * Method supports a callback function that can process multiple records
     * while saving in the database
     *
     * @param callBack
     * @return number of records processed and saved
     * @throws com.library.customexception.MyCustomException
     */
    public int processAndSave(CallBack callBack) throws MyCustomException {

        StatelessSession tempSession = getStatelessSession();
        Transaction transaction = null;
        String errorDetails;

        try {

            transaction = tempSession.beginTransaction();

            int recordsProcessed = callBack.processAndSaveMultipleRecords(tempSession);

            transaction.commit();

            return recordsProcessed;

        } catch (HibernateException he) {

            errorDetails = "hibernate exception inserting/updating records in the database: " + he.toString();

            if (transaction != null) {
                transaction.rollback();
            }
        } catch (Exception e) {

            errorDetails = "General exception performing the processAndSave callback function that saves/updates records in the database: " + e.getCause().toString();

            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            closeSession(tempSession);
        }

        MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
        throw error;
    }

    /**
     * Insert a list of entity records
     *
     * @param entityList to insert
     * @return if entity record has been inserted/saved
     * @throws com.library.customexception.MyCustomException
     */
    public boolean insertBulk(Set<DBInterface> entityList) throws MyCustomException {

        StatelessSession tempSession = getStatelessSession();
        Transaction transaction = null;
        String errorDetails;

        try {

            transaction = tempSession.beginTransaction();
            for (DBInterface entity : entityList) {
                tempSession.insert(entity);
            }
            transaction.commit();
            return Boolean.TRUE;

        } catch (HibernateException he) {

            errorDetails = "hibernate exception saving records in the database: " + he.toString();

            if (transaction != null) {
                transaction.rollback();
            }
        } catch (Exception e) {

            errorDetails = "General exception saving records in the database: " + e.toString();

            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            closeSession(tempSession);
        }

        MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
        throw error;
    }

    /**
     * Save a list of entity records while flushing a batch of records at a time
     * (to release memory)
     *
     * @param <BaseEntity>
     * @param entityList to save
     * @return
     * @throws com.library.customexception.MyCustomException
     */
    public <BaseEntity> boolean saveBulk(Set<BaseEntity> entityList) throws MyCustomException {

        int insertCount = 0;

        Session session = getSession();
        Transaction transaction = null;
        String errorDetails;

        try {

            transaction = session.beginTransaction();
            for (BaseEntity entity : entityList) {

                session.save(entity);

                if ((insertCount % NamedConstants.HIBERNATE_JDBC_BATCH) == 0) { // Same as the JDBC batch size
                    //flush a batch of inserts and release memory: Without the call to the flush method,
                    //your first-level cache would throw an OutOfMemoryException
                    session.flush();
                    session.clear();
                }

                insertCount++;
            }

            transaction.commit();
            return Boolean.TRUE;

        } catch (HibernateException he) {

            LOGGER.error(he.toString());

            errorDetails = "hibernate exception saving records in the database: " + he.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } catch (Exception e) {

            LOGGER.error(e.toString());

            errorDetails = "hibernate exception saving records in the database: " + e.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } finally {

            closeSession(session);
        }

        MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
        throw error;
    }

    /**
     * Save a list of entity records while flushing a batch of records at a time
     * (to release memory)
     *
     * @param <BaseEntity>
     * @param entityList to save
     * @return
     * @throws com.library.customexception.MyCustomException
     */
    public <BaseEntity> boolean saveOrUpdateBulk(Set<BaseEntity> entityList) throws MyCustomException {

        int insertCount = 0;

        Session session = getSession();
        Transaction transaction = null;
        String errorDetails;

        try {

            transaction = session.beginTransaction();
            for (BaseEntity entity : entityList) {

                session.saveOrUpdate(entity);

                if ((insertCount % NamedConstants.HIBERNATE_JDBC_BATCH) == 0) { // Same as the JDBC batch size
                    //flush a batch of inserts and release memory: Without the call to the flush method,
                    //your first-level cache would throw an OutOfMemoryException
                    session.flush();
                    session.clear();
                }

                insertCount++;
            }

            transaction.commit();
            return Boolean.TRUE;

        } catch (HibernateException he) {

            LOGGER.error(he.toString());

            errorDetails = "hibernate exception saving records in the database: " + he.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } catch (Exception e) {

            LOGGER.error(e.toString());

            errorDetails = "hibernate exception saving records in the database: " + e.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } finally {

            closeSession(session);
        }

        MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
        throw error;
    }

    /**
     *
     * @param namedQuery
     * @param parameterName
     * @param parameterValue
     * @throws com.library.customexception.MyCustomException
     */
    public void deleteRecords(String namedQuery, String parameterName, Object parameterValue) throws MyCustomException {

        Session session = getSession();
        Transaction transaction = null;
        String errorDetails;

        String queryString = "";
        try {

            // Query updateQuery = session.createQuery("from Stock where stockCode = :code ");
            //query.setParameter("code", "7277");
            //List list = updateQuery.list();
            transaction = session.beginTransaction();
            Query query = session.getNamedQuery(namedQuery);

            queryString = query.getQueryString();

            LOGGER.debug("Parameter Name: " + parameterName + ", and value: " + parameterValue);

            switch (parameterName) {

                case "displayDate":
                    //LocalDate paymentId = DateUtils.convertStringToLocalDate((String) parameterValue, NamedConstants.DATE_DASH_FORMAT);
                    LocalDate date = new LocalDate(parameterValue);
                    query.setParameter(parameterName, date);

                    break;

                case "id":
                    long val = GeneralUtils.convertObjectToLong(parameterValue);
                    query.setParameter(parameterName, val);
                    break;

                default:
                    query.setParameter(parameterName, parameterValue);
                    break;
            }

            query.executeUpdate();

            transaction.commit();

            return;

        } catch (HibernateException he) {

            errorDetails = "HibernateException occurred trying to delete records with query: " + queryString + " - " + he.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } catch (Exception e) {

            errorDetails = "General exception occurred trying to delete records with query: " + queryString + " - " + e.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } finally {
            closeSession(session);
        }

        MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
        throw error;
    }

    /**
     * Fetch entities (AdText, AdResources, AdPrograms) from the database using
     * named queries
     *
     * @param <BaseEntity>
     * @param namedQuery
     * @param propertyNameValues
     *
     * @return
     * @throws com.agromarketday.ussd.exception.MyCustomException
     */
    public <BaseEntity> Set<BaseEntity> fetchEntities(String namedQuery, Map<String, Object> propertyNameValues) throws MyCustomException {

        Session session = getSession();
        Transaction transaction = null;

        String queryString = "";
        String errorDetails = "";
        Set<BaseEntity> results = new HashSet<>();
        boolean isError = Boolean.TRUE;

        try {

            // Query updateQuery = session.createQuery("from Stock where stockCode = :code ");
            //query.setParameter("code", "7277");
            //List list = updateQuery.list();
            transaction = session.beginTransaction();

            Query<BaseEntity> query = session.getNamedQuery(namedQuery);

//            TypedQuery<Admin> updateQuery = session.createQuery("FROM Admin");
//    List<Admin> result = updateQuery.getResultList();
            queryString = query.getQueryString();

            //propertyNameValues.entrySet().stream().forEach((entry) -> {
            for (Map.Entry<String, Object> entry : propertyNameValues.entrySet()) {

                String name = entry.getKey();
                Set<Object> values = (Set<Object>) entry.getValue();

                LOGGER.debug("Field Name  : " + name);
                LOGGER.debug("Field values: " + values);

                switch (name) {

                    case "campaignStatuses": {
//                        Set<CampaignStatus> statuses = new HashSet<>();
//                        for (Object object : values) {
//
//                            //CampaignStatus val = CampaignStatus.valueOf((String) object);
//                            CampaignStatus val = (CampaignStatus) object;
//                            statuses.add(val);
//                        }
                        //query.setParameterList(name, statuses);
                        query.setParameterList(name, new HashSet<>());
                        break;
                    }

                    case "screenIds":
                        Set<String> screenCodes = new HashSet<>();
                        for (Object object : values) {

                            screenCodes.add((String) object);
                        }
                        query.setParameterList(name, screenCodes);
                        break;

                    case "ispreferred":
                        Set<Boolean> preferred = new HashSet<>();
                        for (Object object : values) {

                            preferred.add((Boolean) object);
                        }
                        query.setParameterList(name, preferred);
                        break;

                    case "campaignId":
                        Set<Integer> campaignIds = new HashSet<>();
                        for (Object object : values) {

                            campaignIds.add(GeneralUtils.convertObjectToInteger(object));
                        }
                        query.setParameterList(name, campaignIds);
                        break;

                    case "internalPaymentID":
                        Set<String> paymentIds = new HashSet<>();
                        for (Object object : values) {

                            paymentIds.add((String) object);
                        }
                        query.setParameterList(name, paymentIds);
                        break;

                    case "uploadId":
                        Set<String> uploadIds = new HashSet<>();
                        for (Object object : values) {

                            uploadIds.add((String) object);
                        }
                        query.setParameterList(name, uploadIds);
                        break;

                    case "userId":
                        Set<String> userId = new HashSet<>();
                        for (Object object : values) {

                            userId.add(String.valueOf(object));
                        }
                        query.setParameterList(name, userId);
                        break;

                    case "displayDate":
                        Set<LocalDate> displayDates = new HashSet<>();
                        for (Object object : values) {

                            LocalDate date = new LocalDate(object);
                            displayDates.add(date);
                        }
                        query.setParameterList(name, displayDates);
                        break;

                    case "id":
                        Set<Long> ids = new HashSet<>();
                        for (Object object : values) {

                            long val = GeneralUtils.convertObjectToLong(object);
                            ids.add(val);
                        }
                        query.setParameterList(name, ids);
                        break;

                    case "isUploadedToDSM": {
                        Set<Boolean> vals = new HashSet<>();
                        for (Object object : values) {

                            boolean val = (Boolean) object;
                            vals.add(val);
                        }
                        query.setParameterList(name, vals);
                        break;
                    }
                    case "fetchStatus": {
//                        Set<FetchStatus> statuses = new HashSet<>();
//                        for (Object object : values) {
//                            FetchStatus val = FetchStatus.convertToEnum((String) object);
//                            statuses.add(val);
//                        }
                        //query.setParameterList(name, statuses);
                        query.setParameterList(name, new HashSet<>());
                        break;
                    }

                    case "audienceTypes.id":

                        Set<Long> vals = new HashSet<>();

                        for (Object object : values) {

                            long val = GeneralUtils.convertObjectToLong(object);
                            vals.add(val);
                        }

//                    query.createAlias("audienceTypes", "audtype") //without this, keeps throwing error - org.hibernate.QueryException: could not resolve property: audienceTypes.audienceCode of: com.library.datamodel.model.v1_0.AdScreen 
//                            .add(Restrictions.in("audtype.id", vals));
                        query.setParameterList("id", vals);
                        break;

                    default:
                        query.setParameterList(name, values);
                        break;
                }

            }

            results = new HashSet<>(query.list());

            transaction.commit();

            isError = Boolean.FALSE;

        } catch (HibernateException he) {

            he.printStackTrace();
            errorDetails = "HibernateException occurred trying to execute query: " + queryString + " - " + he.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } catch (Exception ex) {

            ex.printStackTrace();
            errorDetails = "General exception occurred trying to execute query: " + queryString + " - " + ex.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } finally {
            closeSession(session);
        }

        if (isError) {

            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
            throw error;

        }

        return results;

    }

    /**
     *
     * @param <BaseEntity>
     * @param namedQuery
     * @param parameterName
     * @param parameterValue
     * @return
     * @throws com.library.customexception.MyCustomException
     */
    public <BaseEntity> Set<BaseEntity> fetchEntities(String namedQuery, String parameterName, Object parameterValue) throws MyCustomException {

        Session session = getSession();
        Transaction transaction = null;

        String queryString = "";
        Set<BaseEntity> results = new HashSet<>();
        boolean isError = Boolean.TRUE;
        String errorDetails = "";

        try {

            // Query updateQuery = session.createQuery("from Stock where stockCode = :code ");
            //query.setParameter("code", "7277");
            //List list = updateQuery.list();
            transaction = session.beginTransaction();
            Query query = session.getNamedQuery(namedQuery);

            queryString = query.getQueryString();

            LOGGER.debug("Parameter Name : " + parameterName);
            LOGGER.debug("Parameter value: " + parameterValue);

            if (parameterValue instanceof Set) {
                query.setParameterList(parameterName, (Collection) parameterValue);

            } else {
                //query.setParameter(parameterName, parameterValue);

                switch (parameterName) {

                    case "area":
                        String area = String.valueOf(parameterValue);
                        query.setParameter(parameterName, area);
                        break;

                    case "screenId":
                        String screenId = String.valueOf(parameterValue);
                        query.setParameter(parameterName, screenId);
                        break;

                    case "uploadId":
                        String uploadId = String.valueOf(parameterValue);
                        query.setParameter(parameterName, uploadId);
                        break;

                    case "campaignId":
                        int campaignId = GeneralUtils.convertObjectToInteger(parameterValue);
                        query.setParameter(parameterName, campaignId);
                        break;

                    case "internalPaymentID":
                        String paymentId = String.valueOf(parameterValue);
                        query.setParameter(parameterName, paymentId);
                        break;

                    case "displayDate":
                        LocalDate date = new LocalDate(parameterValue);
                        query.setParameter(parameterName, date);
                        break;

                    case "id":
                        long id = GeneralUtils.convertObjectToLong(parameterValue);
                        query.setParameter(parameterName, id);
                        break;

                    case "userId":
                        String userId = String.valueOf(parameterValue);
                        query.setParameter(parameterName, userId);
                        break;

                    default:
                        query.setParameter(parameterName, parameterValue);
                        break;
                }

            }

            results = new HashSet<>(query.list());
            transaction.commit();
            isError = Boolean.FALSE;

        } catch (HibernateException he) {

            he.printStackTrace();

            errorDetails = "HibernateException occurred trying to execute query: " + queryString + " => " + he.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } catch (NullPointerException ex) {

            errorDetails = "NullPointerException occurred trying to execute query: " + queryString + " => " + ex.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } catch (Exception ex) {

            errorDetails = "General exception occurred trying to execute query: " + queryString + " => " + ex.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } finally {
            closeSession(session);
        }

        if (isError) {

            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
            throw error;

        }

        return results;

    }

    /**
     *
     * @param <BaseEntity>
     * @param namedQuery
     * @return
     * @throws MyCustomException
     */
    public <BaseEntity> Set<BaseEntity> fetchEntities(String namedQuery) throws MyCustomException {

        Session session = getSession();
        Transaction transaction = null;

        String queryString = "";
        boolean isError = Boolean.TRUE;
        String errorDetails = "";
        Set<BaseEntity> results = new HashSet<>();

        try {

            // Query updateQuery = session.createQuery("from Stock where stockCode = :code ");
            //query.setParameter("code", "7277");
            //List list = updateQuery.list();
            transaction = session.beginTransaction();
            Query query = session.getNamedQuery(namedQuery);

            queryString = query.getQueryString();

            results = new HashSet<>(query.list());

            transaction.commit();

            isError = Boolean.FALSE;

        } catch (HibernateException he) {

            errorDetails = "HibernateException occurred trying to execute query: " + queryString + " - " + he.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } catch (Exception ex) {

            errorDetails = "General exception occurred trying to execute query: " + queryString + " - " + ex.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } finally {
            closeSession(session);
        }

        if (isError) {

            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
            throw error;

        }

        return results;

    }

    /**
     * Save an entity record to a database
     *
     * @param entity to save
     * @return Database ID of saved object
     * @throws com.library.customexception.MyCustomException
     */
    public Object saveEntity(DBInterface entity) throws MyCustomException {

        Session session = getSession();
        Transaction transaction = null;
        String errorDetails = "";
        Object entityId = null;
        boolean isError = Boolean.TRUE;

        try {

            transaction = session.beginTransaction();
            entityId = session.save(entity);
            transaction.commit();
            isError = Boolean.FALSE;

        } catch (HibernateException he) {

            errorDetails = "HibernateException occurred trying to save entity: " + entity.getClass() + " - " + he.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } catch (Exception ex) {

            ex.printStackTrace();

            errorDetails = "General exception occurred trying to save entity: " + entity.getClass() + " - " + ex.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } finally {
            closeSession(session);
        }

        if (isError) {
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
            throw error;
        }

        return entityId;

    }

    /**
     * Save an entity record to a database
     *
     * @param entity to save
     * @throws com.agromarketday.ussd.exception.MyCustomException
     */
    public void saveOrUpdateEntity(DBInterface entity) throws MyCustomException {

        Session session = getSession();
        Transaction transaction = null;
        String errorDetails = "";
        boolean isError = Boolean.TRUE;

        try {

            transaction = session.beginTransaction();
            session.saveOrUpdate(entity);
            session.flush();
            session.clear();
            transaction.commit();
            isError = Boolean.FALSE;

            LOGGER.debug("saveOrUpdateEntity done!!");

        } catch (HibernateException he) {

            he.printStackTrace();

            errorDetails = "HibernateException occurred trying to save/update entity: " + entity.getClass() + " - " + he.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } catch (Exception ex) {

            ex.printStackTrace();

            errorDetails = "General exception occurred trying to save/update entity: " + entity.getClass() + " - " + ex.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } finally {
            closeSession(session);
        }

        if (isError) {
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
            throw error;
        }

        return;
    }

    //check this method before using it, dont we need to use flush just like in bulkSave??
    public boolean bulkUpdate(Set<DBInterface> dbObjectList) throws MyCustomException {

        StatelessSession tempSession = getStatelessSession();
        Transaction transaction = null;
        String errorDetails;

        try {

            transaction = tempSession.beginTransaction();

            //check this method before using it, dont we need to use flush just like in bulkSave??
            for (DBInterface dbObject : dbObjectList) {
                tempSession.update(dbObject);
            }
            transaction.commit();
            return Boolean.TRUE;

        } catch (HibernateException he) {

            errorDetails = "HibernateException occurred trying to do a bulk update: " + he.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } catch (Exception ex) {

            errorDetails = "General exception occurred while trying to do a bulk update: " + ex.toString();

            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            closeSession(tempSession);
        }

        MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
        throw error;

    }

    /**
     * Update a list of objects
     *
     * @param entityList
     * @return
     * @throws MyCustomException
     */
    public boolean updateBulk(Set<BaseEntity> entityList) throws MyCustomException {

        int updateCount = 0;

        Session session = getSession();
        Transaction transaction = null;
        String errorDetails;

        try {

            transaction = session.beginTransaction();
            for (BaseEntity entity : entityList) {

                session.update(entity);

                if ((updateCount % NamedConstants.HIBERNATE_JDBC_BATCH) == 0) { // Same as the JDBC batch size
                    //flush a batch of inserts and release memory: Without the call to the flush method,
                    //your first-level cache would throw an OutOfMemoryException
                    session.flush();
                    session.clear();
                }

                updateCount++;
            }

            transaction.commit();
            return Boolean.TRUE;

        } catch (HibernateException he) {
            he.printStackTrace();

            errorDetails = "HibernateException occurred trying to do a bulk update: " + he.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } catch (Exception ex) {

            ex.printStackTrace();

            errorDetails = "General exception occurred while trying to do a bulk update: " + ex.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } finally {

            closeSession(session);
        }

        MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
        throw error;
    }

    /**
     * Update an entity in the database
     *
     * @param entity
     * @return
     * @throws com.agromarketday.ussd.exception.MyCustomException
     */
    public boolean updateEntity(BaseEntity entity) throws MyCustomException {

        Session session = getSession();
        Transaction transaction = null;
        String errorDetails = "";
        boolean isError = Boolean.TRUE;

        try {
            transaction = session.beginTransaction();
            session.update(entity);
            //retrievedDatabaseModel = (T)getSession().get(persistentClass, objectId);
            //retrievedDatabaseModel = (T)session.merge(object);
            //tempSession.update(retrievedDatabaseModel);
            //retrievedDatabaseModel = dbObject;
            //tempSession.update(session.merge(retrievedDatabaseModel));
            //tempSession.update(retrievedDatabaseModel);
            session.flush();
            session.clear();

            transaction.commit();
            isError = Boolean.FALSE;

        } catch (HibernateException he) {

            errorDetails = "HibernateException occurred trying to update a record: " + he.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } catch (Exception ex) {

            errorDetails = "General exception occurred trying to update a record: " + ex.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } finally {
            closeSession(session);
        }

        if (isError) {
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
            throw error;
        }

        return Boolean.TRUE;

    }

    /**
     * Update an entity in the database
     *
     * @param entity
     * @return
     * @throws com.agromarketday.ussd.exception.MyCustomException
     */
    public boolean updateEntity(DBInterface entity) throws MyCustomException {

        Session tempSession = getSession();
        Transaction transaction = null;
        String errorDetails = "";
        boolean isError = Boolean.TRUE;

        try {
            transaction = tempSession.beginTransaction();
            tempSession.update(entity);
            //retrievedDatabaseModel = (T)getSession().get(persistentClass, objectId);
            //retrievedDatabaseModel = (T)session.merge(object);
            //tempSession.update(retrievedDatabaseModel);
            //retrievedDatabaseModel = dbObject;
            //tempSession.update(session.merge(retrievedDatabaseModel));
            //tempSession.update(retrievedDatabaseModel);
            tempSession.flush();
            transaction.commit();
            isError = Boolean.FALSE;

        } catch (HibernateException he) {

            errorDetails = "HibernateException occurred trying to update a record: " + he.toString();

            if (transaction != null) {
                transaction.rollback();
            }
        } catch (Exception ex) {

            errorDetails = "General exception occurred trying to update a record: " + ex.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } finally {
            closeSession(tempSession);
        }

        if (isError) {
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
            throw error;
        }

        return Boolean.TRUE;
    }

    /**
     *
     * @param sameStatusPick
     * @param id
     * @throws MyCustomException
     */
    public void updateCampaignSameStatusColumns(int sameStatusPick, long id) throws MyCustomException {

        LOGGER.info("SAME STATUS INCREMENT: " + sameStatusPick);

        Session session = getSession();

        Transaction transaction = null;
        String errorDetails = "";
        boolean isError = Boolean.TRUE;

        try {

            transaction = session.beginTransaction();

            String hqlUpdate = "update AdProgram prog set prog.sameStatusPick = :sameStatusPick where prog.id = :id";
            // or String hqlUpdate = "update Customer set name = :newName where name = :oldName";

            TypedQuery updateQuery = session.createQuery(hqlUpdate);
            //org.hibernate.Query updateQuery = session.createSQLQuery(sqlQuery);

            updateQuery.setParameter("sameStatusPick", sameStatusPick);
            updateQuery.setParameter("id", id);

            int entitiesUpdated = updateQuery.executeUpdate();
            transaction.commit();

            isError = Boolean.FALSE;

        } catch (HibernateException he) {

            errorDetails = "hibernate exception while updating in Campaign table: " + he.toString();

            LOGGER.error(errorDetails);

            if (transaction != null) {
                transaction.rollback();
            }
        } catch (Exception e) {

            errorDetails = "General exception while updating columns in Campaign table: " + e.toString();

            LOGGER.error(errorDetails);

            if (transaction != null) {
                transaction.rollback();
            }

        } finally {
            closeSession(session);
        }

        if (isError) {
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
            throw error;
        }
    }

    /**
     *
     * @param <BaseEntity>
     * @param entityType
     * @param setPropertyName
     * @return
     * @throws com.library.customexception.MyCustomException
     */
    public <BaseEntity> Set<BaseEntity> fetchCorrespondingSet(Class entityType, String setPropertyName) throws MyCustomException {

        //StatelessSession session = getStatelessSession();
        String errorDetails;
        Session session = getSession();
        Transaction transaction = null;

        Set<BaseEntity> results = new HashSet<>();

        try {

            transaction = session.beginTransaction();
            Criteria criteria = session.createCriteria(entityType);

            //criteria.add(Restrictions.gt("dealerId", dealerId));
            // this tells Hibernate that the makes must be fetched from the database
            // you must use the name of the annotated field in the Java class: dealerMakes
            criteria.setFetchMode(setPropertyName, FetchMode.JOIN);

            // Hibernate will return instances of Dealer, but it will return the same instance several times
            // once per make the dealer has. To avoid this, you must use a distinct root entity transformer
            criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

            List<BaseEntity> records = criteria.list();
            results = GeneralUtils.convertListToSet(records);

            transaction.commit();

            return results;

        } catch (HibernateException he) {

            errorDetails = "hibernate exception Fetching object list: " + he.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } catch (Exception e) {

            errorDetails = "General exception Fetching object list: " + e.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } finally {
            closeSession(session);
        }

        MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
        throw error;

    }

    /**
     * Fetch records matching certain conditions
     *
     * @param <BaseEntity>
     * @param entityType
     * @return
     * @throws com.library.customexception.MyCustomException
     */
    public <BaseEntity> Set<BaseEntity> fetchBulk(Class entityType) throws MyCustomException {

        Session session = getSession();
        Transaction transaction = null;

        Set<BaseEntity> results = new HashSet<>();
        String errorDetails;

        try {

            // Query updateQuery = session.createQuery("from Stock where stockCode = :code ");
            //query.setParameter("code", "7277");
            //List list = updateQuery.list();
            transaction = session.beginTransaction();
            Criteria criteria = session.createCriteria(entityType);
            criteria.setCacheMode(CacheMode.REFRESH);

            //criteria.addOrder(Order.asc(propertyName));
            // To-Do -> add the other parameters, e.g. orderby, etc
            ScrollableResults scrollableResults = criteria.scroll(ScrollMode.FORWARD_ONLY);

            int count = 0;
            while (scrollableResults.next()) {

                if (++count > 0 && count % 10 == 0) {

                    LOGGER.debug("Fetched " + count + " entities");
                    session.flush();
                    session.clear();
                }
                results.add((BaseEntity) scrollableResults.get()[0]);

            }

            //session.refresh(results);
            transaction.commit();

            return results;

        } catch (HibernateException he) {

            errorDetails = "hibernate exception Fetching object list: " + he.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } catch (Exception e) {

            errorDetails = "General exception Fetching object list: " + e.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } finally {
            closeSession(session);
        }

        MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
        throw error;
    }

    /**
     *
     * @param <DBInterface>
     * @param entityType
     * @param propertyName
     * @param propertyValue
     * @return
     * @throws com.library.customexception.MyCustomException
     */
    public <DBInterface> DBInterface fetchEntity(Class entityType, String propertyName, Object propertyValue) throws MyCustomException {

        Session session = getSession();
        Transaction transaction = null;
        String errorDetails = "";
        boolean isError = Boolean.TRUE;
        DBInterface result = null;

        try {

            transaction = session.beginTransaction();
            Criteria criteria = session.createCriteria(entityType);
            criteria.add(Restrictions.eq(propertyName, propertyValue));

            criteria.setMaxResults(1);

            result = (DBInterface) criteria.uniqueResult();

            transaction.commit();
            isError = Boolean.FALSE;
            return result;

        } catch (HibernateException he) {

            errorDetails = "hibernate exception fetching a record from database: " + he.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } catch (Exception e) {

            errorDetails = "General exception Fetching a record from database: " + e.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } finally {
            closeSession(session);
        }

        //if (isError) {
        MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
        throw error;
        //}

    }

    /**
     *
     * @param <BaseEntity>
     * @param entityType
     * @param propertyNameValues
     * @return
     * @throws com.library.customexception.MyCustomException
     */
    public <BaseEntity> BaseEntity fetchEntity(Class entityType, Map<String, Set<Object>> propertyNameValues) throws MyCustomException {

        Session session = getSession();
        Transaction transaction = null;
        String errorDetails = "";
        boolean isError = Boolean.TRUE;
        BaseEntity result = null;

        try {

            transaction = session.beginTransaction();
            Criteria criteria = session.createCriteria(entityType);

            //criteria.add(Restrictions.gt("dealerId", dealerId));
            // this tells Hibernate that the makes must be fetched from the database
            // you must use the name of the annotated field in the Java class: dealerMakes
            //criteria.setFetchMode("setPropertyName", FetchMode.JOIN);
            // Hibernate will return instances of Dealer, but it will return the same instance several times
            // once per make the dealer has. To avoid this, you must use a distinct root entity transformer
            //criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
            //propertyNameValues.entrySet().stream().forEach((entry) -> {
            for (Map.Entry<String, Set<Object>> entry : propertyNameValues.entrySet()) {

                String name = entry.getKey();
                Set<Object> values = entry.getValue();

                LOGGER.debug("Field Name  : " + name);
                LOGGER.debug("Field values: " + values);

                //if objects set is empty or contains a '1' - we will select all records
                if (values == null || values.isEmpty() || values.contains(String.valueOf(1))) {

                    LOGGER.info("No Restrictions on property: " + name + ", while Fetching: " + entityType.getName() + " objects.");

                } else if (name.equals("internalPaymentID")) {

                    Set<String> paymentIds = new HashSet<>();
                    for (Object object : values) {

                        String paymentId = (String) object;
                        paymentIds.add(paymentId);
                    }
                    criteria.add(Restrictions.in(name, paymentIds));

                } else if (name.equals("sessionId")) {

                    Set<String> sessionIds = new HashSet<>();
                    for (Object object : values) {

                        String sessionId = (String) object;
                        sessionIds.add(sessionId);
                    }
                    criteria.add(Restrictions.in(name, sessionIds));

                } else if (name.equals("sellerContact")) {

                    Set<String> sellerContacts = new HashSet<>();
                    for (Object object : values) {

                        String sellerContact = (String) object;
                        sellerContacts.add(sellerContact);
                    }
                    criteria.add(Restrictions.in(name, sellerContacts));

                } else if (name.equals("userId")) {

                    Set<String> userIds = new HashSet<>();
                    for (Object object : values) {

                        String userId = (String) object;
                        userIds.add(userId);
                    }
                    criteria.add(Restrictions.in(name, userIds));

                } else if (name.equals("password")) {

                    Set<String> passwords = new HashSet<>();
                    for (Object object : values) {

                        String password = (String) object;
                        passwords.add(password);
                    }
                    criteria.add(Restrictions.in(name, passwords));

                } else if (name.equals("adUsers.userId")) {

                    Set<String> userIds = new HashSet<>();
                    for (Object object : values) {

                        String userId = (String) object;
                        userIds.add(userId);
                    }
                    criteria.add(Restrictions.in(name, userIds));

                } else if (name.equals("displayDate")) {

                    Set<LocalDate> displayDates = new HashSet<>();
                    for (Object object : values) {

                        //LocalDate paymentId = DateUtils.convertStringToLocalDate((String) object, NamedConstants.DATE_DASH_FORMAT);
                        LocalDate date = new LocalDate(object);
                        displayDates.add(date);
                    }
                    criteria.add(Restrictions.in(name, displayDates));

                } else if (name.equals("id")) {

                    Set<Long> ids = new HashSet<>();

                    for (Object object : values) {

                        long val = GeneralUtils.convertObjectToLong(object);
                        ids.add(val);
                    }
                    criteria.add(Restrictions.in(name, ids));

                } else if (name.equals("isUploadedToDSM")) {

                    Set<Boolean> vals = new HashSet<>();

                    for (Object object : values) {

                        boolean val = (Boolean) object;
                        vals.add(val);
                    }
                    criteria.add(Restrictions.in(name, vals));

                } else {
                    criteria.add(Restrictions.in(name, values));
                }

            }

            criteria.setMaxResults(1);

            result = (BaseEntity) criteria.uniqueResult();
            transaction.commit();

            isError = Boolean.FALSE;

        } catch (HibernateException he) {

            errorDetails = "hibernate exception fetching a record from database: " + he.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } catch (Exception e) {

            errorDetails = "General exception Fetching a record from database: " + e.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } finally {
            closeSession(session);
        }

        if (isError) {
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
            throw error;
        }

        return result;
    }

    /**
     *
     * @param entityType
     * @param propertyNameValues
     * @return
     * @throws com.library.customexception.MyCustomException
     */
    public boolean isRecordExists(Class entityType, Map<String, Object> propertyNameValues) throws MyCustomException {

        Session session = getSession();
        Transaction transaction = null;
        String errorDetails;

        try {
            transaction = session.beginTransaction();
            Criteria criteria = session.createCriteria(entityType);

            //propertyNameValues.entrySet().stream().forEach((entry) -> {
            for (Map.Entry<String, Object> entry : propertyNameValues.entrySet()) {

                String name = entry.getKey();
                Set<Object> values = (Set<Object>) entry.getValue();

                LOGGER.debug("Field Name  : " + name);
                LOGGER.debug("Field values: " + values);

                //if objects set is empty or contains a '1' - we will select all records
                if (values == null || values.isEmpty() || values.contains(String.valueOf(1))) {

                    LOGGER.info("No Restrictions on property: " + name + ", while Fetching: " + entityType.getName() + " objects.");

                } else if (name.equals("displayDate")) {

                    Set<LocalDate> displayDates = new HashSet<>();
                    for (Object object : values) {

                        //LocalDate paymentId = DateUtils.convertStringToLocalDate((String) object, NamedConstants.DATE_DASH_FORMAT);
                        LocalDate date = new LocalDate(object);
                        displayDates.add(date);
                    }
                    criteria.add(Restrictions.in(name, displayDates));

                } else if (name.equals("id")) {

                    Set<Long> ids = new HashSet<>();

                    for (Object object : values) {

                        long val = GeneralUtils.convertObjectToLong(object);
                        ids.add(val);
                    }
                    criteria.add(Restrictions.in(name, ids));

                } else if (name.equals("id.fileId")) {

                    Set<Long> fileIds = new HashSet<>();

                    for (Object object : values) {

                        long val = GeneralUtils.convertObjectToLong(object);
                        fileIds.add(val);
                    }
                    criteria.add(Restrictions.in(name, fileIds));

                } else if (name.equals("isUploadedToDSM")) {

                    Set<Boolean> vals = new HashSet<>();

                    for (Object object : values) {

                        boolean val = (Boolean) object;
                        vals.add(val);
                    }
                    criteria.add(Restrictions.in(name, vals));

                } else if (name.equals("id.taskId")) {

                    Set<Integer> vals = new HashSet<>();

                    for (Object object : values) {

                        int val = (Integer) object;
                        vals.add(val);
                    }
                    criteria.add(Restrictions.in(name, vals));

                } else if (name.equals("id.cstmId")) {

                    Set<Integer> vals = new HashSet<>();

                    for (Object object : values) {

                        int val = (Integer) object;
                        vals.add(val);
                    }
                    criteria.add(Restrictions.in(name, vals));

                } else if (name.equals("cstmId")) {

                    Set<Integer> vals = new HashSet<>();

                    for (Object object : values) {

                        int val = (Integer) object;
                        vals.add(val);
                    }
                    criteria.add(Restrictions.in(name, vals));

                } else {
                    criteria.add(Restrictions.in(name, values));
                }

            }

            criteria.setProjection(Projections.rowCount());
            long count = (Long) criteria.uniqueResult();

            transaction.commit();

            LOGGER.debug("Records count is: " + count);

            if (count != 0) {
                return Boolean.TRUE;
            }

            return Boolean.FALSE;

        } catch (HibernateException he) {

            errorDetails = "HibernateException checking if record exists in database: " + he.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } catch (Exception e) {

            errorDetails = "General exception checking if record exists in database: " + e.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } finally {
            closeSession(session);
        }

        MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
        throw error;

    }

    /**
     * Get sumOfColumn of rows
     *
     * @param entityType
     * @param propertyNameValues
     * @return
     * @throws com.library.customexception.MyCustomException
     */
    public Number countRows(Class entityType, Map<String, Object> propertyNameValues) throws MyCustomException {

        Session session = getSession();
        Transaction transaction = null;
        String errorDetails;

        try {
            transaction = session.beginTransaction();
            Criteria criteria = session.createCriteria(entityType);

            if (propertyNameValues != null) {

                //propertyNameValues.entrySet().stream().forEach((entry) -> {
                for (Map.Entry<String, Object> entry : propertyNameValues.entrySet()) {

                    String name = entry.getKey();
                    Set<Object> values = (Set<Object>) entry.getValue();

                    LOGGER.debug("Field Name  : " + name);
                    LOGGER.debug("Field values: " + values);

                    //if objects set is empty or contains a '1' - we will select all records
                    if (values == null || values.isEmpty() || values.contains(String.valueOf(1))) {

                        LOGGER.info("No Restrictions on property: " + name + ", while Fetching: " + entityType.getName() + " objects.");

                    } else if (name.equals("displayDate")) {

                        Set<LocalDate> displayDates = new HashSet<>();
                        for (Object object : values) {

                            //LocalDate paymentId = DateUtils.convertStringToLocalDate((String) object, NamedConstants.DATE_DASH_FORMAT);
                            LocalDate date = new LocalDate(object);
                            displayDates.add(date);
                        }
                        criteria.add(Restrictions.in(name, displayDates));

                    } else if (name.equals("id")) {

                        Set<Long> ids = new HashSet<>();

                        for (Object object : values) {

                            long val = GeneralUtils.convertObjectToLong(object);
                            ids.add(val);
                        }
                        criteria.add(Restrictions.in(name, ids));

                    } else if (name.equals("isUploadedToDSM")) {

                        Set<Boolean> vals = new HashSet<>();

                        for (Object object : values) {

                            boolean val = (Boolean) object;
                            vals.add(val);
                        }
                        criteria.add(Restrictions.in(name, vals));

                    } else if (name.equals("cstmId")) {

                        Set<Integer> vals = new HashSet<>();

                        for (Object object : values) {

                            int val = (Integer) object;
                            vals.add(val);
                        }
                        criteria.add(Restrictions.in(name, vals));

                    } else {
                        criteria.add(Restrictions.in(name, values));
                    }

                }
            }

            criteria.setProjection(Projections.rowCount());
            Number count = (Number) criteria.uniqueResult();

            transaction.commit();
            return count;

        } catch (HibernateException he) {

            errorDetails = "hibernate exception trying to count records of type: " + entityType.getCanonicalName() + " -  " + he.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } catch (Exception e) {

            errorDetails = "General exception trying to count records of type: " + entityType.getCanonicalName() + " -  " + e.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } finally {
            closeSession(session);
        }

        MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
        throw error;
    }

    /**
     * Get the sum total of a summable column, a column whose objects are of
     * type Number
     *
     * @param entityType
     * @param columnName
     * @param propertyNameValues
     * @return
     * @throws com.library.customexception.MyCustomException
     */
    public Number sumColumn(Class entityType, String columnName, Map<String, Object> propertyNameValues) throws MyCustomException {

        //.setProjection(Projections.sqlProjection("sum(cast(amount as signed)* direction) as amntDir", new String[] {"amntDir"} , new Type[] {Hibernate.DOUBLE}));
        //http://stackoverflow.com/questions/4624807/using-sum-in-hibernate-criteria
        Session session = getSession();
        Transaction transaction = null;
        String errorDetails;

        try {
            transaction = session.beginTransaction();
            Criteria criteria = session.createCriteria(entityType);

            if (propertyNameValues != null) {

                //propertyNameValues.entrySet().stream().forEach((entry) -> {
                for (Map.Entry<String, Object> entry : propertyNameValues.entrySet()) {

                    String name = entry.getKey();
                    Set<Object> values = (Set<Object>) entry.getValue();

                    LOGGER.debug("Field Name  : " + name);
                    LOGGER.debug("Field values: " + values);

                    //if objects set is empty or contains a '1' - we will select all records
                    if (values == null || values.isEmpty() || values.contains(String.valueOf(1))) {

                        LOGGER.info("No Restrictions on property: " + name + ", while Fetching: " + entityType.getName() + " objects.");

                    } else if (name.equals("displayDate")) {

                        Set<LocalDate> displayDates = new HashSet<>();
                        for (Object object : values) {

                            //LocalDate paymentId = DateUtils.convertStringToLocalDate((String) object, NamedConstants.DATE_DASH_FORMAT);
                            LocalDate date = new LocalDate(object);
                            displayDates.add(date);
                        }
                        criteria.add(Restrictions.in(name, displayDates));

                    } else if (name.equals("id")) {

                        Set<Long> ids = new HashSet<>();

                        for (Object object : values) {

                            long val = GeneralUtils.convertObjectToLong(object);
                            ids.add(val);
                        }
                        criteria.add(Restrictions.in(name, ids));

                    } else if (name.equals("isUploadedToDSM")) {

                        Set<Boolean> vals = new HashSet<>();

                        for (Object object : values) {

                            boolean val = (Boolean) object;
                            vals.add(val);
                        }
                        criteria.add(Restrictions.in(name, vals));

                    } else if (name.equals("cstmId")) {

                        Set<Integer> vals = new HashSet<>();

                        for (Object object : values) {

                            int val = (Integer) object;
                            vals.add(val);
                        }
                        criteria.add(Restrictions.in(name, vals));

                    } else {
                        criteria.add(Restrictions.in(name, values));
                    }

                }
            }

            criteria.setProjection((Projections.sum(columnName)));
            Number sumOfColumn = (Number) criteria.uniqueResult();

            transaction.commit();

            return sumOfColumn;

        } catch (HibernateException he) {

            errorDetails = "hibernate exception trying to get sum total of a summable column of entity: " + entityType.getCanonicalName() + " -  " + he.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } catch (Exception e) {

            errorDetails = "General exception trying to get sum total of a summable column of entity: " + entityType.getCanonicalName() + " -  " + e.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } finally {
            closeSession(session);
        }

        MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
        throw error;
    }

    /**
     * Fetch records matching certain conditions
     *
     * @param <BaseEntity>
     * @param entityType
     * @param propertyNameValues
     * @return
     * @throws com.agromarketday.ussd.exception.MyCustomException
     */
    public <BaseEntity> Set<BaseEntity> fetchBulk(Class entityType, Map<String, Object> propertyNameValues) throws MyCustomException {

        Session session = getSession();
        Transaction transaction = null;

        Set<BaseEntity> results = new HashSet<>();
        String errorDetails = "";
        boolean isError = Boolean.TRUE;

        try {

//            // Create CriteriaBuilder
//CriteriaBuilder builder = session.getCriteriaBuilder();
//
//// Create CriteriaQuery
//CriteriaQuery<BaseEntity> criteria = builder.createQuery(entityType);
            transaction = session.beginTransaction();
            Criteria criteria = session.createCriteria(entityType);
            criteria.setCacheMode(CacheMode.REFRESH);
            //adding ordering
            criteria.addOrder(Order.desc("id"));

            //propertyNameValues.entrySet().stream().forEach((entry) -> {
            for (Map.Entry<String, Object> entry : propertyNameValues.entrySet()) {

                String name = entry.getKey();
                Set<Object> objects = (Set<Object>) entry.getValue();

                LOGGER.debug("Field Name  : " + name);
                LOGGER.debug("Field values: " + objects);

                //if objects set is empty or contains a '1' - we will select all records
                if (objects == null || objects.isEmpty() || objects.contains("1")) {
                    LOGGER.info("No Restrictions on property: " + name + ", while Fetching: " + entityType.getName() + " objects.");

                } else if (name.equals("campaignId")) {
                    Set<Integer> values = new HashSet<>();
                    for (Object object : objects) {

                        //LocalDate paymentId = DateUtils.convertStringToLocalDate((String) object, NamedConstants.DATE_DASH_FORMAT);
                        int campaignId = GeneralUtils.convertObjectToInteger(object);
                        values.add(campaignId);
                    }
                    criteria.add(Restrictions.in(name, values));

                } else if (name.equals("displayDate")) {
                    Set<LocalDate> displayDates = new HashSet<>();
                    for (Object object : objects) {

                        //LocalDate paymentId = DateUtils.convertStringToLocalDate((String) object, NamedConstants.DATE_DASH_FORMAT);
                        LocalDate date = new LocalDate(object);
                        displayDates.add(date);
                    }
                    criteria.add(Restrictions.in(name, displayDates));

                } else if (name.equals("id")) {

                    Set<Long> ids = new HashSet<>();

                    for (Object object : objects) {

                        long val = GeneralUtils.convertObjectToLong(object);
                        ids.add(val);
                    }
                    criteria.add(Restrictions.in(name, ids));

                } else if (name.equals("isUploadedToDSM")) {

                    Set<Boolean> vals = new HashSet<>();

                    for (Object object : objects) {

                        boolean val = (Boolean) object;
                        vals.add(val);
                    }
                    criteria.add(Restrictions.in(name, vals));

                } else if (name.equals("fetchStatus")) {

                    Set<FetchStatus> vals = new HashSet<>();

                    for (Object object : objects) {

                        //FetchStatus val = FetchStatus.convertToEnum((String) object); //WORKS WITH external DBAdapter
                        FetchStatus val = (FetchStatus) object;
                        vals.add(val);
                    }
                    criteria.add(Restrictions.in(name, vals));

                } else if (name.equals("paymentStatus")) {

                    //Set<AdPaymentStatus> vals = new HashSet<>();
                    for (Object object : objects) {

//                         AdPaymentStatus val = (AdPaymentStatus) object;
//                        vals.add(val);
                    }
                    //criteria.add(Restrictions.in(name, vals));
                    criteria.add(Restrictions.in(name, new HashSet<>()));

                } else if (name.equals("audienceTypes.id")) {

                    Set<Long> vals = new HashSet<>();

                    for (Object object : objects) {

                        long val = GeneralUtils.convertObjectToLong(object);
                        vals.add(val);
                    }

                    LOGGER.debug("aud values here: " + Arrays.toString(vals.toArray()));

                    criteria.createAlias("audienceTypes", "audtype") //without this, keeps throwing error - org.hibernate.QueryException: could not resolve property: audienceTypes.audienceCode of: com.library.datamodel.model.v1_0.AdScreen 
                            .add(Restrictions.in("audtype.id", vals));

                } else if (name.equals("businessServices.id")) {

                    Set<Long> vals = new HashSet<>();

                    for (Object object : objects) {

                        long val = GeneralUtils.convertObjectToLong(object);
                        vals.add(val);
                    }

                    LOGGER.debug("businessServices values here: " + Arrays.toString(vals.toArray()));

                    criteria.createAlias("businessServices", "services") //without this, keeps throwing error - org.hibernate.QueryException: could not resolve property: audienceTypes.audienceCode of: com.library.datamodel.model.v1_0.AdScreen 
                            .add(Restrictions.in("services.id", vals));

                } else if (name.equals("adTextPrograms.campaignId")) {
                    Set<Integer> values = new HashSet<>();
                    for (Object object : objects) {

                        int campaignId = GeneralUtils.convertObjectToInteger(object);
                        values.add(campaignId);
                    }
                    criteria.createAlias("adTextPrograms", "programs")
                            .add(Restrictions.in("programs.campaignId", values));

                } else if (name.equals("adResourcePrograms.campaignId")) {
                    Set<Integer> values = new HashSet<>();
                    for (Object object : objects) {

                        int campaignId = GeneralUtils.convertObjectToInteger(object);
                        values.add(campaignId);
                    }
                    criteria.createAlias("adResourcePrograms", "programs")
                            .add(Restrictions.in("programs.campaignId", values));

                } else {
                    criteria.add(Restrictions.in(name, objects));
                }

            }

//            if(!isFetchAll){
//                criteria.add(Restrictions.allEq(propertyNameValues));
//            }
            //criteria.addOrder(Order.asc(propertyName)); // To-Do -> add the other parameters, e.g. orderby, etc
            ScrollableResults scrollableResults = criteria.scroll(ScrollMode.FORWARD_ONLY);

            int count = 0;
            while (scrollableResults.next()) {
                if (++count > 0 && count % 10 == 0) {
                    LOGGER.debug("Fetched " + count + " entities");
                    session.flush();
                    session.clear();
                }
                results.add((BaseEntity) scrollableResults.get()[0]);

            }

            //session.refresh(results);
            LOGGER.info("size of results: " + results.size());

            LOGGER.debug("DB Results from Fetch: " + Arrays.asList(results));

//            List<BaseEntity> records = criteria.list();
//            results = GeneralUtils.convertListToSet(records);
            transaction.commit();
            isError = Boolean.FALSE;

        } catch (HibernateException he) {

            he.printStackTrace();

            errorDetails = "hibernate exception Fetching records from the database: " + he.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } catch (Exception e) {

            e.printStackTrace();

            errorDetails = "General exception Fetching records from the database: " + e.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } finally {
            LOGGER.warn("Closing session..");
            closeSession(session);
        }

        if (isError) {
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
            throw error;
        }

        return results;
    }

    /**
     *
     * @param <BaseEntity>
     * @param entityType
     * @param propertyNameValues
     * @return
     * @throws MyCustomException
     */
    public <BaseEntity> Set<BaseEntity> fetchBulk_TempSession(Class entityType, Map<String, Object[]> propertyNameValues) throws MyCustomException {

        StatelessSession session = getStatelessSession();

        Set<BaseEntity> results = new HashSet<>();
        String errorDetails;

        try {

            Criteria criteria = session.createCriteria(entityType);

            // propertyNameValues.entrySet().stream().forEach((entry) -> {
            for (Map.Entry<String, Object[]> entry : propertyNameValues.entrySet()) {

                String name = entry.getKey();
                Object[] values = entry.getValue();

                //criteria.add(Restrictions.in(name, objects)); //un-c0mment and sort out errors when r3ady
            }

//            if(!isFetchAll){
//                criteria.add(Restrictions.allEq(propertyNameValues));
//            }
            //criteria.addOrder(Order.asc(propertyName));
            // To-Do -> add the other parameters, e.g. orderby, etc
            ScrollableResults scrollableResults = criteria.scroll(ScrollMode.FORWARD_ONLY);

            int count = 0;
            while (scrollableResults.next()) {
                if (++count > 0 && count % 10 == 0) {
                    LOGGER.debug("Fetched " + count + " entities");
                }
                results.add((BaseEntity) scrollableResults.get()[0]);

            }

            return results;

        } catch (HibernateException he) {

            errorDetails = "hibernate exception Fetching records from the database: " + he.toString();

        } catch (Exception e) {

            errorDetails = "hibernate exception Fetching records from the database: " + e.toString();

        } finally {
            closeSession(session);
        }

        MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
        throw error;
    }

    /**
     * fetch bulk records that have a given property value
     *
     * @param <DBInterface>
     * @param entityType
     * @param propertyName
     * @param propertyValue
     * @return bulk of records fetched
     * @throws com.library.customexception.MyCustomException
     */
    public <DBInterface> Set<DBInterface> fetchBulk(Class<DBInterface> entityType, String propertyName, Object propertyValue) throws MyCustomException {

        StatelessSession tempSession = getStatelessSession();

        String errorDetails;
        try {

            Criteria criteria = tempSession.createCriteria(entityType);
            criteria.add(Restrictions.eq(propertyName, propertyValue));

            ScrollableResults scrollableResults = criteria.scroll(ScrollMode.FORWARD_ONLY);

            int count = 0;
            Set<DBInterface> fetchedEntities = new HashSet<>();
            while (scrollableResults.next()) {

                if ((count > 0) && (count % 10 == 0)) {
                    LOGGER.debug("Fetched " + count + " entities");
                }
                count++;
                fetchedEntities.add((DBInterface) scrollableResults.get()[0]);

            }
            return fetchedEntities;
        } catch (HibernateException he) {

            errorDetails = "hibernate exception Fetching records from the database: " + he.toString();
        } catch (Exception e) {

            errorDetails = "General exception Fetching records from the database: " + e.toString();
        } finally {
            closeSession(tempSession);
        }

        MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
        throw error;
    }

    /**
     *
     * @param <T>
     * @param entityType
     * @return
     * @throws com.library.customexception.MyCustomException
     */
    public <T> Set<T> fetchBulkStateless(Class<T> entityType) throws MyCustomException {

        StatelessSession tempSession = getStatelessSession();
        String errorDetails;

        try {

            Criteria criteria = tempSession.createCriteria(entityType);

            ScrollableResults scrollableResults = criteria.scroll(ScrollMode.FORWARD_ONLY);

            int count = 0;
            Set<T> fetchedEntities = new HashSet<>();
            while (scrollableResults.next()) {

                if ((count > 0) && (count % 10 == 0)) {
                    LOGGER.debug("Fetched " + count + " entities");
                }
                count++;
                fetchedEntities.add((T) scrollableResults.get()[0]);

            }
            return fetchedEntities;

        } catch (HibernateException he) {

            errorDetails = "hibernate exception Fetching records from the database: " + he.toString();
        } catch (Exception e) {

            errorDetails = "General exception Fetching records from the database: " + e.toString();
        } finally {
            closeSession(tempSession);
        }

        MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
        throw error;
    }

    /**
     * Fetch only a single entity/object from the database with a temp session
     *
     * @param entityType
     * @param propertyName
     * @param propertyValue
     * @return
     * @throws com.library.customexception.MyCustomException
     */
    public DBInterface fetchEntityTempSession(Class entityType, String propertyName, Object propertyValue) throws MyCustomException {

        StatelessSession tempSession = getStatelessSession();

        String errorDetails;
        try {

            Criteria criteria = tempSession.createCriteria(entityType);
            criteria.add(Restrictions.eq(propertyName, propertyValue));
            criteria.setMaxResults(1);

            return ((DBInterface) criteria.uniqueResult());

        } catch (HibernateException he) {

            errorDetails = "hibernate exception Fetching records from the database: " + he.toString();
        } catch (Exception e) {

            errorDetails = "General exception Fetching records from the database: " + e.toString();
        } finally {
            closeSession(tempSession);
        }

        MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
        throw error;
    }

    /**
     * Fetch entire column without restrictions
     *
     * @param <T>
     * @param classType
     * @param columToFetch
     * @return
     * @throws com.library.customexception.MyCustomException
     */
    public <T> List<T> fetchOnlyColumn(Class classType, String columToFetch) throws MyCustomException {

        StatelessSession tempSession = getStatelessSession();
        String errorDetails;

        try {

            //Criteria.forClass(bob.class.getName())
            Criteria criteria = tempSession.createCriteria(classType);
            criteria.setProjection(Projections.property(columToFetch));
            //criteria.add(Restrictions.gt("id", 10));
            //criteria.add(Restrictions.eq(restrictToPropertyName, restrictionValue)); //transactions should belong to the same group
            //criteria.addOrder(Order.asc(propertyName));

            ScrollableResults scrollableResults = criteria.scroll(ScrollMode.FORWARD_ONLY);

            int count = 0;
            List<T> results = new ArrayList<>();
            while (scrollableResults.next()) {
                if (++count > 0 && count % 10 == 0) {
                    LOGGER.debug("Fetched " + count + " entities");
                }
                results.add((T) scrollableResults.get()[0]);

            }
            return results;

        } catch (HibernateException he) {
            errorDetails = "hibernate exception Fetching records from the database: " + he.toString();
        } catch (Exception e) {
            errorDetails = "General exception Fetching records from the database: " + e.toString();
        } finally {
            closeSession(tempSession);
        }

        MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
        throw error;
    }

    /**
     * Get the most recent record in the database according to the condition
     * given
     *
     * @param <T>
     * @param persistentClassType
     * @param idColumn The auto-generated db column ('id') which can be used to
     * sort or count
     * @param propertyName The name of the field that has the condition for
     * fetching this record
     * @param propertyValue The condition's value
     * @return
     * @throws MyCustomException
     */
    public <T> T getMostRecentRecord(Class<T> persistentClassType, String idColumn, String propertyName, String propertyValue) throws MyCustomException {

        StatelessSession tempSession = getStatelessSession();

        String errorDetails;

        try {

            DetachedCriteria maxCriteria = DetachedCriteria.forClass(persistentClassType);
            maxCriteria.setProjection(Projections.max(idColumn));

            Criteria criteria = tempSession.createCriteria(persistentClassType);
            criteria.add(Property.forName(idColumn).eq(maxCriteria));
            criteria.add(Restrictions.eq(propertyName, propertyValue));

            //criteria.list();
            criteria.setMaxResults(1);

            T result = (T) criteria.uniqueResult();

            return result;

        } catch (HibernateException he) {

            he.printStackTrace();
            errorDetails = "hibernate exception fetching max row: " + he.toString();
        } catch (Exception e) {

            e.printStackTrace();
            errorDetails = "General exception fetching max row: " + e.toString();

        } finally {
            closeSession(tempSession);
        }

        MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
        throw error;
    }

    /**
     * Get the most recent record in the database according to the condition
     * given
     *
     * @param <T>
     * @param persistentClassType
     * @param idColumn The auto-generated db column ('id') which can be used to
     * sort or count
     * @return
     * @throws MyCustomException
     */
    public <T> T getMostRecentRecord(Class<T> persistentClassType,
            String idColumn) throws MyCustomException {

        StatelessSession tempSession = getStatelessSession();

        String errorDetails;

        try {

            DetachedCriteria maxCriteria
                    = DetachedCriteria.forClass(persistentClassType);
            maxCriteria.setProjection(Projections.max(idColumn));

            Criteria criteria = tempSession.createCriteria(persistentClassType);
            criteria.add(Property.forName(idColumn).eq(maxCriteria));

            //criteria.list();
            criteria.setMaxResults(1);

            T result = (T) criteria.uniqueResult();

            return result;

        } catch (HibernateException he) {

            he.printStackTrace();
            errorDetails = "hibernate exception fetching max row: " + he.toString();
        } catch (Exception e) {

            e.printStackTrace();
            errorDetails = "General exception fetching max row: " + e.toString();

        } finally {
            closeSession(tempSession);
        }
        MyCustomException error
                = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR,
                        NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
        throw error;
    }

    /**
     *
     * @param <BaseEntity>
     * @param entityType
     * @param columToFetch
     * @param propertyNameValues
     * @return
     * @throws MyCustomException
     */
    public <BaseEntity> Set<BaseEntity> fetchOnlyColumn(Class entityType, String columToFetch, Map<String, Object> propertyNameValues) throws MyCustomException {

        Session session = getSession();
        Transaction transaction = null;

        Set<BaseEntity> results = new HashSet<>();
        String errorDetails;
        try {

            transaction = session.beginTransaction();
            Criteria criteria = session.createCriteria(entityType);
            criteria.setProjection(Projections.property(columToFetch));
            criteria.setCacheMode(CacheMode.REFRESH);

            //propertyNameValues.entrySet().stream().forEach((entry) -> {
            for (Map.Entry<String, Object> entry : propertyNameValues.entrySet()) {

                String name = entry.getKey();
                Set<Object> values = (Set<Object>) entry.getValue();

                LOGGER.debug("Field Name  : " + name);
                LOGGER.debug("Field values: " + values);

                //if objects set is empty or contains a '1' - we will select all records
                if (values == null || values.isEmpty() || values.contains(String.valueOf(1))) {

                    LOGGER.info("No Restrictions on property: " + name + ", while Fetching: " + entityType.getName() + " objects.");

                } else if (name.equals("displayDate")) {

                    Set<LocalDate> displayDates = new HashSet<>();
                    for (Object object : values) {

                        //LocalDate paymentId = DateUtils.convertStringToLocalDate((String) object, NamedConstants.DATE_DASH_FORMAT);
                        LocalDate date = new LocalDate(object);
                        displayDates.add(date);
                    }
                    criteria.add(Restrictions.in(name, displayDates));

                } else if (name.equals("id")) {

                    Set<Long> ids = new HashSet<>();

                    for (Object object : values) {

                        long val = GeneralUtils.convertObjectToLong(object);
                        ids.add(val);
                    }
                    criteria.add(Restrictions.in(name, ids));

                } else if (name.equals("isUploadedToDSM")) {

                    Set<Boolean> vals = new HashSet<>();

                    for (Object object : values) {

                        boolean val = (Boolean) object;
                        vals.add(val);
                    }
                    criteria.add(Restrictions.in(name, vals));

                } else if (name.equals("audienceTypes.audienceCode")) {

                    Set<String> vals = new HashSet<>();

                    for (Object object : values) {

                        String val = (String) object;
                        vals.add(val);
                    }

                    criteria.createAlias("audienceTypes", "audtype") //without this, keeps throwing error - org.hibernate.QueryException: could not resolve property: audienceTypes.audienceCode of: com.library.datamodel.model.v1_0.AdScreen 
                            .add(Restrictions.in("audtype.audienceCode", vals));

                } else {
                    criteria.add(Restrictions.in(name, values));
                }

            }

//            if(!isFetchAll){
//                criteria.add(Restrictions.allEq(propertyNameValues));
//            }
            //criteria.addOrder(Order.asc(propertyName)); // To-Do -> add the other parameters, e.g. orderby, etc
            ScrollableResults scrollableResults = criteria.scroll(ScrollMode.FORWARD_ONLY);

            int count = 0;
            while (scrollableResults.next()) {
                if (++count > 0 && count % 10 == 0) {
                    LOGGER.debug("Fetched " + count + " entities");
                    session.flush();
                    session.clear();
                }
                results.add((BaseEntity) scrollableResults.get()[0]);

            }

//            List<BaseEntity> records = criteria.list();
//            results = GeneralUtils.convertListToSet(records);
            transaction.commit();
            return results;

        } catch (HibernateException he) {

            errorDetails = "HibernateException Fetching records from the database: " + he.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } catch (Exception e) {

            errorDetails = "General exception Fetching records from the database: " + e.toString();

            if (transaction != null) {
                transaction.rollback();
            }

        } finally {
            closeSession(session);
        }

        MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
        throw error;

    }

    private static final class ConfigureHibernate {

        private SessionFactory sessionFactory;

        private ConfigureHibernate() {

            LOGGER.debug("About to configure Hibernate!!!");

            try {
                configure();
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
                LOGGER.error("InvocationTargetException exception during hibernate configuration: " + ex.toString());
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
                LOGGER.error("ClassNotFoundException exception during hibernate configuration: " + ex.toString());
            } catch (NamingException ex) {
                ex.printStackTrace();
                LOGGER.error("Naming exception during hibernate configuration: " + ex.toString());
            } catch (MappingException ex) {
                LOGGER.error("Exception class: " + ex.getClass().toString());
                LOGGER.error("MappingException exception during hibernate configuration: " + ex.toString());
                ex.printStackTrace();
            } catch (HibernateException ex) {
                LOGGER.error("Exception class: " + ex.getClass().toString());
                LOGGER.error("Hibernate exception during hibernate configuration: " + ex.toString());
                ex.printStackTrace();
            } catch (Exception ex) {
                LOGGER.error("Exception class: " + ex.getClass().toString());
                LOGGER.error("General Exception during hibernate configuration: " + ex.toString());
                ex.printStackTrace();
            }
        }

        private static class ConfigureHibernateSingletonHolder {

            private static final ConfigureHibernate INSTANCE = new ConfigureHibernate();
        }

        private static ConfigureHibernate getInstance() {
            return ConfigureHibernateSingletonHolder.INSTANCE;
        }

        private Object readResolve() {
            return getInstance();
        }

        private SessionFactory createSessionFactory() {

            if (sessionFactory == null || sessionFactory.isClosed()) {

                LOGGER.debug("SessionFactory is NULL or closed going to reconfigure");

                try {
                    configure();
                } catch (InvocationTargetException ex) {
                    ex.printStackTrace();
                    LOGGER.error("InvocationTargetException exception during hibernate configuration: " + ex.toString());
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                    LOGGER.error("ClassNotFoundException exception during hibernate configuration: " + ex.toString());
                } catch (NamingException ex) {
                    ex.printStackTrace();
                    LOGGER.error("Naming exception during hibernate configuration: " + ex.toString());
                } catch (MappingException ex) {
                    LOGGER.error("Exception class: " + ex.getClass().toString());
                    LOGGER.error("MappingException exception during hibernate configuration: " + ex.toString());
                    ex.printStackTrace();
                } catch (HibernateException ex) {
                    LOGGER.error("Exception class: " + ex.getClass().toString());
                    LOGGER.error("Hibernate exception during hibernate configuration: " + ex.toString());
                    ex.printStackTrace();
                } catch (Exception ex) {
                    LOGGER.error("Exception class: " + ex.getClass().toString());
                    LOGGER.error("General Exception during hibernate configuration: " + ex.toString());
                    ex.printStackTrace();
                }

            } else {
                LOGGER.debug(">>>>>> We are GOOD, SessionFactory is not NULL and is OPEN");
            }

            return sessionFactory;
        }

        private void setSessionFactory(SessionFactory sessionFactory) {
            this.sessionFactory = sessionFactory;
        }

        private void configure() throws NamingException, HibernateException, ClassNotFoundException, InvocationTargetException {

            LOGGER.debug(">>>>>>>> configure() method called here... IT IS HAPPENING, TAKE NOTE!!!!!!!");

            File file = new File(CustomHibernate.hibernateFilePath);

            Configuration configuration = new Configuration();
            configuration.configure(file);
            //Name tables with lowercase_underscore_separated
            //configuration.setNamingStrategy(ImprovedNamingStrategy.INSTANCE);
            //configuration.addResource(customTypesPropsFileLoc);
            configuration.setInterceptor(new AuditTrailInterceptor());
            //configuration.setInterceptor(new InterceptorClass());

            //StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
            //ServiceRegistry serviceRegistry = serviceRegistryBuilder.build();
            //SessionFactory sessFactory = configuration.buildSessionFactory(serviceRegistry);
            SessionFactory sessFactory = configuration.buildSessionFactory();

            /*   
            StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
        .configure( "hibernate.cfg.xml" )
        .build();

        Metadata metadata = new MetadataSources( standardRegistry )
        .getMetadataBuilder()
        .build();

        return metadata.getSessionFactoryBuilder().build();
        
             */
            setSessionFactory(sessFactory);
        }
    }

    /**
     *
     * @param currentMenuOptionIds
     * @param id
     * @throws MyCustomException
     */
    public void updateNavigation(String currentMenuOptionIds, long id) throws MyCustomException {

        Session session = getSession();

        Transaction transaction = null;
        String errorDetails = "";
        boolean isError = Boolean.TRUE;

        try {

            transaction = session.beginTransaction();

            String hqlUpdate = "update AgNavigation navigation set navigation.currentMenuOptionIds = :currentMenuOptionIds where navigation.id = :id";
            // or String hqlUpdate = "update Customer set name = :newName where name = :oldName";

            TypedQuery updateQuery = session.createQuery(hqlUpdate);
            //org.hibernate.Query updateQuery = session.createSQLQuery(sqlQuery);

            updateQuery.setParameter("currentMenuOptionIds", currentMenuOptionIds);
            updateQuery.setParameter("id", id);

            int entitiesUpdated = updateQuery.executeUpdate();
            transaction.commit();

            isError = Boolean.FALSE;

        } catch (HibernateException he) {

            errorDetails = "hibernate exception while updating in AgNavigation table: " + he.toString();

            LOGGER.error(errorDetails);

            if (transaction != null) {
                transaction.rollback();
            }
        } catch (Exception e) {

            errorDetails = "General exception while updating columns in AgNavigation table: " + e.toString();

            LOGGER.error(errorDetails);

            if (transaction != null) {
                transaction.rollback();
            }

        } finally {
            closeSession(session);
        }

        if (isError) {
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
            throw error;
        }
    }
}
