package com.agromarketday.ussd.constant;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;

/**
 *
 * @author smallgod
 */
public interface NamedConstants {

    /**
     * 1 minute Delay in scheduling the advert due to system delays, for example
     * Uploading Resources to servers, moving resources, downloading resources
     * by the player etc
     *
     */
    public static final long SYSTEM_SCHEDULE_DELAY_MILLIS = Duration.standardSeconds(60).getMillis(); //changed from 120 to 60 to 0

    public static final long PAY_TIME = Duration.standardSeconds(300).getMillis();

    public static final long REVIEW_TIME = Duration.standardSeconds(300).getMillis();

    public static final long DOWNLOAD_TIME_FULLSCREEN = Duration.standardSeconds(420).getMillis();

    public static final long DOWNLOAD_TIME_3SPLIT = Duration.standardSeconds(300).getMillis();

    public static final long DOWNLOAD_TIME_TEXT = Duration.standardSeconds(60).getMillis();//1min for text upload

    public static final String ADMIN_SMS_RECIPIENT = "256790790491";

    public static final String ADVERTXPO_CALLBACK_URL = "http://advertxpo.com:9008/adcentral/api/json";
    public static final String MAMBOPAY_DEBIT_URL = "https://mambopay.azure-api.net/api/v1/mtnmobilemoneyapi/debit";

    //public static final String SUBSCRIPTION_KEY = "9389259ec349469682c71910ab6f4ac3";
    public static final String SUBSCRIPTION_KEY = "f3abdfa6568a4d8da75c27aa8fbd26ce"; //UNLIMITED

    public static final String MAMBOPAY_DEBIT_MISSING_SUBSCRIPTION_KEY_2 = "401";
    public static final int MAMBOPAY_DEBIT_MISSING_SUBSCRIPTION_KEY = 401;
    public static final int MAMBOPAY_DEBIT_ACCOUNT_UNREGISTERED = 101;
    public static final int MAMBOPAY_DEBIT_INSUFFICIENT_FUNDS = 106;
    public static final int MAMBOPAY_DEBIT_BELOW_THRESHOLD = 105;
    public static final int MAMBOPAY_DEBIT_PAYMENT_EXPIRED = 103;
    public static final int MAMBOPAY_DEBIT_SUCCESS = 1;
    public static final String MAMBOPAY_DEBIT_PROCESSING = "01";
    public static final String MAMBOPAY_DEBIT_DUPLICATE = "02";

    //MAMBO-PAY DEBIT API HTTP PARAMS
    public static final String MAMBOPAY_PARAM_MSISDN = "msisdn";
    public static final String MAMBOPAY_PARAM_AMOUNT = "amount";
    public static final String MAMBOPAY_PARAM_TRANSID = "transaction_id";
    public static final String MAMBOPAY_PARAM_CALLBACKURL = "callbackurl";
    public static final String MAMBOPAY_HEADER_SUBSCKEY = "Ocp-Apim-Subscription-Key";

    /**
     * When a scheduler is first created it is in "stand-by" mode, and will not
     * fire triggers for this delay
     */
    public static int CAMPAIGN_JOB_SCHEDULE_START_DELAY = 10;//seconds

    public static int PAYMENT_JOB_SCHEDULE_START_DELAY = 25;

    public static int DISPLAY_JOB_SCHEDULE_START_DELAY = 40;
    
    
    /**
     * Time allowed before previously saved sessions are discarded
     */
     public static int SAVED_SESSION_VALIDITY = 30; //MINUTES

    /**
     * Thumbnail maximum width
     */
    public static int THUMB_MAX_WIDTH = 300;//pixels

    /**
     * Thumbnail maximum height
     */
    public static int THUMB_MAX_HEIGHT = 300;//pixels

    /**
     * Generic error message from a database operation that will be displayed to
     * users on the UI
     */
    public static String GENERIC_DB_ERR_DESC = "Sorry, request can't be processed now. Please try again later or call AgroMarketDay on 0784725338";

