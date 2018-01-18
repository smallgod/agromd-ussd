/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agromarketday.ussd.database;

import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.logger.LoggerUtil;
import com.agromarketday.ussd.util.GeneralUtils;
import static com.agromarketday.ussd.util.GeneralUtils.convertListToSet;
import com.agromarketday.ussd.util.IDCreator;
import java.util.List;
import java.util.Set;

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

}
