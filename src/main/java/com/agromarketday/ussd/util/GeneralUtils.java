/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agromarketday.ussd.util;

import com.agromarketday.ussd.constant.APIContentType;
import com.agromarketday.ussd.constant.EntityName;
import com.agromarketday.ussd.constant.ErrorCode;
import com.agromarketday.ussd.constant.NamedConstants;
import com.agromarketday.ussd.constant.NetworkId;
import com.agromarketday.ussd.datamodel.AdAPIRequest;
import com.agromarketday.ussd.datamodel.AgClient;
import com.agromarketday.ussd.datamodel.json.MenuHistory;
import com.agromarketday.ussd.exception.EmptyStringException;
import com.agromarketday.ussd.exception.ErrorWrapper;
import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.logger.LoggerUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.openide.util.MapFormat;

/**
 *
 * @author smallgod
 */
public class GeneralUtils {

    private static final LoggerUtil logger = new LoggerUtil(GeneralUtils.class);

    private static final Type stringMapType = new TypeToken<Map<String, Object>>() {
    }.getType();

    private static final Type mapInMapType = new TypeToken<Map<String, Map<String, String>>>() {
    }.getType();

    /**
     * The following method shuts down an ExecutorService in two phases, first
     * by calling shutdown to reject incoming tasks, and then calling
     * shutdownNow, if necessary, to cancel any lingering tasks (timeToWait
     * time) elapses.
     *
     * @param pool the executor service pool
     * @param timeToWait
     * @param timeUnit
     */
    public static void shutdownProcessor(final ExecutorService pool, long timeToWait, TimeUnit timeUnit) {

        logger.info("Executor pool waiting for tasks to complete");
        pool.shutdown(); // Disable new tasks from being submitted

        try {

            boolean terminatedOK = pool.awaitTermination(timeToWait, timeUnit);

            // Wait a while for existing tasks to terminate
            if (!terminatedOK) {

                // Wait a while for tasks to respond to being cancelled
                terminatedOK = pool.awaitTermination(++timeToWait, timeUnit);

                if (!terminatedOK) {
                    logger.warn("Executor waiting for pending tasks, another " + timeToWait + " " + timeUnit.toString() + "...");

                    pool.shutdownNow(); // Cancel currently executing tasks
                    logger.warn("Executor ShutdownNow with pending tasks");
                }

            } else {
                logger.info("Executor pool completed all tasks and has shut "
                        + "down normally");
            }
        } catch (InterruptedException ie) {
            logger.error("Executor pool shutdown error: " + ie.getMessage());
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();

            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    /**
     *
     * @param errorCode
     * @param errorDescription
     * @param errorDetails
     * @return
     */
    public static MyCustomException getSingleError(ErrorCode errorCode, String errorDescription, String errorDetails) {

        ErrorWrapper errorWrapper = new ErrorWrapper();
        Set<ErrorWrapper> errors = new HashSet<>();
        errorWrapper.setErrorCode(errorCode);
        errorWrapper.setDescription(errorDescription);
        errorWrapper.setErrorDetails(errorDetails);

        errors.add(errorWrapper);

        return new MyCustomException("", errors);
    }

    public static String readXMLStream(InputStream xmlInputStream) throws IOException, EmptyStringException {

        BufferedReader in = new BufferedReader(new InputStreamReader(xmlInputStream));

        String inputLine;
        String xmlResponse = "";

        while ((inputLine = in.readLine()) != null) {
            xmlResponse += inputLine;
        }

        if (xmlResponse.trim().isEmpty()) {
            throw new EmptyStringException("XML string is empty");
        }
        return xmlResponse.trim();
    }

    public static String readStream(InputStream inputStream) throws IOException, EmptyStringException {

        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

        String inputLine;
        String inputStreamString = "";

        while ((inputLine = in.readLine()) != null) {
            inputStreamString += inputLine;
        }

        if (inputStreamString.trim().isEmpty()) {
            throw new EmptyStringException("string is empty");
        }
        return inputStreamString.trim();
    }

    /**
     * This method tries to find a solution to extract the JSON from the worst
     * API I have ever seen - oh mehn these guys cheiiii. Anyhow, work had to
     * move on.
     *
     * @param inputStreamString
     * @return
     */
    public static String extractJsonFromSMSOneWorstAPIever(String inputStreamString) {

        //string looks like this:
        ////------------------------------c6514c3a96aeContent-Disposition: form-data; name="ussdTransactionObject"{"transactionId":"13010904","transactionTime":"2018-01-17 16:07:13","serviceCode":"236","ussdDailedCode":"*236#","msisdn":"256774983602","ussdRequestString":"continue","userInput":"continue","response":"false","network":"MTN-UG","newRequest":true}------------------------------c6514c3a96ae--
        int startIndex = inputStreamString.indexOf("name=\"ussdTransactionObject\"");
        String extractedString = inputStreamString.substring(startIndex + 1).trim();

        int jsonStart = extractedString.indexOf("{");
        int jsonEnd = extractedString.indexOf("}");

        String jsonRequest = extractedString.substring(jsonStart, jsonEnd + 1);

        return jsonRequest;
    }

    public static String readJsonPart(HttpServletRequest httpServletRequest)
            throws ServletException, IOException {

        Collection<Part> multiParts = httpServletRequest.getParts();
        InputStream inputStream;

        for (Part part : multiParts) {

            //String partName = part.getName();
            //inputStream = httpServletRequest.getPart(partName).getInputStream();
            inputStream = part.getInputStream();
            int biteSize = inputStream.available();
            byte[] bites = new byte[biteSize];
            inputStream.read(bites);

            for (String temp : part.getHeader("content-disposition").split(";")) {

                logger.debug("Temp part: " + temp);

                if (temp.trim().startsWith("name")) {
                    String json = temp.substring(temp.indexOf("=\"ussdTransactionObject\"") + 1).trim();

                    return json;
                }
            }
        }
        return "{}";
    }

    /**
     *
     * @param entityName
     * @param isCollection
     * @return
     */
    public static Type getEntityType(EntityName entityName, boolean isCollection) {

        Type entityCollectionType = null;
        Type singleCollectionType = null;

        switch (entityName) {

            case AG_CLIENT:
                //if they are many adverts (multiple) we need to read them in as a map????? not so ????

                singleCollectionType = new TypeToken<AgClient>() {
                }.getType();
                entityCollectionType = new TypeToken<Set<AgClient>>() {
                }.getType();

                break;

            default:
                logger.warn("Unknown Entity: " + entityName + ", bad things bound to happen!!! ");
                break;
        }

        if (isCollection) {
            return entityCollectionType;
        }

        return singleCollectionType;
    }

    /**
     *
     * @param entityName
     * @return
     * @throws com.agromarketday.ussd.exception.MyCustomException
     */
    public static Class getEntityClass(EntityName entityName) throws MyCustomException {

        Class entityClass;

        switch (entityName) {

            case AG_CLIENT:

                entityClass = AgClient.class;
                break;

            default:

                String errorDetails = "Can't get Entity Class, Unknown Entity: " + entityName + ", bad things bound to happen!!! ";
                MyCustomException error = GeneralUtils.getSingleError(ErrorCode.PROCESSING_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
                throw error;
        }

        return entityClass;
    }

    /**
     * Convert a JSON string to pretty print version
     *
     * @param jsonString
     * @return a well formatted JSON string
     */
    public static String toPrettyJsonOLD(String jsonString) {

        JsonParser parser = new JsonParser();

        JsonObject json = parser.parse(jsonString).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(json);

        return prettyJson;
    }

    /**
     * Print out pretty json
     *
     * @param jsonString
     * @return
     */
    public static String toPrettyJson(String jsonString) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(jsonString);
        String prettyJson = gson.toJson(je);

        logger.debug(prettyJson);

        return prettyJson;

    }

    /**
     * Get the JSON string from an HTTPServerletRequest
     *
     * @param request
     * @return
     */
    public static String extractJson(HttpServletRequest request) {

        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        String s;

        try {

            reader = request.getReader();

            do {

                s = reader.readLine();

                if (s != null) {
                    sb.append(s);
                } else {
                    break;
                }

            } while (true);

        } catch (IOException ex) {
            logger.error("IO Exception, failed to decode JSON string from request: " + ex.getMessage());
            //throw new MyCustomException("IO Exception occurred", ErrorCode.CLIENT_ERR, "Failed to decode JSON string from the HTTP request: " + ex.getMessage(), ErrorCategory.CLIENT_ERR_TYPE);

        } catch (Exception ex) {
            logger.error("General Exception, failed to decode JSON string from request: " + ex.getMessage());
            //throw new MyCustomException("General Exception occurred", ErrorCode.CLIENT_ERR, "Failed to decode JSON string from the HTTP request: " + ex.getMessage(), ErrorCategory.CLIENT_ERR_TYPE);

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    logger.error("exception closing buffered reader: " + ex.getMessage());
                }
            }
        }

        return sb.toString();
    }