    /**
     * Generic error message to be displayed to users on the UI
     */
    public static String GENERIC_ERR_DESC = "Sorry, request can't be processed now. Please try again later or call AgroMarketDay on 0784725338";

    /**
     * Will be concantenated to the business ID to form a screen_id
     */
    public static String SCREEN_START_ID = "01";

    /**
     * 3SPLIT screen layout description
     */
    public static String THREE_SPLIT_LAYOUT_DESC = "Splits a display/TV screen into 3. 2 of the 3 splits are used for advertising with Scrolling Text on the lower split and Images and/or Video on the side split";
    public static int THREE_SPLIT_LAYOUT_PRICE = 1000;
    /**
     * 3SPLIT screen layout description
     */
    public static String FULLSCREEN_LAYOUT_DESC = "This layout lets the user's adverts fill up the entire display/TV screen";
    public static int FULLSCREEN_LAYOUT_PRICE = 1500;

    public static int ARREST_CREEPY_THREADS = 1;

    public static final Lock FETCH_CAMPAIGNS_LOCK = new ReentrantLock();

    public static final Lock FETCH_SCHEDULE_LOCK = new ReentrantLock();

    public static final Lock FETCH_PAYMENTS_LOCK = new ReentrantLock();

    /**
     * Use this mutex to lock threads on sensitive portions of code
     */
    public static final String READ_SETUP_PARAMS_MUTEX = "READ_SETUP_PARAMS";

    /**
     * Use this mutex to lock threads on sensitive portions of code
     */
    public static final String FETCH_CAMPAIGNS_MUTEX = "FETCH_CAMPAIGNS_MUTEX";

    /**
     * Use this mutex to lock threads on sensitive portions of code
     */
    public static final String FETCH_SCHEDULE_MUTEX = "FETCH_SCHEDULE_MUTEX";

    /**
     * Use this mutex to lock threads on sensitive portions of code
     */
    public static final String AD_PAYMENT_MUTEX = "AD_PAYMENT_MUTEX";

    /**
     * UPDATE DB MUTEX
     */
    public static final String UPDATE_DB_MUTEX = "UPDATE_DB_MUTEX";

    /**
     * SSL certificates
     */
    public static final String PEER_CERTIFICATES = "PEER_CERTIFICATES";

    /**
     * Date time string formats
     *
     * public static final String timeMinAndSecFormat = "HH:mm:ss"; public
     * static final String timeAndMinutesFormat = "HH:mm"; public static final
     * String dateOnlyFormat = "dd/MM/yyyy"; public static final String
     * dateOnlyDashFormat = "dd-MM-yyyy"; public static final String
     * dateTimeFormat = "dd/MM/yyyy HH:mm"; public static final String
     * dateOnlyDashYearFirstFormat = "yyyy-MM-dd";
     *
     */
    public static final String KAMPALA_TIME_ZONE = "Africa/Kampala";

    public static final DateTimeZone KAMPALA_DATE_TIME_ZONE = DateTimeZone.forID("Africa/Kampala");

    public static final DateTimeZone UTC_TIME_ZONE = DateTimeZone.UTC;

    public static final int OTP_EXPIRY_DAYS = 30;

    /**
     * The DateTime format we are using in this application is in the format
     * "2016-07-25 08:55:09"
     */
    public static final String DATE_TIME_DASH_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * DateTime format without seconds e.g. "2016-07-25 08:55"
     */
    public static final String DATE_TIME_DASH_NO_SECONDS_FORMAT = "yyyy-MM-dd HH:mm";

    /**
     * The Date only format we are using in this application is in the format
     * "2016-07-25"
     */
    public static final String DATE_DASH_FORMAT = "yyyy-MM-dd";

    /**
     * The time represented as Hour:Minute
     */
    public static final String HOUR_MINUTE_FORMAT = "HH:mm";

    /**
     * The time represented as Hour:minute:second
     */
    public static final String HOUR_MINUTE_SEC_FORMAT = "HH:mm:ss";

    /**
     * All JSON request strings must have a root node named 'method' which
     * identifies the name of the API method being called
     */
    public static final String JSON_METHOD_NODENAME = "method";

