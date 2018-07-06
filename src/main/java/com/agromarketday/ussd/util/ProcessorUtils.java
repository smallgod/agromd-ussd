package com.agromarketday.ussd.util;

import com.agromarketday.ussd.constant.MenuName;
import com.agromarketday.ussd.constant.NamedConstants;
import com.agromarketday.ussd.constant.NavigationInput;
import com.agromarketday.ussd.database.CustomHibernate;
import com.agromarketday.ussd.database.HibernateUtils;
import com.agromarketday.ussd.datamodel.AgNavigation;
import com.agromarketday.ussd.datamodel.NextNavigation;
import com.agromarketday.ussd.datamodel.json.MenuHistory;
import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.logger.LoggerUtil;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author smallgod
 */
public class ProcessorUtils {

    private static final LoggerUtil logger = new LoggerUtil(ProcessorUtils.class);

//    MenuHistory.Data getCurrentMenuData(MenuHistory menuHistory, String msisdn)
//            throws MyCustomException {
//
//        List<MenuHistory.Data> historyData = menuHistory.getMenuHistoryData();
//        int dataSize = historyData.size();
//
//        MenuHistory.Data currentMenu = historyData.get(dataSize - 1);
//
//        if (MenuName.CONTINUE_SESSION == getMenuName(currentMenu)) {
//            currentMenu = historyData.get(dataSize - 2); //get previous be4 add
//            MenuHistory newMenuHistory//remove continue_sess
//                    = popMostRecentMenuFromHistory(menuHistory);
//
//            AgNavigation navigation = getNavigationByMsisdn(msisdn);
//            navigation.setMenuHistory(newMenuHistory.toString());
//            internalDbAccess.saveOrUpdateEntity(navigation);
//        }
//
//        return currentMenu;
//
//    }
    /**
     * 
     * @param input
     * @param navigation
     * @return
     * @throws MyCustomException 
     */
    public static AgNavigation refreshMenuHistory(
            NavigationInput input,
            AgNavigation navigation ) throws MyCustomException {

        //check if coming from preserve session
        MenuHistory.Data topMenuData = getTopMenuDataInHistory(navigation);
        MenuName topMenu = MenuName.convertToEnum(topMenuData.getMenuName());

        if (topMenu == MenuName.CONTINUE_SESSION) {
            if (input != NavigationInput.CONTINUE) {
                navigation.setMenuHistory("{}");
            }
        }
        return navigation;
    }

    public static MenuHistory.Data getTopMenuDataInHistory(AgNavigation navigation)
            throws MyCustomException {

        String history = navigation.getMenuHistory();
        MenuHistory menuHistory = getMenuHistoryHelper(history);

        menuHistory = getMostRecentMenuFromHistory(menuHistory);
        navigation.setMenuHistory(menuHistory.toString());

        MenuHistory.Data currentMenu = getCurrentMenuData(menuHistory);

        return currentMenu;

    }

    public static MenuHistory.Data getCurrentMenuData(MenuHistory menuHistory)
            throws MyCustomException {

        List<MenuHistory.Data> historyData = menuHistory.getMenuHistoryData();

        if (historyData == null || historyData.isEmpty()) {
            return null;
        }
        int dataSize = historyData.size();

        MenuHistory.Data currentMenu = historyData.get(dataSize - 1);

        return currentMenu;

    }

    public static MenuHistory.Data getSecondLastMenuData(MenuHistory menuHistory)
            throws MyCustomException {

        List<MenuHistory.Data> historyData = menuHistory.getMenuHistoryData();
        int dataSize = historyData.size();

        MenuHistory.Data currentMenu = historyData.get(dataSize - 2);

        return currentMenu;

    }

    public static MenuHistory popMostRecentMenuFromHistory(MenuHistory menuHistory) {

        List<MenuHistory.Data> historyData = menuHistory.getMenuHistoryData();
        int dataSize = historyData.size();
        historyData.remove(dataSize - 1); //pop current menuData
        menuHistory.setMenuHistoryData(historyData);

        return menuHistory;
    }

    public static MenuHistory getMostRecentMenuFromHistory(MenuHistory menuHistory) {

        List<MenuHistory.Data> historyData = menuHistory.getMenuHistoryData();
        int dataSize = historyData.size();
        historyData.get(dataSize - 1);
        menuHistory.setMenuHistoryData(historyData);

        return menuHistory;
    }

    public static MenuName checkIfContinueSession(
            CustomHibernate hibernate,
            String msisdn,
            String newSessionId)
            throws MyCustomException {

        AgNavigation navigation = HibernateUtils
                .getNavigationByMsisdn(hibernate, msisdn);

        int minutes = DateUtils
                .getTimeTakenBetweenTwoDates(navigation.getDateLastModified(),
                        DateUtils.getDateTimeNow(), TimeUnit.MINUTES);

        logger.debug("Minutes taken since last session: " + minutes);

        //Put this in Config File
        return (minutes > NamedConstants.SAVED_SESSION_VALIDITY)
                ? MenuName.MAIN_MENU : MenuName.CONTINUE_SESSION;
    }

    public static MenuHistory getMenuHistoryHelper(String history)
            throws MyCustomException {

        MenuHistory menuHistory
                = GeneralUtils.convertFromJson(history, MenuHistory.class);
        GeneralUtils.toPrettyJson(history);

        return menuHistory;
    }

    public static void logRequestInfo(HttpServletRequest request) {

        logger.debug(">>> Request Content-type   : " + request.getContentType());
        logger.debug(">>> Request Context-path   : " + request.getContextPath());
        logger.debug(">>> Request Content-length : " + request.getContentLength());
        logger.debug(">>> Request Protocol       : " + request.getProtocol());
        logger.debug(">>> Request PathInfo       : " + request.getPathInfo());
        logger.debug(">>> Request Path translated: " + request.getPathTranslated());
        logger.debug(">>> Request Remote Address : " + request.getRemoteAddr());
        logger.debug(">>> Request Remote Port    : " + request.getRemotePort());
        logger.debug(">>> Request Server name    : " + request.getServerName());
        logger.debug(">>> Request Querystring    : " + request.getQueryString());
        logger.debug(">>> Request URL            : " + request.getRequestURL().toString());
        logger.debug(">>> Request URI            : " + request.getRequestURI());
        logger.debug(">>> Request URI last-Index : " + request.getRequestURI().lastIndexOf("/"));
        logger.debug(">>> Request Method-name    : " + request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1));

    }

}