    /**
     * Get the method name value with key "method" if Json request or enclosing
     * method root name if xml request
     *
     * @param jsonRequest
     * @param apiType
     * @return
     * @throws IOException
     */
    public static String getMethodName(String jsonRequest, APIContentType apiType) throws JsonProcessingException, IOException {

        String methodName = "";

        switch (apiType) {

            case JSON:

                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(jsonRequest);
                methodName = rootNode.path(NamedConstants.JSON_METHOD_NODENAME).asText();

                break;

            case XML:
                break;

        }

        //APIMethodName methodNameEnum = APIMethodName.convertToEnum(methodName);
        return methodName;
    }

    /**
     * Write a response to calling server client
     *
     * @param response
     * @param responseToWrite
     * @throws com.library.customexception.MyCustomException
     */
    public static void writeResponse(HttpServletResponse response, String responseToWrite) throws MyCustomException {

        try (PrintWriter out = response.getWriter()) {

            out.write(responseToWrite);
            out.flush();
            response.flushBuffer();

        } catch (IOException ex) {

            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.COMMUNICATION_ERR, "Error writing back client response", "Error writing response to client: " + ex.getMessage());
            throw error;

        }
    }

    /**
     * Return JSON string representation of given object
     *
     * @param <T>
     * @param objectToConvert
     * @param objectType
     * @return
     * @throws com.library.customexception.MyCustomException
     */
    public static <T> String convertToJson(Object objectToConvert, Class<T> objectType) throws MyCustomException {

        try {
            //Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setPrettyPrinting();

            //gsonBuilder.excludeFieldsWithoutExposeAnnotation();
            //gsonBuilder.registerTypeAdapter(AdScreenOwner.class, new MyGsonTypeAdapter<AdScreenOwner>());
            GraphAdapterBuilder graphAdapterBuilder = new GraphAdapterBuilder();
            graphAdapterBuilder
                    //.addType(Author.class)
                    //.addType(AdScreenOwner.class)
                    //.addType(AdResource.class)
                    //.addType(AdProgram.class)
                    .registerOn(gsonBuilder);
            gsonBuilder.registerTypeAdapter(LocalDate.class, new JodaGsonLocalDateConverter());
            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new JodaGsonLocalDateTimeConverter());
            gsonBuilder.registerTypeAdapter(LocalTime.class, new JodaGsonLocalTimeConverter());

            Gson gson = gsonBuilder.create();

            return gson.toJson(objectToConvert, objectType);

        } catch (IllegalArgumentException iae) {

            String errorDescription = "Error! Sorry, request cannot be processed now, please try again later";
            String errorDetails = "IllegalArgumentException occurred trying to convert to JSON: " + iae.getMessage();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.PROCESSING_ERR, errorDescription, errorDetails);
            throw error;
        }
    }

    /**
     * Return JSON string representation of given object
     *
     * @param <T>
     * @param objectToConvert
     * @param objectType
     * @return
     * @throws com.library.customexception.MyCustomException
     */
    public static <T> String convertToJson(Object objectToConvert, Type objectType) throws MyCustomException {

        try {
            //Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setPrettyPrinting();

            //gsonBuilder.registerTypeAdapter(AdScreenOwner.class, new MyGsonTypeAdapter<AdScreenOwner>());
            //gsonBuilder.excludeFieldsWithoutExposeAnnotation();
            GraphAdapterBuilder graphAdapterBuilder = new GraphAdapterBuilder();
            graphAdapterBuilder
                    //.addType(Author.class)
                    //.addType(AdScreenOwner.class)
                    //.addType(AdProgram.class)
                    .registerOn(gsonBuilder);
            gsonBuilder.registerTypeAdapter(LocalDate.class, new JodaGsonLocalDateConverter());
            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new JodaGsonLocalDateTimeConverter());
            gsonBuilder.registerTypeAdapter(LocalTime.class, new JodaGsonLocalTimeConverter());

            Gson gson = gsonBuilder.create();

            return gson.toJson(objectToConvert, objectType);

        } catch (IllegalArgumentException iae) {

            String errorDescription = "Error! Sorry, request cannot be processed now, please try again later";
            String errorDetails = "IllegalArgumentException occurred trying to convert to JSON: " + iae.getMessage();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.PROCESSING_ERR, errorDescription, errorDetails);
            throw error;
        }
    }

    /**
     * Return Object from JSON string
     *
     * @param <T>
     * @param stringToConvert
     * @param objectType
     * @return
     * @throws com.library.customexception.MyCustomException
     */
    public static <T> T convertFromJson(String stringToConvert, Class<T> objectType) throws MyCustomException {

        ErrorWrapper errorWrapper = new ErrorWrapper(); //incase it happens
        Set<ErrorWrapper> errors = new HashSet<>();

        //Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();

        //gsonBuilder.excludeFieldsWithoutExposeAnnotation();
        //gsonBuilder.registerTypeAdapter(AdScreenOwner.class, new MyGsonTypeAdapter<AdScreenOwner>());
        GraphAdapterBuilder graphAdapterBuilder = new GraphAdapterBuilder();
        graphAdapterBuilder
                //.addType(Author.class)
                //.addType(AdScreenOwner.class)
                //.addType(AdProgram.class)
                .registerOn(gsonBuilder);
        gsonBuilder.registerTypeAdapter(LocalDate.class, new JodaGsonLocalDateConverter());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new JodaGsonLocalDateTimeConverter());
        gsonBuilder.registerTypeAdapter(LocalTime.class, new JodaGsonLocalTimeConverter());

        Gson gson = gsonBuilder.create();

        T returnObj = null;

        try {
            returnObj = gson.fromJson(stringToConvert.trim(), objectType);
            return returnObj;

        } catch (JsonSyntaxException jse) {

            errorWrapper.setErrorCode(ErrorCode.PROCESSING_ERR);
            errorWrapper.setErrorDetails("Error converting from JSON");
            errorWrapper.setDescription(jse.getMessage());

        }

        errors.add(errorWrapper);
        throw new MyCustomException("", errors);

    }

    /**
     *
     * @param <T>
     * @param stringArrayToConvert
     * @param objectType
     * @return a list of converted JSON strings
     */
    public static <T> List<T> convertFromJson(List<String> stringArrayToConvert, Type objectType) throws MyCustomException {

        ErrorWrapper errorWrapper = new ErrorWrapper(); //incase it happens
        Set<ErrorWrapper> errors = new HashSet<>();

        //Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();

        //gsonBuilder.registerTypeAdapter(AdScreenOwner.class, new MyGsonTypeAdapter<AdScreenOwner>());
        GraphAdapterBuilder graphAdapterBuilder = new GraphAdapterBuilder();
        graphAdapterBuilder
                //.addType(Author.class)
                //.addType(AdScreenOwner.class)
                //.addType(AdProgram.class)
                .registerOn(gsonBuilder);
        gsonBuilder.registerTypeAdapter(LocalDate.class, new JodaGsonLocalDateConverter());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new JodaGsonLocalDateTimeConverter());
        gsonBuilder.registerTypeAdapter(LocalTime.class, new JodaGsonLocalTimeConverter());

        Gson gson = gsonBuilder.create();

        List list = new ArrayList<>();

        try {
            for (String strToConvert : stringArrayToConvert) {

                list.add(gson.fromJson(strToConvert.trim(), objectType));
            }

            return list;

        } catch (JsonSyntaxException jse) {

            errorWrapper.setErrorCode(ErrorCode.PROCESSING_ERR);
            errorWrapper.setErrorDetails("Error converting from JSON");
            errorWrapper.setDescription(jse.getMessage());

        }

        errors.add(errorWrapper);
        throw new MyCustomException("", errors);

    }

    /**
     * Return Object from JSON string
     *
     * @param <T>
     * @param stringToConvert
     * @param objectType
     * @return
     * @throws com.library.customexception.MyCustomException
     */
    public static <T> T convertFromJson(String stringToConvert, Type objectType) throws MyCustomException {

        ErrorWrapper errorWrapper = new ErrorWrapper(); //incase it happens
        Set<ErrorWrapper> errors = new HashSet<>();

        try {

            //Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setPrettyPrinting();

            //gsonBuilder.excludeFieldsWithoutExposeAnnotation();
            //gsonBuilder.registerTypeAdapter(AdScreenOwner.class, new MyGsonTypeAdapter<AdScreenOwner>());
            GraphAdapterBuilder graphAdapterBuilder = new GraphAdapterBuilder();
            graphAdapterBuilder
                    //.addType(Author.class)
                    //.addType(AdScreenOwner.class)
                    //.addType(AdProgram.class)
                    .registerOn(gsonBuilder);
            gsonBuilder.registerTypeAdapter(LocalDate.class, new JodaGsonLocalDateConverter());
            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new JodaGsonLocalDateTimeConverter());
            gsonBuilder.registerTypeAdapter(LocalTime.class, new JodaGsonLocalTimeConverter());

            Gson gson = gsonBuilder.create();

            return gson.fromJson(stringToConvert.trim(), objectType);

        } catch (JsonSyntaxException jse) {

            errorWrapper.setErrorCode(ErrorCode.PROCESSING_ERR);
            errorWrapper.setErrorDetails("Error converting from JSON");
            errorWrapper.setDescription(jse.getMessage());

        }

        errors.add(errorWrapper);
        throw new MyCustomException("", errors);

    }

    /**
     * Generate short UUID (13 characters)
     *
     * @return short randomValue
     */
    public static String generateShorterRandomID() {

        UUID uuid = UUID.randomUUID();
        //long longValue = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
        //randomValue = Long.toString(longValue, Character.MAX_RADIX);
        long lessSignificantBits = uuid.getLeastSignificantBits();
        String randomValue = Long.toString(lessSignificantBits, Character.MAX_RADIX);

        return randomValue;

    }

    /**
     *
     * @return full randomValue
     */
    public static String generateFullRandomID() {

        UUID uuid = UUID.randomUUID();

        long longValue = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
        String randomValue = Long.toString(longValue, Character.MAX_RADIX);

        return randomValue.toUpperCase();
    }

    /**
     * Generate a random alpha numeric string of specified length
     *
     * @param stringLength
     * @return
     */
    public static String generateRandomAlphaNumeric(int stringLength) {

        String randomString = RandomStringUtils.randomAlphanumeric(stringLength).toUpperCase();

        return randomString.toUpperCase();
    }

    /**
     * Method will print to a debug file ALL the HttpServletRequest headerNames
     * and their values
     *
     * @param request
     * @throws IOException
     */
    public static void printRequesterHeaderInfo(HttpServletRequest request) throws IOException {

        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {

            String headerName = headerNames.nextElement();
            logger.debug(">>> header name  : " + headerName);

            Enumeration<String> headers = request.getHeaders(headerName);
            while (headers.hasMoreElements()) {
                String headerValue = headers.nextElement();
                logger.debug(">>> header value : " + headerValue);
            }
            logger.debug(">>> -------------------------------------");
        }
    }

    /**
     *
     * @param request
     * @return
     */
    public static String getRequesterHeaderInfo(HttpServletRequest request) {

        String allHeaders = "";

        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {

            String headerName = headerNames.nextElement();
            allHeaders += headerName;

            Enumeration<String> headers = request.getHeaders(headerName);
            while (headers.hasMoreElements()) {

                String headerValue = headers.nextElement();
                allHeaders = allHeaders + " = " + headerValue + ";";
            }

            allHeaders += " <> ";
        }

        return allHeaders;
    }

    /**
     * Method logs to a debug file most of the HttpServletRequest parameters
     *
     * @param request HttpServletRequest
     * @return
     */
    public static AdAPIRequest getRequestInfo(HttpServletRequest request) {

        AdAPIRequest apiRequest = new AdAPIRequest();
        apiRequest.setContentType(request.getContentType());
        apiRequest.setContextPath(request.getContextPath());
        apiRequest.setContentLength(request.getContentLength());
        apiRequest.setProtocol(request.getProtocol());
        apiRequest.setPathInfo(request.getPathInfo());
        apiRequest.setRemoteAddress(request.getRemoteAddr());
        apiRequest.setRemotePort(request.getRemotePort());
        apiRequest.setServerName(request.getServerName());
        apiRequest.setQueryString(request.getQueryString());
        apiRequest.setRequestUrl(request.getRequestURL().toString());
        apiRequest.setRequestUri(request.getRequestURI());
        apiRequest.setServletPath(request.getServletPath());
        apiRequest.setRequestBody("");

        return apiRequest;

    }

    /**
     * The following methods will remove all invalid XML characters from a given
     * string (the special handling of a CDATA section is not supported).
     *
     * @param xml
     * @return
     */
    public static String sanitizeXmlChars(String xml) {

        if (xml == null || ("".equals(xml))) {
            return "";
        }
        // ref : http://www.w3.org/TR/REC-xml/#charsets
        // jdk 7
        Pattern xmlInvalidChars = Pattern.compile(
                "[^\\u0009\\u000A\\u000D\\u0020-\\uD7FF\\uE000-\\uFFFD\\x{10000}-\\x{10FFFF}]"
        );

        return xmlInvalidChars.matcher(xml).replaceAll("");
    }

    /**
     *
     * @param jsonPaymentRequest
     * @return
     * @throws MyCustomException
     */
    public static Set<Map.Entry<String, Object>> getJsonDetails(String jsonPaymentRequest) throws MyCustomException {

        Map<String, Object> paymentDetails = GeneralUtils.convertFromJson(jsonPaymentRequest, stringMapType);

        Set<Map.Entry<String, Object>> detailsSet = paymentDetails.entrySet();

        //return user;
        return detailsSet;
    }

    /**
     * Generate random 5 digit number
     *
     * @return
     */
    public static int generate5Digits() {

        Random r = new Random(System.currentTimeMillis());
        return 10000 + r.nextInt(20000);
    }

    /**
     * Return a nice string representation of the array
     *
     * @param iterable
     * @return
     */
    public static String getPrintableIterable(Iterable iterable) {

        return (String.join(",", iterable));

    }

    /**
     * Get a printable array string
     *
     * @param <T>
     * @param collection
     * @return
     */
    public static <T> String getPrintableArray(Set<T> collection) {

        return (Arrays.toString(collection.toArray()));
    }

    /**
     * Convert Set to List
     *
     * @param <T>
     * @param set
     * @return
     */
    public static <T> List<T> convertSetToList(Set<T> set) {

        List<T> newList = new ArrayList<>(set);
        return newList;
    }

    public static <T> Object[] convertSetToArray(Set<T> set) {

        Object[] newArray = new Object[set.size()];
        newArray = set.toArray(newArray);

        return newArray;
    }

    /**
     * Convert List to Set
     *
     * @param <T>
     * @param list
     * @return
     */
    public static <T> Set<T> convertListToSet(List<T> list) {

        System.out.println("1st : " + MapFormat.format("", new HashMap<>()));

        Set<T> set = new HashSet<>(list);
        return set;
    }

    /**
     *
     * @param <T>
     * @param list
     * @return
     */
    public static <T> Object[] convertListToArray(List<T> list) {

        Object[] newArray = new Object[list.size()];
        newArray = list.toArray(newArray);

        return newArray;
    }

    /**
     * Convert a JSON string to pretty print version
     *
     * @param jsonString
     * @return a well formatted JSON string
     */
    public static String toPrettyJsonFormat(String jsonString) {
        JsonParser parser = new JsonParser();

        JsonObject json = parser.parse(jsonString).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(json);

        return prettyJson;
    }

    /**
     * Round up to next 100th integer
     *
     * @param value
     * @return
     */
    public static int roundUpToNext100(double value) {

        return (int) (Math.ceil(value / 100.0) * 100);
    }

    /**
     * Round up to next integer
     *
     * @param value
     * @return
     */
    public static int roundUpToNextInt(double value) {
        return (int) Math.ceil(value);
    }

    /**
     * Add commas to a number
     *
     * @param numberToFormat
     * @return
     */
    public static String addCommas1(int numberToFormat) {

        return (NumberFormat.getNumberInstance(Locale.US).format(numberToFormat));
    }

    /**
     *
     * @param numberToAddCommas
     * @return
     */
    public static String addCommas2(int numberToAddCommas) {

        String str = "UGX" + String.valueOf(numberToAddCommas).replaceAll("/\\B(?=(\\d{3})+(?!\\d))/g", ",");

        logger.debug("Formatted amount string is: " + str);

        return str;
    }

    /**
     * Add (a) comma(s) to a number
     *
     * @param numberToAddCommas
     * @return
     */
    public static String addCommasAndCurrency(int numberToAddCommas) {

        DecimalFormat myFormatter = new DecimalFormat("#,###");
        String output = "UGX" + myFormatter.format(numberToAddCommas);

        logger.debug("Formatted amount string is: " + output);

        return output;

    }

    /**
     * Is given number a Prime
     *
     * @param n
     * @return
     */
    public static boolean isPrime(long n) {
        // fast even test.
        if (n > 2 && (n & 1) == 0) {
            return false;
        }
        // only odd factors need to be tested up to n^0.5
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Is given number a Prime
     *
     * @param n
     * @return
     */
    public static boolean isPrime(int n) {
        // fast even test.
        if (n > 2 && (n & 1) == 0) {
            return false;
        }
        // only odd factors need to be tested up to n^0.5
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Convert an Object to a long value
     *
     * @param value
     * @return
     */
    public static long convertObjectToLong(Object value) {

        logger.debug("Converting Object to long");

        if (value instanceof String) {
            return Long.parseLong((String) value);
        } else {
            return (value instanceof Number ? ((Number) value).longValue() : -1);
        }
    }

    /**
     * Convert an Object to a double value
     *
     * @param value
     * @return
     */
    public static double convertObjectToDouble(Object value) {
        logger.debug("Converting Object to double");
        return (value instanceof Number ? ((Number) value).doubleValue() : -1.0);
    }

    /**
     * Convert an Object to a double value
     *
     * @param value
     * @return
     */
    public static int convertObjectToInteger(Object value) {
        logger.debug("Converting Object to Integer");
        return (value instanceof Number ? ((Number) value).intValue() : 0);
    }

    /**
     *
     * @param mapOfSchedulesAndProgIds
     * @return
     */
    public static String convertToStringMapTheOfSchedulesAndProgIds(Map<Integer, Long> mapOfSchedulesAndProgIds) {

        String scheduleString = "";

        for (Map.Entry<Integer, Long> entry : mapOfSchedulesAndProgIds.entrySet()) {

            int scheduleTime = entry.getKey();
            long progEntityId = entry.getValue();

            scheduleString += (progEntityId + "::" + scheduleTime + ";");

        }

        return scheduleString;
    }

    /**
     * Convert the String returned from the schedule table column that maps
     * Schedule times for this screen to their respective program Entity Ids
     * String is in the format "764::4563;905::2355;" i.e.
     * schedTime::progEntityId;
     *
     * @param scheduleStringFromDatabase
     * @return
     */
    public static Map<Integer, Long> convertToMapStringOfSchedulesAndProgIds(String scheduleStringFromDatabase) {

        logger.debug(">>>> Schedule String, fetched from Database >> " + scheduleStringFromDatabase + " <<<<<<<<");

        String[] progTimeArray = scheduleStringFromDatabase.trim().split("\\s*;\\s*"); // ["764:4563", "905:2355"]

        logger.debug("ProgTimeArray: " + Arrays.toString(progTimeArray));

        //add program ids and their schedule times to an iterable
        Map<Integer, Long> mapOfScheduleAndProgIds = new HashMap<>();

        for (String progAndTime : progTimeArray) { //"764:4563"

            if (!progAndTime.isEmpty()) {

                long progEntityId = Long.parseLong(progAndTime.split("\\s*::\\s*")[0]);
                int previouslySchedTime = Integer.parseInt(progAndTime.split("\\s*::\\s*")[1]);

                mapOfScheduleAndProgIds.put(previouslySchedTime, progEntityId);
            }
        }

        return mapOfScheduleAndProgIds;
    }

    /**
     * formatMSISDN
     *
     * @param MSISDN
     * @return
     */
    public static String formatMSISDN(String MSISDN) {

        if (MSISDN.startsWith("+")) {
            MSISDN = MSISDN.replace("+", "").trim();
        }
        Long phoneNumber;
        try {
            phoneNumber = Long.valueOf(MSISDN);
        } catch (NumberFormatException ex) {
            logger.error("Could not convert number to a Long value: " + ex.getMessage() + ". So returning the number as it was.");
            return MSISDN;
        }
        int length = phoneNumber.toString().length();

        switch (length) {
            case 12:
                logger.info("MSISDN [ "
                        + MSISDN + "] has length: " + MSISDN.length()
                        + " when converted to a long value. No fix to be done");
                break;
            case 9:
                logger.info("MSISDN [ "
                        + MSISDN + "] has length: " + MSISDN.length() + ". "
                        + " when converted to a long value."
                        + " An attempt to fix the number by adding a prefix "
                        + "will be done");
                if (phoneNumber.toString().startsWith("7")) {
                    MSISDN = 256 + phoneNumber.toString();
                }
                break;

            default:
                logger.info("MSISDN [ "
                        + MSISDN + "] has length " + MSISDN.length()
                        + " when converted to a long value. "
                        + "Will be sent as is.");
                break;
        }

        logger.debug("Returning formatted MSISDN as: " + MSISDN);

        return MSISDN;
    }

    public static NetworkId getNetworkId(String msisdn) {

        String prefix = formatMSISDN(msisdn).substring(3, 5);
        logger.debug("Network Prefix: " + prefix);

        NetworkId networkId;
        switch (prefix) {
            //put all this in config
            
            

            case "77":
                networkId = NetworkId.MTN_UG;
                break;

            case "78":
                networkId = NetworkId.MTN_UG;
                break;

            case "39":
                networkId = NetworkId.MTN_UG;
                break;

            case "79":
                networkId = NetworkId.AFRICELL_UG;
                break;

            case "70":
                networkId = NetworkId.AIRTEL_UG;
                break;

            case "75":
                networkId = NetworkId.AIRTEL_UG;
                break;

            case "71":
                networkId = NetworkId.UTL;
                break;

            default:
                networkId = NetworkId.UNKOWN;
                break;
        }
        return networkId;

    }

    public static int createRandomInteger(int aStart, long aEnd, Random aRandom) {

        if (aStart > aEnd) {
            throw new IllegalArgumentException("Start cannot exceed End.");
        }
        //get the range, casting to long to avoid overflow problems
        long range = aEnd - aStart + 1;
        //logger.info("range>>>>>>>>>>>" + range);
        // compute a fraction of the range, 0 <= frac < range
        long fraction = (long) (range * aRandom.nextDouble());
        //logger.info("fraction>>>>>>>>>>>>>>>>>>>>" + fraction);
        long randomNumber = fraction + (long) aStart;
        //logger.info("Generated : " + randomNumber);

        return (int) randomNumber;

    }

    /**
     * Generate OTP (4-digit PIN)
     *
     * @return
     */
    public static synchronized int generateOTP() {

        int START = 1000;
        long END = 9999L;

        Random random = new Random();
        int generatedOTP = createRandomInteger(START, END, random);

        return generatedOTP;
    }

    public static synchronized int generateInt() {

        int START = 1000;
        long END = 9999L;

        Random random = new Random();
        int generatedOTP = createRandomInteger(START, END, random);

        return generatedOTP;
    }

    /**
     * getActivationMessage from template
     *
     * @param firstName
     * @param amount
     * @param outstandingBalance
     * @param activationCode
     * @param numberOfActiveDays
     * @return
     */
    public static String getActivationCodeMessage(String firstName, int amount, int outstandingBalance, String activationCode, int numberOfActiveDays) {

        //Object[] params = {"nameRobert", "rhume55@gmail.com"};
        Map<String, Object> map = new HashMap<>();

        map.put("firstName", firstName);
        map.put("amount", addCommasAndCurrency(amount));
        map.put("outstandingBalance", addCommasAndCurrency(outstandingBalance));
        map.put("activationCode", activationCode);
        map.put("numberOfActiveDays", String.valueOf(numberOfActiveDays));

        String message = MapFormat.format(NamedConstants.SMS_TEMPLATE_ACT_CODE, map);
        logger.debug("Activation message going out : " + message);

        return message;
    }

    /**
     * getPaymentFailMessage message from Template
     *
     * @param firstName
     * @param amount
     * @param generatorId
     * @param statusDescription
     * @return
     */
    public static String getPaymentFailMessage(String firstName, int amount, String generatorId, String statusDescription) {

        //Object[] params = {"nameRobert", "rhume55@gmail.com"};
        Map<String, Object> map = new HashMap<>();

        map.put("firstName", firstName);
        map.put("amount", addCommasAndCurrency(amount));
        map.put("generatorId", generatorId);
        map.put("statusDescription", statusDescription);

        String message = MapFormat.format(NamedConstants.SMS_PAYMENT_FAILURE, map);
        logger.debug("Payment Failure message going out : " + message);

        return message;
    }

    /**
     * getActivationMessage from template
     *
     * @param firstName
     * @param otp
     * @return
     */
    public static String getOTPMessage(String firstName, int otp) {

        //Object[] params = {"nameRobert", "rhume55@gmail.com"};
        Map<String, String> map = new HashMap<>();

        map.put("firstName", firstName);
        map.put("otp", "" + otp);
        //map.put("telesolaAccount", telesolaAccount);

        String message = MapFormat.format(NamedConstants.SMS_TEMPLATE_OTP, map);
        logger.debug("OTP message : " + message);

        return message;
    }

    /**
     *
     * @param smsText
     * @param recipientNumber
     * @return
     */
    public static Map<String, Object> prepareTextMsgParams(String smsText, String recipientNumber) {

        Map<String, Object> paramPairs = new HashMap<>();

        paramPairs.put(NamedConstants.SMS_API_PARAM_USERNAME, NamedConstants.SMS_API_USERNAME);
        paramPairs.put(NamedConstants.SMS_API_PARAM_PASSOWRD, NamedConstants.SMS_API_PASSWORD);
        paramPairs.put(NamedConstants.SMS_API_PARAM_SENDER, NamedConstants.SMS_API_SENDER_NAME);
        paramPairs.put(NamedConstants.SMS_API_PARAM_TEXT, smsText);
        paramPairs.put(NamedConstants.SMS_API_PARAM_RECIPIENT, recipientNumber);

        return paramPairs;
    }

    public static List<NameValuePair> convertToNameValuePair(Map<String, Object> pairs) {

        if (pairs == null) {
            return null;
        }

        List<NameValuePair> nvpList = new ArrayList<>(pairs.size());

        for (Map.Entry<String, Object> entry : pairs.entrySet()) {
            nvpList.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
        }

        return nvpList;

    }

    /**
     * Send out SMS
     *
     * @param paramPairs
     * @return
     */
    public static String sendSMS(Map<String, Object> paramPairs) {

        //String response = "Assume an SMS is sent and this is the response, hihihihi, LOLEST!!";
        //String response = AppEntry.clientPool.sendRemoteRequest("", NamedConstants.SMS_API_URL, paramPairs, HTTPMethod.GET);
        return "";
    }

    /**
     * Generate a User Id account from the client's primaryContact
     *
     * @param primaryContact
     * @return
     */
    public static String generateUserId(String primaryContact) {
        return (primaryContact.substring(3));

        //To-Do
        //Separate accounts by region, especially for distributors e.g. DKLA774983602 for a Kampala Distributor
    }

    /**
     * Convert a set of string objects to a comma delimited String
     *
     * @param screenCodes
     * @return
     */
    public static String convertSetToCommaDelString(Set<String> screenCodes) {

        String screenCodeStr = "";
        if (screenCodes != null) {

            screenCodeStr = String.join(",", screenCodes);
        }

        //org.apache.commons.lang.StringUtils.join(screenCodes, ",");
        return screenCodeStr;
    }

    /**
     * Convert a comma delimited String to a Set
     *
     * @param commaDelString
     * @return
     */
    public static Set<String> convertCommaDelStringToStringSet(String commaDelString) {

        Set<String> set = new HashSet<>();

        StringTokenizer st = new StringTokenizer(commaDelString, ",");
        while (st.hasMoreTokens()) {
            set.add(st.nextToken());
        }
        //Set<String> hashSet = new HashSet<>(Arrays.asList(commaDelString.split(",")));
        return set;
    }

    /**
     * Convert a comma delimited String to a Set of type Long
     *
     * @param commaDelString
     * @return
     */
    public static Set<Long> convertCommaDelStringToLongSet(String commaDelString) {

        Set<Long> set = new HashSet<>();

        StringTokenizer st = new StringTokenizer(commaDelString, ",");
        while (st.hasMoreTokens()) {
            set.add(Long.valueOf(st.nextToken()));
        }

        //Set<String> hashSet = new HashSet<>(Arrays.asList(commaDelString.split(",")));
        return set;
    }

    /**
     * Convert a comma delimited String to a Set of type Long
     *
     * @param commaDelString
     * @return
     */
    public static Set<Integer> convertCommaDelStringToIntegerSet(String commaDelString) {

        Set<Integer> set = new HashSet<>();

        StringTokenizer st = new StringTokenizer(commaDelString, ",");
        while (st.hasMoreTokens()) {
            set.add(Integer.valueOf(st.nextToken()));
        }

        //Set<String> hashSet = new HashSet<>(Arrays.asList(commaDelString.split(",")));
        return set;
    }

    /**
     * Get Resource unique id from fullUploadName
     *
     * @param fullUploadName
     * @return
     */
    public static String getResourceUploadIdHelperNOTUSED(String fullUploadName) {

        String[] stripString = fullUploadName.split("_", 2);//1486059288818_hotel(4).jpg
        String resourceName = stripString[stripString.length - 1];
        String uploadId = stripString[0];

        return uploadId;
    }

    /**
     * Get Resource Name from fullUploadName
     *
     * @param fullUploadName
     * @return
     */
    public static String getResourceNameHelperNOTUSED(String fullUploadName) {

        String[] stripString = fullUploadName.split("_", 2);//1486059288818_hotel(4).jpg
        String resourceName = stripString[stripString.length - 1];
        String uploadId = stripString[0];

        return resourceName;
    }

    /**
     * Get the JSON string from an HTTPServerletRequest
     *
     * @param request
     * @return
     * @throws MyCustomException
     */
    public static String getJsonStringFromRequest(HttpServletRequest request) {

        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        String s;

        try {

            reader = request.getReader();

            do {

                s = reader.readLine();

                if (s != null) {
                    sb.append(s);
                } else {
                    break;
                }

            } while (true);

        } catch (IOException ex) {
            logger.error("IO Exception, failed to decode JSON string from request: " + ex.getMessage());
            //throw new MyCustomException("IO Exception occurred", ErrorCode.CLIENT_ERR, "Failed to decode JSON string from the HTTP request: " + ex.getMessage(), ErrorCategory.CLIENT_ERR_TYPE);

        } catch (Exception ex) {
            logger.error("General Exception, failed to decode JSON string from request: " + ex.getMessage());
            //throw new MyCustomException("General Exception occurred", ErrorCode.CLIENT_ERR, "Failed to decode JSON string from the HTTP request: " + ex.getMessage(), ErrorCategory.CLIENT_ERR_TYPE);

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    logger.error("exception closing buffered reader: " + ex.getMessage());
                }
            }
        }

        return sb.toString();
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