    /**
     * All JSON request strings must have a root node named 'params' which
     * identifies the parameters of the API method being called
     */
    public static final String JSON_PARAMS_NODENAME = "params";

    /**
     * When storing appconfigs in the servlet context, we will use this as the
     * attribute name
     */
    public static final String APPCONFIGS_ATTR_NAME = "appconfigs";

    /**
     * When storing the database reference in the servlet context, we will use
     * this as the attribute name
     */
    public static final String DATABASE_ATTR_NAME = "dbreference";

    /**
     * When storing the http pool reference in the servlet context, we will use
     * this as the attribute name
     */
    public static final String HTTP_POOL_ATTR_NAME = "httppool";

    public static final String USSD_SERVER_XML_HANDLER = "USSD_SERVER_XML_HANDLER";
    
    public static final String USSD_SERVER_JSON_HANDLER = "USSD_SERVER_JSON_HANDLER";
    
    public static final String JSON_API_SERVER_HANDLER = "JSON_API_SERVER_HANDLER";

    /**
     * A username that last modified a given auditable database entity
     */
    public static final String PROPNAME_LAST_MODIFIED_BY = "lastModifiedBy";

    /**
     * A date when a given auditable database entity was modified
     */
    public static final String PROPNAME_DATE_LAST_MODIFIED = "dateLastModified";

    /**
     * A delimited string with a trail of dates when an auditable database
     * entity has been modified
     */
    public static final String PROPNAME_DATE_MODIFIED_HISTORY = "dateModifiedHistory";
    /**
     * A delimited string with a trail of usernames that have modified an
     * auditable database entity
     */
    public static final String PROPNAME_MODIFIED_BY_HISTORY = "modifiedByHistory";

    /**
     * A username that first created a given auditable database entity
     */
    public static final String PROPNAME_CREATED_BY = "createdBy";

    /**
     * A date when a given auditable database entity was first created
     */
    public static final String PROPNAME_CREATED_ON = "createdOn";

    /**
     * same as the JDBC batch size in the hibernate config xml config file Will
     * flush a batch of inserts and release memory
     */
    public static final int HIBERNATE_JDBC_BATCH = 30;

    /**
     * The batch size for number of records to be read at a time
     */
    public static final int NUM_ROWS_CHUNK = 10000;

    /**
     * Profile under which the daemon has been started and is running -
     * Development profile
     */
    public static final String DAEMON_PROFILE_DEV = "development";

    /**
     * Profile under which the daemon has been started and is running -
     * Production profile
     */
    public static final String DAEMON_PROFILE_PROD = "production";

    /**
     * Profile under which the daemon has been started and is running - Staging
     * profile
     */
    public static final String DAEMON_PROFILE_STAGE = "staging";

    /**
     * Http header value for the JSON content type
     */
    //public static final String HTTP_CONTENT_TYPE_JSON = "application/json"; //"application/json;charset=UTF-8")
    /**
     * Http header value for the XML content type
     */
    //public static final String HTTP_CONTENT_TYPE_XML = "application/xml";
    /**
     * An http server can return a success http response but with no content in
     * the body
     */
    public static final int HTTPCODE_SUCCESS_NO_CONTENT = 204;

    /**
     * HTTP requestmethod - POST
     */
    public static final String HTTP_REQUESTMETHOD_POST = "POST";
    /**
     * HTTP requestmethod - GET
     */
    public static final String HTTP_REQUESTMETHOD_GET = "GET";

    /**
     * Attribute name for the http parameter name holding the database entity
     * name/type useful when passing a request to the DB manager unit, so that
     * the entity in context can be identified
     */
    public static final String HTTP_PARAM_ENTITYNAME = "entityname";

    /**
     * HTTP parameter name with value to identify which DB operation is being
     * executed
     */
    public static final String HTTP_PARAM_DB_OPERATION = "dboperation";

    /**
     * DB Table property names and values
     */
    public static final String HTTP_PARAM_DB_PROPS = "dbproperties";

    /**
     * This is the single/default customer ID we will be using for all tasks and
     * programs
     */
    public static final int CUSTOMER_ID = 20120101;

