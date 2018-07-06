package com.agromarketday.ussd;

import com.agromarketday.ussd.constant.ErrorCode;
import com.agromarketday.ussd.constant.NamedConstants;
import com.agromarketday.ussd.controller.JsonAPIServer;
import com.agromarketday.ussd.controller.JsonProcessor;
import com.agromarketday.ussd.controller.XmlProcessor;
import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.logger.LoggerUtil;
import com.agromarketday.ussd.util.GeneralUtils;
import static com.agromarketday.ussd.util.GeneralUtils.writeResponse;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author smallgod
 */
public class RequestHandler extends HttpServlet {

    private static final LoggerUtil logger = new LoggerUtil(RequestHandler.class);
    private static final long serialVersionUID = -7470512984394456827L;

    //But you should also realize that you should never assign any request or session scoped data as 
    //an instance variable of a servlet or filter. It will be shared among all other requests in other sessions. 
    //That's threadunsafe!
    private static JsonProcessor processor; //i think this is not session scoped
    //private static XmlProcessor processor; //i think this is not session scoped
    private static JsonAPIServer jsonApiServer;

    /**
     * The init method is designed to be called only once. It is called when the
     * servlet is first created, and not called again for each user request. So,
     * it is used for one-time initializations, just as with the init method of
     * applets. The servlet is normally created when a user first invokes a URL
     * corresponding to the servlet, but you can also specify that the servlet
     * be loaded when the server is first started. When a user invokes a
     * servlet, a single instance of each servlet gets created, with each user
     * request resulting in a new thread that is handed off to doGet or doPost
     * as appropriate. The init() method simply creates or loads some data that
     * will be used throughout the life of the servlet.
     *
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {

        ServletContext context = getServletContext();

        //appConfigs = (SharedAppConfigIF) context.getAttribute(NamedConstants.APPCONFIGS_ATTR_NAME);
        jsonApiServer = (JsonAPIServer) context.getAttribute(NamedConstants.JSON_API_SERVER_HANDLER);
        //processor = (XmlProcessor) context.getAttribute(NamedConstants.USSD_SERVER_XML_HANDLER);
        processor = (JsonProcessor) context.getAttribute(NamedConstants.USSD_SERVER_JSON_HANDLER);
        
        

    }

    /**
     * The destroy() method is called only once at the end of the life cycle of
     * a servlet. This method gives your servlet a chance to close database
     * connections, halt background threads, write cookie lists or hit counts to
     * disk, and perform other such cleanup activities. After the destroy()
     * method is called, the servlet object is marked for garbage collection
     */
    @Override
    public void destroy() {
        // Finalization code...
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        //response.setContentType("application/xml");
        response.setCharacterEncoding("UTF-8");

        String requestUri = request.getRequestURI();
        String path = requestUri.substring(requestUri.lastIndexOf("/") + 1);

        GeneralUtils.logRequestInfo(request);

        try {

            logger.debug("processRequest called in RequestHandler - path: " + path);

            String responseToServer;
            if (requestUri.equalsIgnoreCase("/api/json")) {

                response.setContentType("application/json");
                responseToServer = jsonApiServer.processClientRequest(request);

            } else {

                if (processor == null) {
                    MyCustomException error = GeneralUtils.getSingleError(ErrorCode.BAD_STATE_ERR, "Server in bad state", "xmlProcessor object is NULL");
                    throw new ServletException("MyCustomException", error);
                }
                responseToServer = processor.processClientRequest(request);
            }

            writeResponse(response, responseToServer);

        } catch (MyCustomException customException) {

            customException.printStackTrace();

            throw new ServletException("MyCustomException", customException);

        } catch (NullPointerException npe) {

            npe.printStackTrace();

            String errorDescription = "Error! Sorry, your request cannot be processed at the time.";
            String errorDetails = "A null pointer exception occurred, check the requests and other objects: " + npe.toString();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.PROCESSING_ERR, errorDescription, errorDetails);

            throw new ServletException("MyCustomException", error);

        } catch (ClassCastException ex) {

            ex.printStackTrace();

            String errorDescription = "Error! Sorry, your request cannot be processed at the time.";
            String errorDetails = "A ClassCastException occurred: " + ex.toString();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.PROCESSING_ERR, errorDescription, errorDetails);

            throw new ServletException("MyCustomException", error);

        } catch (Exception ex) {

            ex.printStackTrace();

            String errorDescription = "Error! Sorry, your request cannot be processed at the time.";
            String errorDetails = "A General exception occurred: " + ex.toString();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.PROCESSING_ERR, errorDescription, errorDetails);

            throw new ServletException("MyCustomException", error);

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

        logger.warn("Request GET method instead of POST from: " + request.getRequestURL());
        processRequest(request, response);
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
        processRequest(request, response);
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

}
