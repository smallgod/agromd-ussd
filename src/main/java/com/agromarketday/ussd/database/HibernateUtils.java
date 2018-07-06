/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agromarketday.ussd.database;

import com.agromarketday.ussd.datamodel.AgClient;
import com.agromarketday.ussd.datamodel.AgNavigation;
import com.agromarketday.ussd.datamodel.AgSession;
import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.logger.LoggerUtil;
import com.agromarketday.ussd.util.GeneralUtils;
import static com.agromarketday.ussd.util.GeneralUtils.convertListToSet;
import com.agromarketday.ussd.util.IDCreator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

/**
 * Class contains all the helper methods when dealing with Hibernate
 *
 * @author smallgod
 */
public class HibernateUtils {

    private static final LoggerUtil LOG = new LoggerUtil(HibernateUtils.class);

    /**
     * Generate a customer ID
     *
     * @param customHibernate
     * @param classType
     * @param idColumnName
     * @return
     * @throws com.agromarketday.ussd.exception.MyCustomException
     */
    public static synchronized int generateIntegerID(CustomHibernate customHibernate, Class classType, String idColumnName) throws MyCustomException {

        List<Integer> idList = customHibernate.fetchOnlyColumn(classType, idColumnName);

        Set<Integer> set = convertListToSet(idList);

        int generatedId;

        do {
            generatedId = IDCreator.GenerateInt();
        } while (set.contains(generatedId));

        return generatedId;
    }

    /**
     * Generate the FileID To-Do -> Method fetches entire file list for each
     * call, we need to come up with a better way of doing this.
     *
     * @param customHibernate
     * @param classType
     * @param idColumnName
     * @return
     * @throws com.agromarketday.ussd.exception.MyCustomException
     */
    public static synchronized long generateLongID(CustomHibernate customHibernate, Class classType, String idColumnName) throws MyCustomException {

        List<Long> idList = customHibernate.fetchOnlyColumn(classType, idColumnName);
        LOG.debug("Records fetched size: " + idList.size());

        Set<Long> setOfIds = GeneralUtils.convertListToSet(idList);

        long generatedId;

        do {
            generatedId = IDCreator.GenerateLong();
        } while (setOfIds.contains(generatedId));

        return generatedId;
    }

    public static Set<AgSession> getSessionsByMsisdnDates(
            CustomHibernate customHibernate,
            String msisdn,
            LocalDateTime startDate,
            LocalDateTime endDate)
            throws MyCustomException {

        Map<String, Object> resourceProps = new HashMap<>();

        String query = AgSession.FETCH_SESSION_BY_DATES;
        resourceProps.put("startDate", new HashSet<>(Arrays.asList(startDate)));
        resourceProps.put("endDate", new HashSet<>(Arrays.asList(endDate)));

        if (!msisdn.isEmpty()) {
            resourceProps.put("msisdn", new HashSet<>(Arrays.asList(msisdn)));
            query = AgSession.FETCH_SESSION_BY_MSISDN_DATES;
        }
        
        Set<AgSession> sessions = customHibernate
                .fetchEntities(query, resourceProps);

        return sessions;
    }

    public static Set<AgSession> getSessionsByMsisdn(
            CustomHibernate customHibernate, String msisdn)
            throws MyCustomException {

        Map<String, Object> resourceProps = new HashMap<>();
        resourceProps.put("msisdn", new HashSet<>(Arrays.asList(msisdn)));

        Set<AgSession> sessions = customHibernate
                .fetchEntities(AgSession.FETCH_SESSION_BY_MSISDN, resourceProps);

        return sessions;

    }

    public static AgClient getClient(
            CustomHibernate customHibernate,
            String msisdn)
            throws MyCustomException {

        Map<String, Object> resourceProps = new HashMap<>();
        resourceProps.put("msisdn", new HashSet<>(Arrays.asList(msisdn)));

        Set<AgClient> clients = customHibernate
                .fetchEntities(AgClient.FETCH_CLIENT_MSISDN, resourceProps);

        if (clients == null || clients.isEmpty()) {
            return null;
        }
        return (AgClient) clients.toArray()[0];
    }

    public static AgSession getSessionById(
            CustomHibernate customHibernate, String sessionId)
            throws MyCustomException {

        Map<String, Object> resourceProps = new HashMap<>();
        resourceProps.put("sessionId", new HashSet<>(Arrays.asList(sessionId)));

        Set<AgSession> sessions = customHibernate
                .fetchEntities(AgSession.FETCH_SESSION_BY_SESSIONID, resourceProps);

        if (sessions == null || sessions.isEmpty()) {
            return null;
        }
        return (AgSession) sessions.toArray()[0];

    }

    public static AgNavigation getNavigationByMsisdn(
            CustomHibernate customHibernate, String msisdn)
            throws MyCustomException {

        Map<String, Object> resourceProps = new HashMap<>();
        resourceProps.put("msisdn", new HashSet<>(Arrays.asList(msisdn)));
        Set<AgNavigation> navigations = customHibernate
                .fetchEntities(AgNavigation.FETCH_NAVIG_BY_MSISDN, resourceProps);

        if (navigations == null || navigations.isEmpty()) {
            return null;
        }

        return (AgNavigation) navigations.toArray()[0];
    }

    public static AgNavigation getNavigationBySession(
            CustomHibernate customHibernate, String sessionId)
            throws MyCustomException {

        Map<String, Object> resourceProps = new HashMap<>();
        resourceProps.put("sessionId", new HashSet<>(Arrays.asList(sessionId)));
        Set<AgNavigation> navigations = customHibernate
                .fetchEntities(AgNavigation.FETCH_NAVIG_BY_SESSIONID, resourceProps);

        if (navigations == null || navigations.isEmpty()) {
            return null;
        }
        return (AgNavigation) navigations.toArray()[0];
        //return clients.toArray(new AgNavigation[clients.size()])[0];

    }

}