    /**
     * When no customer ID is provided, use default which is one (1)
     */
    public static final int DEFAULT_DSM8_CUSTOMER_ID = 1;

    /**
     * Header Name containing the enum value name of the task type
     */
    public static final String TASK_TYPE = "TASK_TYPE";

    /**
     * Header Name containing the customer ID value
     */
    public static final String CUSTOMER_ID_HEADER = "CUSTOMER_ID";

    /**
     * Header name for this task ID value if it exists
     */
    public static final String TASK_ID = "TASK_ID";

    public static final String TASK_NAME = "TASK_NAME";

    public static final String TASK_DESC = "TASK_DESC";

    /**
     * Thread shutdown delay if busy, in Minutes
     */
    public static long SHUTDOWN_DELAY = 5;  //5 minutes

    /**
     * Ad TEXT that will scroll
     */
    public static String SCROLL_TEXT_CONTENT = "SCROLL_TEXT";

    /**
     * Header TEXT to the scroll text ad (static)
     */
    public static String SCROLL_TEXT_HEADER = "SCROLL_HEADER";

    /**
     * Header TEXT to the list of all device ids that we are binding this
     * campaign to
     */
    public static String BIND_DEVICE_IDS = "BIND_DEVICE_IDS";

    //We might want to make this dynamic
    /**
     * this task was created in the database via the DSM UI
     */
    public static int RESET_LOOP_TASKID = 1483959575;

    /**
     * Identifies by name the RemoteRequest put in the Remote unit's map
     *
     */
    public static String DSM_UNIT_REQUEST = "DSM_UNIT";

    /**
     * Identifies by name the RemoteRequest put in the Remote unit's map
     */
    public static String CENTRAL_UNIT_REQUEST = "CENTRAL_UNIT";

    /**
     * Identifies by name the RemoteRequest put in the Remote unit's map
     */
    public static String ADDISPLAY_UNIT_REQUEST = "ADDISPLAY_UNIT";

    /**
     * Identifies by name the RemoteRequest put in the Remote unit's map
     */
    public static String ADDBManager_UNIT_REQUEST = "ADDBManager_UNIT";

    /**
     * Identifies by name the HTTP CLIENT pool stored in the JOBS datamap
     */
    public static String CLIENT_POOL = "CLIENT_POOL";

    /**
     * Identifies by name the external db access adapter object stored in the
     * JOBS datamap
     */
    public static String EXTERNAL_DB_ACCESS = "EXTERNAL_DB_ACCESS";

    /**
     * Identifies by name the internal db access adapter object stored in the
     * JOBS datamap
     */
    public static String INTERNAL_DB_ACCESS = "INTERNAL_DB_ACCESS";

    public static String SECOND_JOBSDATA = "SECOND_JOBSDATA";

    public static String THIRD_JOBSDATA = "THIRD_JOBSDATA";

    public static String TRIGGER_NOW_PAYPROCESSOR = "TRIGGER_NOW_PAYPROCESSOR";

    public static String TRIGGER_NOW_CAMPAIGNPROCESSOR = "TRIGGER_NOW_CAMPAIGNPROCESSOR";

    public static String TRIGGER_NOW_DISPLAYPROCESSOR = "TRIGGER_NOW_DISPLAYPROCESSOR";

    public static String PAYMENTS_DETAILS = "PAYMENTS_DETAILS";

    public static String PAYMENTS_ID = "PAYMENTS_ID";

    public static String CAMPAIGN_DETAILS = "CAMPAIGN_DETAILS";

    public static Set<String> ALL_COLUMNS = new HashSet<>(Arrays.asList("ALL"));

    /**
     * Directory we are currently uploading files to -> Need to see a way of
     * having this in configs
     */
    public static String FILE_UPLOAD_DIR = "/etc/ug/adcentral/temp/uploads";

    /**
     * character set
     */
    public static String CHARSET = "UTF-8";

    /**
     * Advert/Program Slots (minutes) available in one Hour
     */
    final int SLOTS_IN_HOUR = 60;

