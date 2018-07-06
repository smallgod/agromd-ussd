package com.agromarketday.ussd.controller;

import com.agromarketday.ussd.constant.APIContentType;
import com.agromarketday.ussd.constant.APIMethodName;
import com.agromarketday.ussd.constant.ErrorCode;
import com.agromarketday.ussd.constant.NamedConstants;
import com.agromarketday.ussd.database.CustomHibernate;
import com.agromarketday.ussd.datamodel.json.GetStatsRequest;
import com.agromarketday.ussd.datamodel.json.GetStatsResponse;
import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.logger.LoggerUtil;
import com.agromarketday.ussd.util.GeneralUtils;
import static com.agromarketday.ussd.util.GeneralUtils.getMethodName;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import com.agromarketday.ussd.database.HibernateUtils;
import com.agromarketday.ussd.datamodel.AgSession;
import com.agromarketday.ussd.datamodel.json.MenuHistory;
import com.agromarketday.ussd.exception.EmptyStringException;
import com.agromarketday.ussd.sharedInterface.HttpUnitController;
import com.agromarketday.ussd.util.DateUtils;
import java.util.HashSet;
import java.util.Set;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import static com.agromarketday.ussd.util.GeneralUtils.convertFromJson;
import static com.agromarketday.ussd.util.GeneralUtils.getNetworkId;
import java.util.LinkedHashSet;

/**
 *
 * @author smallgod
 */
@WebServlet(asyncSupported = true)
public class JsonAPIServer implements HttpUnitController {

    private static final LoggerUtil logger = new LoggerUtil(JsonAPIServer.class);
    private static final long serialVersionUID = 6806417914014484081L;
    private final CustomHibernate customHibernate;

    public JsonAPIServer(CustomHibernate customHibernate) {
        this.customHibernate = customHibernate;
    }

    @Override
    public String processClientRequest(HttpServletRequest request)
            throws MyCustomException, NullPointerException {

        //logRequestInfo(request);
        //printRequesterHeaderInfo(request);
        final String jsonRequest = GeneralUtils.getJsonStringFromRequest(request);

        System.out.println("---------- Request --------\n"
                + GeneralUtils.toPrettyJsonFormat(jsonRequest));

        String methodName;
        String errorDetails;
        String jsonResponse = "{}";

        try {

            methodName = getMethodName(jsonRequest, APIContentType.JSON);
            APIMethodName methodNameEnum = APIMethodName.convertToEnum(methodName);

            switch (methodNameEnum) {

                case GET_STATS:
                    jsonResponse = getStats(jsonRequest);
                    break;

                default:
                    break;
            }
            System.out.println("---------- Response --------\n"
                    + GeneralUtils.toPrettyJsonFormat(jsonResponse));

            return jsonResponse;

        } catch (NullPointerException npe) {

            errorDetails = npe.getMessage();
            logger.error("Error! NullPointerException, Failed to parse the XML: " + errorDetails);
            npe.printStackTrace();

        } catch (ClassCastException exc) {

            errorDetails = exc.getMessage();
            logger.error("Error!  ClassCastException: " + errorDetails);
            exc.printStackTrace();

        } catch (EmptyStringException exc) {

            errorDetails = exc.getMessage();
            logger.error("Error! EmptyStringException: " + errorDetails);
            exc.printStackTrace();

        } catch (Throwable exc) {

            errorDetails = exc.getMessage();
            logger.error("Error! : " + errorDetails);
            exc.printStackTrace();
        }

        MyCustomException error
                = GeneralUtils.getSingleError(ErrorCode.INTERNAL_ERR,
                        NamedConstants.GENERIC_ERR_DESC, errorDetails);
        throw error;

    }

    /**
     *
     * @param reportPaymentsRequest
     * @return
     */
    String getStats(String reportPaymentsRequest)
            throws MyCustomException, Exception {

        GetStatsResponse response = new GetStatsResponse();

        GetStatsRequest getStatsRequest
                = convertFromJson(reportPaymentsRequest, GetStatsRequest.class);

        String contact = getStatsRequest.getParams().getMsisdn();
        String msisdn = "";
        if (!(contact == null || contact.isEmpty())) {
            msisdn = GeneralUtils.formatMSISDN(contact);
        }

        String startDateStr = getStatsRequest.getParams().getStartDate();
        String endDateStr = getStatsRequest.getParams().getEndDate();

        LocalDate stDate = DateUtils.convertStringToLocalDate(startDateStr,
                NamedConstants.DATE_DASH_FORMAT);
        LocalDate eDate = DateUtils.convertStringToLocalDate(endDateStr,
                NamedConstants.DATE_DASH_FORMAT);

        LocalDateTime startDate = DateUtils.convertLocalDateToLocalDateTime(stDate);
        LocalDateTime endDate = DateUtils.convertLocalDateToLocalDateTime(eDate);
        endDate = DateUtils.getEndOfThisDay(endDate);
        //endDate.withTime(23, 59, 59, 0); //end of day

        Set<AgSession> sessionList = HibernateUtils
                .getSessionsByMsisdnDates(customHibernate, msisdn, startDate, endDate);

        Set<GetStatsResponse.Data> sessionData = new LinkedHashSet<>();
        for (AgSession aSession : sessionList) {

            msisdn = aSession.getClient().getMsisdn();

            GetStatsResponse.Data responseSession = response.new Data();
            Set<GetStatsResponse.Data.VisitedMenu> visitedMenus = new HashSet<>();

            MenuHistory history = GeneralUtils
                    .getMenuHistoryHelper(aSession.getMenuHistory());

            int order = 1;
            for (MenuHistory.Data historyData : history.getMenuHistoryData()) {

                GetStatsResponse.Data.VisitedMenu visitedMenu
                        = responseSession.new VisitedMenu();
                visitedMenu.setMenu(historyData.getMenuName());
                visitedMenu.setOrder(order);
                visitedMenus.add(visitedMenu);

                order++;
            }

            LocalDateTime startTime = aSession.getSessionStartTime();
            LocalDateTime endTime = aSession.getSessionEndTime();

            int sessionLength = DateUtils
                    .getTimeTakenBetweenTwoDates(startTime, endTime,
                            TimeUnit.SECONDS);

            responseSession.setMsisdn(msisdn);
            responseSession.setNetwork(getNetworkId(msisdn).getValue());
            responseSession.setSessionLength(sessionLength);
            responseSession.setStartTime(DateUtils
                    .convertLocalDateTimeToString(aSession.getSessionStartTime(),
                            NamedConstants.DATE_TIME_DASH_FORMAT));
            responseSession.setEndTime(DateUtils
                    .convertLocalDateTimeToString(aSession.getSessionEndTime(),
                            NamedConstants.DATE_TIME_DASH_FORMAT));
            responseSession.setSessionId(aSession.getSessionId());
            responseSession.setVisitedMenus(visitedMenus);

            sessionData.add(responseSession);
        }

        response.setData(sessionData);

        String jsonResponse = GeneralUtils
                .convertToJson(response, GetStatsResponse.class);

        return jsonResponse;
    }

}
