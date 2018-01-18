
package com.agromarketday.ussd.exception;

import com.agromarketday.ussd.datamodel.ErrorResponse;
import com.agromarketday.ussd.logger.LoggerUtil;
import com.agromarketday.ussd.util.GeneralUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author smallgod
 */
public class ErrorResponseHandler extends HttpServlet {

    private static final long serialVersionUID = -1884977557720523777L;

    private static final LoggerUtil logger = new LoggerUtil(ErrorResponseHandler.class);

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processError(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");

        logger.debug("ErrorResponseHandler.java called!");

        Throwable throwable = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Class className = (Class) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION_TYPE);//class name of the exception instance that caused the error (or null)
        String errorMessage = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);//error message
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);//status code of the error (e.g. 404, 500 etc.)
        String servletName = (String) request.getAttribute(RequestDispatcher.ERROR_SERVLET_NAME);//The Servlet name of the servlet that the errored request was dispatched to
        String requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);//URI of the errored request

        MyCustomException customException = (MyCustomException) throwable.getCause();

        //set the HTTP status code here
        //response.setStatus(customException.getHTTPStatusCode());
        ErrorResponse errorResponse = customException.createErrorResponse();

        //we need to cater for error scenario where we fail to cast exception or to marshal into the XML
        logger.debug("Throwable -getMessage      : " + throwable.getMessage());
        logger.debug("ClassName                  : " + className.getName());
        logger.debug("errorMessage               : " + errorMessage);
        logger.debug("statusCode                 : " + statusCode);
        logger.debug("servletName                : " + servletName);
        logger.debug("requestURI                 : " + requestUri);

        String jsonErrorResponse;

        try {

            jsonErrorResponse = GeneralUtils.convertToJson(errorResponse, ErrorResponse.class);

        } catch (MyCustomException ex) {

            jsonErrorResponse = "{\n"
                    + "  \"success\": false,\n"
                    + "  \"data\": {\n"
                    + "    \"request_id\": \"\",\n"
                    + "    \"errors\": [\n"
                    + "      {\n"
                    + "        \"error_code\": \"PROCESSING_ERROR\",\n"
                    + "        \"description\": \"Error! Sorry, your request cannot be processed at the moment.\",\n"
                    + "        \"additional_details\": \"An error occurred while trying to convert a meaningful error object to JSON.\"\n"
                    + "      }\n"
                    + "    ]\n"
                    + "  }\n"
                    + "}";

            logger.error("Error! Failed to send back the error response. "
                    + "an error occurred while trying to convert the error object to JSON: " + ex.getMessage());

        } catch (Exception ex) {

            jsonErrorResponse = "{\n"
                    + "  \"success\": false,\n"
                    + "  \"data\": {\n"
                    + "    \"request_id\": \"\",\n"
                    + "    \"errors\": [\n"
                    + "      {\n"
                    + "        \"error_code\": \"PROCESSING_ERROR\",\n"
                    + "        \"description\": \"Error! Sorry, your request cannot be processed at the moment.\",\n"
                    + "        \"additional_details\": \"An error occurred while trying to convert a meaningful error object to JSON.\"\n"
                    + "      }\n"
                    + "    ]\n"
                    + "  }\n"
                    + "}";

            logger.error("Error! Failed to send back the error response. "
                    + "an error occurred while trying to convert the error object to JSON: " + ex.getMessage());
        }

        logger.error("JSON error-response: " + jsonErrorResponse);

        response.setStatus(HttpURLConnection.HTTP_OK); //do this for some clients that cannot handle HTTP status code 500    

        try (PrintWriter out = response.getWriter()) {
            out.write(jsonErrorResponse);
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processError(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processError(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>


    /*PrintWriter out = response.getWriter();
            out.write("<html><head><title>Exception/Error Details</title></head><body>");
            if (statusCode != 500) {
            out.write("<h3>Error Details</h3>");
            out.write("<strong>Status Code</strong>:" + statusCode + "<br>");
            out.write("<strong>Requested URI</strong>:" + requestUri);
            } else {
            out.write("<h3>Exception Details</h3>");
            out.write("<ul><li>Servlet Name:" + servletName + "</li>");
            out.write("<li>Exception Name:" + throwable.getClass().getName() + "</li>");
            out.write("<li>Requested URI:" + requestUri + "</li>");
            out.write("<li>Exception Message:" + throwable.getMessage() + "</li>");
            out.write("</ul>");
            }
            
            out.write("<br><br>");
            out.write("<a href=\"index.html\">Home Page</a>");
            out.write("</body></html>");*/
}