    /**
     * Multiples of 3 slots/minutes Advert/Program Slots to allocate first
     *
     */
    final int FIRST_SLOT_ALLOCATION = 3;

    /**
     * Multiples of 5 slots/minutes Advert/Program Slots to allocate second
     *
     */
    final int SECOND_SLOT_ALLOCATION = 5;

    /**
     * Multiples of 2 slots/minutes Advert/Program Slots to allocate second
     * last, since the last allocation is for minutes that are prime numbers
     * (remaining)
     *
     */
    final int THIRD_SLOT_ALLOCATION = 2;

    /**
     * Token ID / Subscription ID length
     */
    public static int TOKEN_ID_LENGTH = 25;
    
    
    

    public static final String SMS_API_URL = "http://api.infobip.com/api/v3/sendsms/plain";
    public static final String SMS_API_SENDER_NAME = "ADVERTXPO";
    public static final String SMS_API_USERNAME = "codev";
    public static final String SMS_API_PASSWORD = "codev";
    public static final String SMS_TEMPLATE_ACT_CODE = "Hello {firstName}, you paid {amount}, remaining balance is {outstandingBalance}. Enter code {activationCode} in your Telesola app for {numberOfActiveDays} days. Thank you";
    public static final String SMS_PAYMENT_FAILURE = "Hello {firstName}, payment of {amount} for generator: {generatorId}, has Failed.Reason: {statusDescription}. Please re-initiate another payment. Thank you";
    //public static final String SMS_TEMPLATE_OTP = "Hello {firstName}, your PIN is: {otp} & GeneratorID: {generatorId}. Please open your Telesola app & enter the details.However, clear the deposit to activate the generator";

    public static final String SMS_TEMPLATE_OTP = "Hello {firstName}, your One Time Pin is: {otp}. To verify your account, please login and enter your PIN at: http://advertxpo.com/#/confirm_otp -.";

    public static final String SMS_TEMPLATE_NEW_CAMPAIGN_ADMIN = "Hi advertXpo admin, a new campaign just hit the system at: {createTime}. Cost: {campaignCost}, MoMo a/c: {momoAccount}";

    public static final String SMS_TEMPLATE_CONTACT_US_REQ_ADMIN = "Hi admin, enquiry from: {phoneContact}. Excerpt: {messageExcerpt}..";

    public static final String SMS_TEMPLATE_CAMPAIGN_ESCALATE_ADMIN = "Hi advertXpo admin, campaign with id: {id} has been escalated. Manual intervention required. MoMo a/c: {momoAccount}";

    public static final String SMS_TEMPLATE_CAMPAIGN_REVIEW_ADMIN = "Hi advertXpo admin, campaign with id: {id} needs to be reviewed. MoMo a/c: {momoAccount}";

    public static final String SMS_TEMPLATE_CAMPAIGN_SCHEDULE_MSG = "Hello, you campaign with id: {id} has been scheduled to start playing at, {time}";

    public static final String SMS_API_PARAM_USERNAME = "user";
    public static final String SMS_API_PARAM_PASSOWRD = "password";
    public static final String SMS_API_PARAM_SENDER = "sender";
    public static final String SMS_API_PARAM_TEXT = "SMSText";
    public static final String SMS_API_PARAM_RECIPIENT = "GSM";

    /**
     * Guest Credentials
     */
    public static final String GUEST_USER_ID = "256770000000";
    public static final String GUEST_USER_PASSWORD = "111111";

    /**
     * Guest Credentials
     */
    public static final String TEST_USER_ID = "256790790491";
    public static final String TEST_USER_PASSWORD = "111111";

    /**
     * Campaign Ids that will be used as templates
     */
    public static int[] TEMPLATE_CAMPAIGNS = {};

    /**
     * Templates user credentials
     */
    public static String TEMPLATE_CAMPAIGNS_USER_ID = "25677777777";
    public static String TEMPLATE_CAMPAIGNS_USER_PASSWORD = "111111";

    /**
     * Default language (code value) on the USSD menu
     */
    public static String DEFAULT_USSD_LANGUAGE_CODE = "english";
}
