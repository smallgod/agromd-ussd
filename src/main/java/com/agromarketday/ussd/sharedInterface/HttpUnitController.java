
package com.agromarketday.ussd.sharedInterface;

import com.agromarketday.ussd.exception.MyCustomException;
import javax.servlet.http.HttpServletRequest;

/**
 * Different units/modules that are accessed via HTTP in this application may
 * process similar http API calls differently
 *
 * HttpUnitController
 *
 * @author smallgod
 */
public interface HttpUnitController {

    //public String processClientRequest(HttpServletRequest request) throws JsonProcessingException, IOException, SchedulerException, ServletException;
    public String processClientRequest(HttpServletRequest request) throws MyCustomException, NullPointerException;

}
