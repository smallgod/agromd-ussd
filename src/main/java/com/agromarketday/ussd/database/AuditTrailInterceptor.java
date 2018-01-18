package com.agromarketday.ussd.database;

/**
 *
 * @author smallgod
 */
import com.agromarketday.ussd.constant.NamedConstants;
import com.agromarketday.ussd.datamodel.AgClient;
import com.agromarketday.ussd.logger.LoggerUtil;
import com.agromarketday.ussd.sharedInterface.Auditable;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.joda.time.LocalDateTime;

public class AuditTrailInterceptor extends EmptyInterceptor {

    private static final LoggerUtil logger = new LoggerUtil(AuditTrailInterceptor.class);
    private static final long serialVersionUID = 5997616111315960747L;

    public AuditTrailInterceptor() {
    }

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        logger.debug("Delete event");
    }

    @Override
    public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {

        logger.debug("onLoad event");
        return true;
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {

        logger.debug("onSave called");

        if (entity instanceof Auditable) {
            setValue(state, propertyNames, NamedConstants.PROPNAME_CREATED_BY, ((Auditable) entity).getUsername());
            setValue(state, propertyNames, NamedConstants.PROPNAME_CREATED_ON, new LocalDateTime());

            return true;
        }

        return false;
    }

    /**
     * called before commit into database
     *
     * @param entities
     */
    @Override
    public void preFlush(Iterator entities) {
        
        int i = 0;
        while (entities.hasNext()) {
            Object entity = entities.next();

            if (entity instanceof AgClient) {
                AgClient client = (AgClient) entity;
                logger.debug("preFlush().. about to commit an instance of AgClient: " + client.getId() + ", lang: " + client.getLanguage().getLangCode());

            }
            logger.debug("preFlush: " + (++i) + " : " + entity);
        }
    }

    /**
     * Called after committed into database This method is called after a flush
     * has occurred and an object has been updated in memory
     *
     * @param entities
     */
    @Override
    public void postFlush(Iterator entities) {

        logger.debug("postFlush operation, after commiting to db  >> postFlush event");
        logger.debug("preFlush: List of objects to flush... ");

        int i = 0;
        while (entities.hasNext()) {

            Object entity = entities.next();

            if (entity instanceof AgClient) {
                AgClient client = (AgClient) entity;
                logger.debug("postFlush().. about to commit an instance of AgClient: " + client.getId() + ", lang: " + client.getLanguage().getLangCode());

            }

            logger.info("postFlush: " + (++i) + " : " + entity);
        }
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {

        logger.debug("Update Operation >> onFlushDirty event");

        if (entity instanceof Auditable) {

            logger.debug("this is where we add some audit trail but for now leave it out till we get a proper way to deal with the 2 ever increasing string length");

            setValue(currentState, propertyNames, NamedConstants.PROPNAME_LAST_MODIFIED_BY, ((Auditable) entity).getUsername());
            setValue(currentState, propertyNames, NamedConstants.PROPNAME_DATE_LAST_MODIFIED, new LocalDateTime());

            /*
            updateValue(currentState, propertyNames, NamedConstants.PROPNAME_DATE_MODIFIED_HISTORY, ((Auditable) entity).getUsername());
            updateValue(currentState, propertyNames, NamedConstants.PROPNAME_MODIFIED_BY_HISTORY, ((Auditable) entity).getUsername());
             */
            return true;
        }

        return false;
    }

    /**
     * Set a completely new value for a property of an auditable entity
     *
     * @param currentState
     * @param propertyNames
     * @param propertyToSet
     * @param value
     */
    private void setValue(Object[] currentState, String[] propertyNames, String propertyToSet, Object value) {

        int index = Arrays.asList(propertyNames).indexOf(propertyToSet);

        if (index >= 0) {
            currentState[index] = value;
        }
    }

    /**
     * Update (add delimeter and then new value) the property of an auditable
     * entity
     *
     * @param currentState
     * @param propertyNames
     * @param propertyToSet
     * @param value
     */
    private void updateValue(Object[] currentState, String[] propertyNames, String propertyToSet, Object value) {

        int index = Arrays.asList(propertyNames).indexOf(propertyToSet);

        if (index >= 0) {

            Object obj = currentState[index];
            String strObj = "";

            if (obj instanceof String) {
                strObj = (String) obj;
            }

            currentState[index] = (strObj + "|" + value);

            logger.debug("val: " + currentState[index]);
        }
    }
}
