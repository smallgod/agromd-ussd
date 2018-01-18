package com.agromarketday.ussd.controller;

import com.agromarketday.ussd.datamodel.Item;
import com.agromarketday.ussd.sharedInterface.HttpUnitController;
import com.agromarketday.ussd.config.UssdMenuConfig;
import com.agromarketday.ussd.config.JobsConfig;
import com.agromarketday.ussd.config.RemoteUnitConfig;
import com.agromarketday.ussd.connect.HttpClientPool;
import com.agromarketday.ussd.constant.APIMethodName;
import com.agromarketday.ussd.constant.BuyerLocation;
import com.agromarketday.ussd.constant.ErrorCode;
import com.agromarketday.ussd.constant.ItemTag;
import com.agromarketday.ussd.constant.MenuName;
import com.agromarketday.ussd.constant.MenuType;
import com.agromarketday.ussd.constant.NamedConstants;
import com.agromarketday.ussd.constant.NavigationBar;
import com.agromarketday.ussd.constant.NavigationInput;
import com.agromarketday.ussd.constant.Region;
import com.agromarketday.ussd.constant.TransportArea;
import com.agromarketday.ussd.constant.UssdFunction;
import com.agromarketday.ussd.database.CustomHibernate;
import com.agromarketday.ussd.datamodel.AgClient;
import com.agromarketday.ussd.datamodel.AgLanguage;
import com.agromarketday.ussd.datamodel.AgUssdMenu;
import com.agromarketday.ussd.datamodel.AgNavigation;
import com.agromarketday.ussd.datamodel.AgMenuItemIndex;
import com.agromarketday.ussd.datamodel.AgProduct;
import com.agromarketday.ussd.datamodel.DataItem;
import com.agromarketday.ussd.datamodel.DynamicMenuItem;
import com.agromarketday.ussd.datamodel.NextNavigation;
import com.agromarketday.ussd.datamodel.TitleAddition;
import com.agromarketday.ussd.datamodel.UssdMenuComponents;
import com.agromarketday.ussd.datamodel.jaxb.smsone.USSDRequest;
import com.agromarketday.ussd.datamodel.jaxb.smsone.USSDResponse;
import com.agromarketday.ussd.datamodel.json.CheckAccountRequest;
import com.agromarketday.ussd.datamodel.json.CheckAccountResponse;
import com.agromarketday.ussd.datamodel.json.ContactBuyerRequest;
import com.agromarketday.ussd.datamodel.json.ContactResponse;
import com.agromarketday.ussd.datamodel.json.ContactRequest;
import com.agromarketday.ussd.datamodel.json.CreateAccountRequest;
import com.agromarketday.ussd.datamodel.json.CreateAccountResponse;
import com.agromarketday.ussd.datamodel.json.Credentials;
import com.agromarketday.ussd.datamodel.json.GetBuyerRequest;
import com.agromarketday.ussd.datamodel.json.GetBuyerResponse;
import com.agromarketday.ussd.datamodel.json.GetCategoryRequest;
import com.agromarketday.ussd.datamodel.json.GetCategoryResponse;
import com.agromarketday.ussd.datamodel.json.GetSellersRequest;
import com.agromarketday.ussd.datamodel.json.GetSellersResponse;
import com.agromarketday.ussd.datamodel.json.GetSellersResponse.Data.SellerDistrict;
import com.agromarketday.ussd.datamodel.json.GetSellersResponse.Data.SellerDistrict.Seller;
import com.agromarketday.ussd.datamodel.json.MarketPriceResponse;
import com.agromarketday.ussd.datamodel.json.MarketPriceRequest;
import com.agromarketday.ussd.datamodel.json.ItemUploadRequest;
import com.agromarketday.ussd.datamodel.json.ItemUploadResponse;
import com.agromarketday.ussd.datamodel.json.MarketPriceResponse2;
import com.agromarketday.ussd.datamodel.json.MarketPriceResponse2.Data.District;
import com.agromarketday.ussd.datamodel.json.MarketPriceResponse2.Data.District.Product;
import com.agromarketday.ussd.datamodel.json.MenuHistory;
import com.agromarketday.ussd.datamodel.json.MenuHistory.MenuOption;
import com.agromarketday.ussd.datamodel.json.Price;
import com.agromarketday.ussd.datamodel.json.ScreenPagination;
import com.agromarketday.ussd.exception.EmptyStringException;
import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.logger.LoggerUtil;
import com.agromarketday.ussd.scheduler.CustomJobScheduler;
import com.agromarketday.ussd.sharedInterface.HasChildrenItems;
import com.agromarketday.ussd.sharedInterface.MenuItem;
import com.agromarketday.ussd.util.BindXmlAndPojo;
import com.agromarketday.ussd.util.DateUtils;
import com.agromarketday.ussd.util.GeneralUtils;
import com.agromarketday.ussd.util.ItemNodeComparator;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutorService;

/**
 *
 * @author smallgod
 */
public class XmlProcessor implements HttpUnitController {

    private final CustomHibernate internalDbAccess;
    private final HttpClientPool clientPool;
    private final UssdMenuConfig ussdMenuConfig;
    private final RemoteUnitConfig remoteUnitConfig;
    private final JobsConfig adCampainProcessorConfig;
    private final JobsConfig adPaymentProcessorConfig;
    private final JobsConfig adDisplayProcessorConfig;
    private final ExecutorService taskExecutorService;
    private final CustomJobScheduler jobScheduler;
    private final int DEFAULT_ITEMS_PER_SCREEN = 8; //items to display per screen
    private final int SELLER_ITEMS_PER_SCREEN = 6;

    private static final LoggerUtil logger = new LoggerUtil(XmlProcessor.class);

    public XmlProcessor(CustomHibernate customHibernate,
            HttpClientPool clientPool, UssdMenuConfig ussdMenuConfig,
            RemoteUnitConfig remoteUnitConfig,
            JobsConfig adCampainProcessorConfig,
            JobsConfig adPaymentProcessorConfig,
            JobsConfig adDisplayProcessorConfig,
            ExecutorService taskExecutorService,
            CustomJobScheduler jobScheduler) {

        this.internalDbAccess = customHibernate;
        this.clientPool = clientPool;
        this.ussdMenuConfig = ussdMenuConfig;
        this.remoteUnitConfig = remoteUnitConfig;
        this.adCampainProcessorConfig = adCampainProcessorConfig;
        this.adPaymentProcessorConfig = adPaymentProcessorConfig;
        this.taskExecutorService = taskExecutorService;
        this.jobScheduler = jobScheduler;
        this.adDisplayProcessorConfig = adDisplayProcessorConfig;
    }

    @Override
    public String processClientRequest(HttpServletRequest request)
            throws MyCustomException, NullPointerException {

        String errorDetails;

        try {

            InputStream xmlInputStream = request.getInputStream();
            String xmlRequest = GeneralUtils.readXMLStream(xmlInputStream);

            logger.info("-----\nUSSD Request...\n" + xmlRequest + "\n--------");

            USSDRequest ussdRequest = (USSDRequest) BindXmlAndPojo
                    .xmlToObject(xmlRequest, USSDRequest.class);

            String transactionId = ussdRequest.getTransactionId();
            String transactionTime = ussdRequest.getTransactionTime();
            String msisdn = ussdRequest.getMSISDN();
            String requestString = ussdRequest.getUSSDRequestString().trim();

            logger.debug("TransactionID  : " + transactionId);
            logger.debug("transactionTime: " + transactionTime);
            logger.debug("msisdn         : " + msisdn);
            logger.debug("requestString  : " + requestString);
            
            

            NextNavigation nextNavig = processMenuRequest(
                    msisdn, requestString, transactionId);

            USSDResponse ussdResponse = new USSDResponse();
            ussdResponse.setTransactionId(transactionId);
            ussdResponse.setTransactionTime(transactionTime);
            ussdResponse.setUSSDResponseString(nextNavig.getResponseString());
            ussdResponse.setUSSDAction(nextNavig.getUssdAction().getValue());

            String xmlResponse = BindXmlAndPojo.objectToXML(ussdResponse,
                    USSDResponse.class);

            logger.info("------\nUSSD Response...\n" + xmlResponse + "\n----");

            return xmlResponse;

        } catch (IOException | EmptyStringException exc) {

            errorDetails = exc.getMessage();
            logger.error("Error! Failed to parse the XML: " + errorDetails);
            exc.printStackTrace();

        } catch (Throwable exc) {

            errorDetails = exc.getMessage();
            logger.error("Error! Failed to parse the XML: " + errorDetails);
            exc.printStackTrace();
        }

        MyCustomException error
                = GeneralUtils.getSingleError(ErrorCode.INTERNAL_ERR,
                        NamedConstants.GENERIC_ERR_DESC, errorDetails);
        throw error;
    }

    NextNavigation processMenuRequest(
            String msisdn,
            String requestInput,
            String sessionId)
            throws MyCustomException {

        NextNavigation nextNavigation;

        logger.debug("User Input Request: " + requestInput);

        //first userContact dial or unknown userContact
        if (requestInput.equalsIgnoreCase("continue")) {

            nextNavigation = setStartUssdMenu(msisdn, sessionId);

        } else {
            nextNavigation = processUSSDMenu(requestInput, msisdn, sessionId);
        }

        internalDbAccess.saveOrUpdateEntity(nextNavigation.getNavig());
        return nextNavigation;
    }

    private String getChosenStaticMenuItem(AgUssdMenu menu, String requestInput)
            throws MyCustomException {

        int index;

        try {
            index = Integer.parseInt(requestInput);
        } catch (NumberFormatException nfe) {

            String errorDetails = "Bad user input! Expected a number input: "
                    + nfe.getMessage();
            MyCustomException error
                    = GeneralUtils.getSingleError(ErrorCode.INVALID_USER_INPUT,
                            NamedConstants.GENERIC_ERR_DESC, errorDetails);
            throw error;
        }

        String menuItemCode;
        Set<AgMenuItemIndex> childNodes = menu.getMenuItems();

        for (AgMenuItemIndex node : childNodes) {

            int menuItemIndex = node.getMenuItemIndex();//e.g. 1
            menuItemCode = node.getMenuItemCode(); //e.g. '1080'

            if (index == menuItemIndex) {
                return menuItemCode;
            }
        }

        String errorDetails = "Failed to match user input";
        MyCustomException error
                = GeneralUtils.getSingleError(ErrorCode.PROCESSING_ERR,
                        NamedConstants.GENERIC_ERR_DESC, errorDetails);
        throw error;
    }

    private UssdMenuComponents processDynamicMenu(String requestInput,
            MenuName oldCurrentMenu, String msisdn,
            Map<String, String> menuNodes, String sessionID,
            boolean isRegistered)
            throws MyCustomException {

        UssdMenuComponents menuComponents = null;

        switch (oldCurrentMenu) {

            case ITEM_CATEGORIES:
                menuComponents = displaySubCategories(requestInput,
                        msisdn, menuNodes, sessionID);
                break;

            case ITEM_SUBCATEGORIES:
                menuComponents = displayRegions(requestInput,
                        msisdn, menuNodes, sessionID);
                break;

            case MARKET_DISTRICTS:
//                menuComponents = displayMarkets(requestInput,
//                        msisdn, menuNodes, sessionID);
                menuComponents = displayMarketPricesEnd(requestInput,
                        msisdn, menuNodes, sessionID);
                break;

            case SELLER_DISTRICTS:
                menuComponents = displaySellerList(requestInput,
                        msisdn, menuNodes, sessionID);
                break;

            case MARKETS:
                menuComponents = displayMarketPrices(requestInput,
                        msisdn, menuNodes, sessionID);
                break;

            case SELLER_LIST:
                menuComponents = contactSeller(requestInput,
                        msisdn, menuNodes, sessionID);
                break;

            case MATCHED_PRODUCTS:
                menuComponents = displayMatchedBuyerLocation(requestInput,
                        msisdn, menuNodes, sessionID);
                break;

            case MATCHED_BUYER_LIST:
                menuComponents = contactBuyer(requestInput,
                        msisdn, menuNodes, sessionID);
                break;

            case BUYER_CATEGORY:
//                menuComponents = processBuyerCategories(requestInput,
//                        msisdn, menuNodes, sessionID);
                break;

//          case LANGUAGE_MENU:
//                menuComponents = setLanguage(requestInput, msisdn);
//                break;
            default:
//                menuComponents = processBuyerCategories(requestInput,
//                        msisdn, menuNodes, sessionID);
                break;
        }

        return menuComponents;
    }

    private UssdMenuComponents processDefaultMenu(MenuName oldCurrentMenu,
            String requestInput, String msisdn, Map<String, String> menuNodes,
            String newSessionID, boolean isRegistered) throws MyCustomException {

        MenuName responseMenu = MenuName.UNDER_MAINTENANCE;
        NavigationBar navBar = NavigationBar.MAIN;
        Set<AgMenuItemIndex> menuItems = null;
        String menuTitle = null;

        AgUssdMenu newMenu = retrieveMenuFromDB(responseMenu);
        if (menuItems == null) {
            menuItems = newMenu.getMenuItems();
        }
        if (menuTitle == null) {
            menuTitle = newMenu.getMenuTitleText();
            menuTitle = convertMenuNodeToString(menuTitle, menuNodes);
        }

        UssdMenuComponents menuComponents = new UssdMenuComponents();
        menuComponents.setNavBar(navBar);
        menuComponents.setMenuItems(menuItems);
        menuComponents.setMenuTitle(menuTitle);
        menuComponents.setResponseMenu(responseMenu);

        return menuComponents;

    }

    private UssdMenuComponents processInputMenu(MenuName oldCurrentMenu,
            String requestInput, Map<String, String> menuNodes,
            String newSessionID, AgClient client) throws MyCustomException {

        String msisdn = client.getMsisdn();
        MenuName responseMenu = null;
        NavigationBar navBar = NavigationBar.PREVIOUS_AND_MAIN;
        Set<AgMenuItemIndex> menuItems = null;
        String menuTitle = null;
        UssdMenuComponents menuComponents = null;

        switch (oldCurrentMenu) {

            case REGISTER_NAME:
                menuComponents = registerName(requestInput,
                        menuNodes, newSessionID, client);
                break;

            case REGISTER_DISTRICT:
                menuComponents = registerDistrict(requestInput,
                        menuNodes, newSessionID, client);
                break;

            case ENTERED_PRODUCE:
                menuComponents = processCustomProduce(requestInput, msisdn,
                        menuNodes, newSessionID);
                break;

            case PRODUCT_DESCRIPTION:
                menuComponents = processProductDescription(requestInput,
                        msisdn, menuNodes, newSessionID);
                break;

            case CUSTOM_DISTRICT:
                menuComponents = processItemDistrict(requestInput, msisdn,
                        menuNodes, newSessionID);
                break;

            case ITEM_PLACE:
                menuComponents = processItemPlace(requestInput, msisdn,
                        menuNodes, newSessionID);
                break;

            case QUANTITY:
                menuComponents = processItemQuantity(requestInput, msisdn,
                        menuNodes, newSessionID);
                break;

            case PRICE:
                menuComponents = processItemPrice(requestInput, msisdn,
                        menuNodes, newSessionID);
                break;

            case CONFIRM:
                responseMenu = confirmProductUpload();
                break;

            case ADVICE_QUERY:
                responseMenu = adviceQueryMessage();
                break;

            case UNDER_MAINTENANCE:
                responseMenu = underMaintenance();
                break;

            default:
                responseMenu = underMaintenance();
                navBar = NavigationBar.MAIN;
                break;
        }

        if (menuComponents == null) {

            AgUssdMenu newMenu = retrieveMenuFromDB(responseMenu);
            if (menuItems == null) {
                menuItems = newMenu.getMenuItems();
            }
            if (menuTitle == null) {
                menuTitle = newMenu.getMenuTitleText();
                menuTitle = convertMenuNodeToString(menuTitle, menuNodes);
            }

            menuComponents = new UssdMenuComponents();
            menuComponents.setNavBar(navBar);
            menuComponents.setMenuItems(menuItems);
            menuComponents.setMenuTitle(menuTitle);
            menuComponents.setResponseMenu(responseMenu);
        }

        return menuComponents;
    }

    private UssdMenuComponents processStaticMenu(String requestInput,
            AgUssdMenu menu, String msisdn, Map<String, String> menuNodes,
            String newSessionID, boolean isRegistered) throws MyCustomException {

        String chosenMenuItem = getChosenStaticMenuItem(menu, requestInput);
        NavigationBar navBar = NavigationBar.PREVIOUS_AND_MAIN;
        Set<AgMenuItemIndex> menuItems = null;
        String menuTitle = null;
        UssdMenuComponents menuComponents = null;
        MenuName responseMenu = MenuName.MAIN_MENU;

        logger.debug("Chosen Menu Item: " + chosenMenuItem);

        //set language
        if (menu.getMenuName() == MenuName.LANGUAGE_MENU) {
            menuComponents = setLanguage(requestInput, msisdn);

        } else {

            ItemTag tag;
            UssdFunction function;

            switch (chosenMenuItem) {

                case "1010": //Find buyers
                    AgProduct product = saveNewProduct(msisdn, newSessionID,
                            ItemTag.UNKNOWN, UssdFunction.FIND_BUYERS);

                    menuComponents = displayItemSuperTypes(requestInput,
                            msisdn, menuNodes, newSessionID, isRegistered, product);
                    break;

                case "1008": //Sell an item
                    product = saveNewProduct(msisdn, newSessionID,
                            ItemTag.PRODUCE, UssdFunction.SELL);

                    menuComponents = displayItemSuperTypes(requestInput, msisdn,
                            menuNodes, newSessionID, isRegistered, product);

                    break;

                case "1019": //Market prices
                    //For now we are doing district prices for produce only
                    product = saveNewProduct(msisdn, newSessionID,
                            ItemTag.PRODUCE, UssdFunction.MARKET_PRICES);

                    menuComponents = displayCategories(requestInput, newSessionID,
                            menuNodes, product, ItemTag.PRODUCE);
                    break;

                case "1012": //Sellers/Traders
                    product = saveNewProduct(msisdn, newSessionID,
                            ItemTag.UNKNOWN, UssdFunction.SELLERS_TRADERS);

                    menuComponents = displayItemSuperTypes(requestInput,
                            msisdn, menuNodes, newSessionID, isRegistered, product);
                    break;

                case "1013"://Inputs & Tools
                    product = saveNewProduct(msisdn, newSessionID,
                            ItemTag.PRODUCE, UssdFunction.INPUT_TOOLS);

                    menuComponents = displayCategories(requestInput, newSessionID,
                            menuNodes, product, ItemTag.INPUT);
                    break;

                case "1016": // Get Advice
                    responseMenu = getAdvice();
                    break;

                case "1017":// My Account
                    responseMenu = myAccount();
                    break;

//                case "1011":
//                    responseMenu = marketPrices();
//                    break;
//
//                case "1014":
//                    responseMenu = phoneFarming();
//                    break;
//
//                case "1015":
//                    responseMenu = weather();
//                    break;
//
//                case "1018":
//                    responseMenu = MenuName.MATCHED_PRODUCTS;
//                    menuItems = getMatchedProducts(msisdn);
//                    break;
                case "1220":
                    menuComponents = processTransportArea(requestInput,
                            msisdn, menuNodes, newSessionID);
                    break;
//
                case "1021"://Produce

                    product = fetchProduct(msisdn);
                    menuComponents = displayCategories(requestInput,
                            newSessionID, menuNodes, product, ItemTag.PRODUCE);
                    break;

                case "1022": //VAP

                    product = fetchProduct(msisdn);
                    menuComponents = displayCategories(requestInput,
                            newSessionID, menuNodes, product, ItemTag.VAP);
                    break;

                case "1023": //Inputs

                    product = fetchProduct(msisdn);
                    menuComponents = displayCategories(requestInput,
                            newSessionID, menuNodes, product, ItemTag.INPUT);
                    break;

                case "1181":
                    menuComponents = processRegion(requestInput,
                            msisdn, menuNodes, newSessionID);
                    break;

                case "1182":
                    menuComponents = processRegion(requestInput,
                            msisdn, menuNodes, newSessionID);
                    break;

                case "1183":
                    menuComponents = processRegion(requestInput,
                            msisdn, menuNodes, newSessionID);
                    break;

                case "1184":
                    menuComponents = processRegion(requestInput,
                            msisdn, menuNodes, newSessionID);
                    break;

                case "1185":
                    menuComponents = processRegion(requestInput,
                            msisdn, menuNodes, newSessionID);
                    break;

                case "1061":
                    menuComponents = processPaymentMethod(requestInput,
                            msisdn, menuNodes, newSessionID);
                    break;

                case "1062":
                    menuComponents = processPaymentMethod(requestInput,
                            msisdn, menuNodes, newSessionID);
                    break;

                case "1071":
                    menuComponents = processTransportArea(requestInput,
                            msisdn, menuNodes, newSessionID);
                    break;

                case "1072":
                    menuComponents = processTransportArea(requestInput,
                            msisdn, menuNodes, newSessionID);
                    break;

                case "1073":
                    menuComponents = processTransportArea(requestInput,
                            msisdn, menuNodes, newSessionID);
                    break;

                case "1081":
                    menuComponents = confirm(requestInput, msisdn,
                            menuNodes, newSessionID);
                    break;

                case "1082":
                    responseMenu = cancelUpload();
                    break;

                case "1105":

                    product = saveNewProduct(msisdn, newSessionID,
                            ItemTag.PRODUCE, UssdFunction.MATCHED_BUYERS);

                    menuComponents = displayMatchedBuyers(requestInput, newSessionID,
                            menuNodes, product, ItemTag.PRODUCE);
                    break;

                case "1131":
                    menuComponents = processBuyerLocation(requestInput,
                            msisdn, menuNodes, newSessionID);
                    break;

                case "1132":
                    menuComponents = processBuyerLocation(requestInput,
                            msisdn, menuNodes, newSessionID);
                    break;

                case "1133":
                    menuComponents = processBuyerLocation(requestInput,
                            msisdn, menuNodes, newSessionID);
                    break;

                case "1107":
                    responseMenu = underMaintenance();
                    navBar = NavigationBar.MAIN;
                    break;

                case "1034":
                    responseMenu = customProduce();
                    break;

                case "1041"://Produce
                    responseMenu = cageTilapia();
                    break;

                case "1042":
                    responseMenu = pondTilapia();
                    break;

                case "1043":
                    responseMenu = cageCatfish();
                    break;

                case "1044":
                    responseMenu = pondCatfish();
                    break;

                case "1101":
                    responseMenu = underMaintenance();
                    navBar = NavigationBar.MAIN;
                    break;

                case "1102":
                    responseMenu = underMaintenance();
                    navBar = NavigationBar.MAIN;
                    break;

                case "1103":
                    responseMenu = underMaintenance();
                    navBar = NavigationBar.MAIN;
                    break;

                case "1104":
                    responseMenu = underMaintenance();
                    navBar = NavigationBar.MAIN;
                    break;

                case "1106":
                    responseMenu = underMaintenance();
                    navBar = NavigationBar.MAIN;
                    break;

                case "1121":
                    responseMenu = catFishBuyers();//get from remote/DB
                    break;

                case "1122":
                    responseMenu = tilapiaBuyers();//get from remote/DB
                    break;

                case "1141":
                    responseMenu = ayubuBuyer();//get from remote/DB
                    break;

                case "1142":
                    responseMenu = willyBuyer();//get from remote/DB
                    break;

                case "1143":
                    responseMenu = smallGodBuyer();//get from remote/DB
                    break;

                case "1151":
                    menuComponents = contactBuyer(requestInput, msisdn,
                            menuNodes, newSessionID);
                    break;

                case "1152":
                    responseMenu = dontContactBuyer();
                    break;

                case "1161":
                    responseMenu = MenuName.BUYER_CATEGORY;
                    break;

                case "1162":
                    responseMenu = underMaintenance();
                    navBar = NavigationBar.MAIN;
                    break;

                case "1163":
                    responseMenu = underMaintenance();
                    navBar = NavigationBar.MAIN;
                    break;

                case "1171":
                    responseMenu = sellingProduceCatfish();
                    break;

                case "1172":
                    responseMenu = sellingProduceTilapia();
                    break;

                case "1173":
                    responseMenu = sellingProduceRice();
                    break;

                case "1174":
                    responseMenu = sellingProduceushroom();
                    break;

                case "1191":
                    responseMenu = sellingCentralKampal();
                    break;

                case "1192":
                    responseMenu = underMaintenance();
                    navBar = NavigationBar.MAIN;
                    break;

                case "1193":
                    responseMenu = underMaintenance();
                    navBar = NavigationBar.MAIN;
                    break;

                case "1211":
                    responseMenu = contactSeller();
                    break;

                case "1212":
                    responseMenu = contactSeller();
                    break;

                case "1221":
                    responseMenu = contactSellerConfirm();
                    break;

                case "1222":
                    responseMenu = contactSellerCancel();
                    break;

                case "1231":
                    responseMenu = tilapiaCommodityPrice();
                    break;

                case "1232":
                    responseMenu = catFishCommodityPrice();
                    break;

                case "1233":
                    responseMenu = vanillaCommodityPrice();
                    break;

                case "1241":
                    responseMenu = centralCommodityPrice();
                    break;

                case "1242":
                    responseMenu = easternCommodityPrice();
                    break;

                case "1243":
                    responseMenu = northernCommodityPrice();
                    break;

                case "1244":
                    responseMenu = westernCommodityPrice();
                    break;

                case "1261":
                    responseMenu = centralMarketPrice();
                    break;

                case "1262":
                    responseMenu = easternMarketPrice();
                    break;

                case "1263":
                    responseMenu = northernMarketPrice();
                    break;

                case "1264":
                    responseMenu = westernMarketPrice();
                    break;

                case "1271":
                    responseMenu = regionAreaMarketPrice();
                    break;

                case "1272":
                    responseMenu = regionAreaMarketPrice();
                    break;

                case "1273":
                    responseMenu = regionAreaMarketPrice();
                    break;

                case "1281":
                    responseMenu = chosenMarket();
                    break;

                case "1282":
                    responseMenu = chosenMarket();
                    break;

                case "1283":
                    responseMenu = chosenMarket();
                    break;

                case "1291":
                    responseMenu = commodityPrices();
                    break;

                case "1292":
                    responseMenu = MenuName.SELLING_CATEGORIES;
                    break;

                case "1293":
                    responseMenu = underMaintenance();
                    navBar = NavigationBar.MAIN;
                    break;

                case "1294":
                    responseMenu = underMaintenance();
                    navBar = NavigationBar.MAIN;
                    break;

                case "1301":
                    responseMenu = chosenInputToolsProduce();
                    break;

                case "1302":
                    responseMenu = chosenInputToolsProduce();
                    break;

                case "1311":
                    responseMenu = chosenInputToolsItem();
                    break;

                case "1312":
                    responseMenu = chosenInputToolsItem();
                    break;

                case "1313":
                    responseMenu = chosenInputToolsItem();
                    break;

                case "1314":
                    responseMenu = chosenInputToolsItem();
                    break;

                case "1321":
                    responseMenu = chosenInputToolsByRegion();
                    break;

                case "1322":
                    responseMenu = chosenInputToolsByDistrict();
                    break;

                case "1323":
                    responseMenu = inputToolsSellerList();
                    break;

                case "1331":
                    responseMenu = inputToolsSellerList();
                    break;

                case "1332":
                    responseMenu = inputToolsSellerList();
                    break;

                case "1333":
                    responseMenu = inputToolsSellerList();
                    break;

                case "1334":
                    responseMenu = inputToolsSellerList();
                    break;

                case "1335":
                    responseMenu = inputToolsSellerList();
                    break;

                case "1336":
                    responseMenu = inputToolsSellerList();
                    break;

                case "1341":
                    responseMenu = inputToolsSellerList();
                    break;

                case "1342":
                    responseMenu = inputToolsSellerList();
                    break;

                case "1343":
                    responseMenu = inputToolsSellerList();
                    break;

                case "1351":
                    responseMenu = goToCheckOutPage();
                    break;

                case "1352":
                    responseMenu = goToCheckOutPage();
                    break;

                case "1361":
                    responseMenu = XmlProcessor.this.confirm();
                    break;

                case "1371":
                    responseMenu = learningCategory();
                    break;

                case "1381":
                    responseMenu = learningSubCategories1();
                    break;

                case "1382":
                    responseMenu = learningSubCategories1();
                    break;

                case "1401":
                    responseMenu = learningSubCategories2();
                    break;

                case "1402":
                    responseMenu = learningSubCategories2();
                    break;

                case "1403":
                    responseMenu = learningSubCategories2();
                    break;

                case "1404":
                    responseMenu = learningSubCategories2();
                    break;

                case "1411":
                    responseMenu = learningSubCategories3();
                    break;

                case "1412":
                    responseMenu = learningSubCategories3();
                    break;

                case "1413":
                    responseMenu = learningSubCategories3();
                    break;

                case "1414":
                    responseMenu = learningSubCategories3();
                    break;

                case "1421":
                    responseMenu = weatherRegion();
                    break;

                case "1422":
                    responseMenu = weatherRegion();
                    break;

                case "1423":
                    responseMenu = weatherRegion();
                    break;

                case "1424":
                    responseMenu = weatherRegion();
                    break;

                case "1431":
                    responseMenu = weatherDistrict();
                    break;

                case "1432":
                    responseMenu = weatherDistrict();
                    break;

                case "1433":
                    responseMenu = weatherDistrict();
                    break;

                case "1441":
                    responseMenu = adviceTopic();
                    break;

                case "1442":
                    responseMenu = adviceTopic();
                    break;

                default:
                    responseMenu = underMaintenance();
                    navBar = NavigationBar.MAIN;
                    break;
            }

            if (menuComponents == null) {

                AgUssdMenu newMenu = retrieveMenuFromDB(responseMenu);

                if (menuTitle == null) {
                    menuTitle = newMenu.getMenuTitleText();
                    menuTitle = convertMenuNodeToString(menuTitle, menuNodes);
                }

                if (menuItems == null) {
                    menuItems = newMenu.getMenuItems();
                } else if (menuItems.isEmpty()) {
                    menuTitle += "\n" + convertMenuNodeToString("1510",
                            menuNodes); //no records
                }

                menuComponents = new UssdMenuComponents();
                menuComponents.setNavBar(navBar);
                menuComponents.setMenuItems(menuItems);
                menuComponents.setMenuTitle(menuTitle);
                menuComponents.setResponseMenu(responseMenu);
            }
        }

        return menuComponents;
    }

//    AgProduct fetchProduct(String sessionId, String sellerContact)
//            throws MyCustomException {
//
//        Set<Object> sessionIds = new HashSet<>();
//        sessionIds.add(sessionId);
//
//        Set<Object> sellerContacts = new HashSet<>();
//        sellerContacts.add(sellerContact);
//
//        Map<String, Set<Object>> propertyNameValues = new HashMap<>();
//        propertyNameValues.put("sessionId", sessionIds);
//        propertyNameValues.put("sellerContact", sellerContacts);
//
//        AgProduct product = internalDbAccess
//                .fetchEntity(AgProduct.class, propertyNameValues);
//
//        return product;
//    }
    AgProduct fetchProduct(String userContact)
            throws MyCustomException {

        AgProduct productSale
                = internalDbAccess.getMostRecentRecord(AgProduct.class,
                        "id", "userContact", userContact);

        return productSale;
    }

    UssdMenuComponents registerName(
            String requestInput, Map<String, String> menuNodes,
            String newSessionID, AgClient client)
            throws MyCustomException {

        MenuName responseMenu = MenuName.REGISTER_DISTRICT;

        client.setName(requestInput); //update name in db

        internalDbAccess.saveOrUpdateEntity(client);

        UssdMenuComponents menuComponents
                = getUssdMenuComponentsHelper(responseMenu, menuNodes);
        menuComponents.setNavBar(NavigationBar.PREVIOUS_AND_MAIN);

        return menuComponents;
    }

    CreateAccountResponse registerClient(AgClient client)
            throws MyCustomException {

        String msisdn = client.getMsisdn();
        String name = client.getName();
        String district = client.getDistrict();

        CreateAccountRequest createAccount = new CreateAccountRequest();

        CreateAccountRequest.Params params = createAccount.new Params();
        params.setMsisdn(msisdn);
        params.setName(name);
        params.setDistrict(district);

        Credentials credentials = new Credentials();
        credentials.setApiPassword("");
        credentials.setAppId("");
        credentials.setTokenId("");

        createAccount.setCredentials(credentials);
        createAccount.setMethodName(APIMethodName.CREATE_ACCOUNT.getValue());
        createAccount.setParams(params);

        String jsonReq = GeneralUtils.convertToJson(createAccount,
                CreateAccountRequest.class);
        GeneralUtils.toPrettyJson(jsonReq);

        String response = clientPool.sendRemoteRequest(jsonReq,
                remoteUnitConfig.getDSMBridgeRemoteUnit());
        //delete after tests
//        String response = "{\n"
//                + "  \"success\": true,\n"
//                + "  \"data\": {\n"
//                + "    \"user_id\": \" " + msisdn + "\",\n"
//                + "    \"is_exist\":false,\n"
//                + "    \"status\": \"PENDING_VERIFICATION\", // |   REGISTERED\n"
//                + "    \"description\": \"New client account created, pending verification\"\n"
//                + "  }\n"
//                + "}";

        CreateAccountResponse createAccountResponse = GeneralUtils
                .convertFromJson(response, CreateAccountResponse.class);
        return createAccountResponse;
    }

    UssdMenuComponents registerDistrict(
            String requestInput, Map<String, String> menuNodes,
            String newSessionID, AgClient client)
            throws MyCustomException {

        client.setDistrict(requestInput);
        client.setIsRegistered(Boolean.TRUE);
        registerClient(client);
        internalDbAccess.saveOrUpdateEntity(client);

        UssdMenuComponents menuComponents
                = getUssdMenuComponentsHelper(MenuName.MAIN_MENU, menuNodes);
        menuComponents.setNavBar(NavigationBar.PREVIOUS_AND_MAIN);

        return menuComponents;
    }

    UssdMenuComponents processCustomProduce(String requestInput,
            String clientMsisdn, Map<String, String> menuNodes,
            String newSessionID) throws MyCustomException {

        MenuName responseMenu = MenuName.PRODUCT_DESCRIPTION;

        AgProduct product = fetchProduct(clientMsisdn);
        product.setItemName(requestInput);

        internalDbAccess.saveOrUpdateEntity(product);

        UssdMenuComponents menuComponents
                = getUssdMenuComponentsHelper(responseMenu, menuNodes);
        menuComponents.setNavBar(NavigationBar.PREVIOUS_AND_MAIN);

        return menuComponents;
    }

    UssdMenuComponents processItemDistrict(String requestInput,
            String clientMsisdn, Map<String, String> menuNodes,
            String newSessionID) throws MyCustomException {

        MenuName responseMenu = MenuName.ITEM_PLACE;

        AgProduct productSale = fetchProduct(clientMsisdn);
        productSale.setDistrictName(requestInput);

        internalDbAccess.saveOrUpdateEntity(productSale);

        UssdMenuComponents menuComponents
                = getUssdMenuComponentsHelper(responseMenu, menuNodes);
        menuComponents.setNavBar(NavigationBar.PREVIOUS_AND_MAIN);

        return menuComponents;
    }

    UssdMenuComponents processProductDescription(String requestInput,
            String clientMsisdn, Map<String, String> menuNodes,
            String newSessionID) throws MyCustomException {

        MenuName responseMenu = MenuName.ITEM_PLACE;

        AgProduct productSale = fetchProduct(clientMsisdn);
        productSale.setItemDescription(requestInput);

        internalDbAccess.saveOrUpdateEntity(productSale);

        UssdMenuComponents menuComponents
                = getUssdMenuComponentsHelper(responseMenu, menuNodes);
        menuComponents.setNavBar(NavigationBar.PREVIOUS_AND_MAIN);

        return menuComponents;
    }

    UssdMenuComponents processItemPlace(String requestInput,
            String clientMsisdn, Map<String, String> menuNodes,
            String newSessionID) throws MyCustomException {

        MenuName responseMenu = MenuName.QUANTITY;

        AgProduct productSale = fetchProduct(clientMsisdn);
        productSale.setPlace(requestInput);
        internalDbAccess.saveOrUpdateEntity(productSale);

        UssdMenuComponents menuComponents
                = getUssdMenuComponentsHelper(responseMenu, menuNodes);
        menuComponents.setNavBar(NavigationBar.PREVIOUS_AND_MAIN);

        return menuComponents;
    }

    UssdMenuComponents processItemQuantity(String requestInput,
            String clientMsisdn, Map<String, String> menuNodes,
            String newSessionID) throws MyCustomException {

        MenuName responseMenu = MenuName.PRICE;

        AgProduct productSale = fetchProduct(clientMsisdn);
        productSale.setQuantity(requestInput);
        internalDbAccess.saveOrUpdateEntity(productSale);

        UssdMenuComponents menuComponents
                = getUssdMenuComponentsHelper(responseMenu, menuNodes);
        menuComponents.setNavBar(NavigationBar.PREVIOUS_AND_MAIN);

        return menuComponents;
    }

    UssdMenuComponents processItemPrice(String requestInput,
            String clientMsisdn, Map<String, String> menuNodes,
            String newSessionID) throws MyCustomException {

        MenuName responseMenu = MenuName.PAY_METHOD;

        AgProduct productSale = fetchProduct(clientMsisdn);
        productSale.setSellerPrice(Integer.parseInt(requestInput));
        internalDbAccess.saveOrUpdateEntity(productSale);

        UssdMenuComponents menuComponents
                = getUssdMenuComponentsHelper(responseMenu, menuNodes);
        menuComponents.setNavBar(NavigationBar.PREVIOUS_AND_MAIN);

        return menuComponents;
    }

    UssdMenuComponents displayItemSuperTypes(String requestInput,
            String clientMsisdn, Map<String, String> menuNodes,
            String newSessionID, boolean isRegistered, AgProduct product)
            throws MyCustomException {

        MenuName responseMenu = MenuName.SELECT_ITEM_TYPE;

        //Must be registered to find buyers
        if (!isRegistered
                && (product.getUssdFunction() == UssdFunction.FIND_BUYERS
                || product.getUssdFunction() == UssdFunction.SELL)) {
            responseMenu = MenuName.REGISTER_NAME;
        }

        UssdMenuComponents menuComponents
                = getUssdMenuComponentsHelper(responseMenu, menuNodes);
        menuComponents.setNavBar(NavigationBar.PREVIOUS_AND_MAIN);

        return menuComponents;
    }

    ScreenPagination paginateData(Set<? extends MenuItem> catList,
            int itemsPerScreen) {

        int categoryCount = catList.size();

        ScreenPagination screens = new ScreenPagination();
        List<ScreenPagination.Data> dataList = new LinkedList<>();
        screens.setScreenData(dataList);

        //int perScreen = 8;//max 8 items per screen 
        int perScreen = itemsPerScreen;//max 8 items per screen 
        int screenCount = 1;

        int quotient = categoryCount / perScreen;
        int remainder = categoryCount % perScreen;

        logger.debug("Cat count: " + categoryCount);
        logger.debug("Quotient : " + quotient);
        logger.debug("Remainder: " + remainder);

        List<ScreenPagination.Item> items = new LinkedList<>();

        for (MenuItem element : catList) {

            perScreen--;

            logger.debug("Quotient in: " + quotient);

            ScreenPagination.Item item = screens.new Item();

            logger.debug("ELEM ID: " + element.getId());
            logger.debug("ELEM Nm: " + element.getName());

            item.setId(element.getId());
            item.setName(element.getName());
            //item.setExtra(element.getExtra());

            items.add(item);

            if (quotient <= 0) {

                remainder--;

                if (remainder <= 0) {

                    ScreenPagination.Data data = screens.new Data();
                    data.setScreen(screenCount);
                    data.setItems(Collections.unmodifiableList(items));
                    dataList.add(data);
                }

            } else if (perScreen == 0) {

                ScreenPagination.Data data = screens.new Data();
                data.setScreen(screenCount);
                data.setItems(Collections.unmodifiableList(items));
                dataList.add(data);

                items = new LinkedList<>();

                quotient--;
                screenCount++;
                perScreen = itemsPerScreen;
            }
        }
        return screens;
    }

    List<? extends MenuItem> getScreenDetails(
            ScreenPagination screenPages, int screenNum) {

        logger.debug("Getting screen @: " + screenNum);

        List<ScreenPagination.Data> data = screenPages.getScreenData();

        if (data == null || data.isEmpty()) {
            return new LinkedList<>();
        }

        return data.get(screenNum).getItems();
    }

    Set<? extends MenuItem> listMarketPrices(
            Set<MarketPriceResponse.Data> marketData)
            throws MyCustomException {

        Set<MenuItem> menuItems = new HashSet<>();
        menuItems.addAll(marketData);

        return menuItems;
    }

//    Set<? extends MenuItem> listMarketPrices(
//            Set<MarketPriceResponse.Data> marketData, Region region)
//            throws MyCustomException {
//
//        Set<MenuItem> menuItems = new HashSet<>();
//
//        for (MarketPriceResponse.Data data : marketData) {
//
//            if (region == Region.ALL) {
//                menuItems.addAll(data.getDefaultProduct());
//                continue;
//            }
//
//            if (region == Region.convertToEnum(data.getDistrictName())) {
//                return data.getDefaultProduct();
//            }
//        }
//        return menuItems;
//    }
    Set<? extends MenuItem> filterSellersByDistrict(
            GetSellersResponse.Data marketData, int districtId)
            throws MyCustomException {

        Set<MenuItem> menuItems = new HashSet<>();

        Set<GetSellersResponse.Data.SellerDistrict> districts
                = marketData.getDistricts();

        if (districtId > 0) {

            for (GetSellersResponse.Data.SellerDistrict district : districts) {

                if (district.getId() == districtId) {
                    menuItems.add(district);
                }
            }
        } else {
            menuItems.addAll(districts);
        }

        return menuItems;
    }

    Set<? extends MenuItem> getMatchedProducts(GetBuyerResponse buyers)
            throws MyCustomException {

        Set<MenuItem> menuList = new HashSet<>();

        for (GetBuyerResponse.Data data : buyers.getData()) {
            menuList.addAll(data.getBuying());
        }
        return menuList;
    }

    Set<? extends MenuItem> getCategories(GetCategoryResponse categories)
            throws MyCustomException {

        Set<? extends MenuItem> categoryMenuList = categories.getData().getProduce();

        return categoryMenuList;
    }

    Set<? extends MenuItem> getSubCategories(GetCategoryResponse categories,
            int categoryId)
            throws MyCustomException {

        //        Set<? extends MenuItem> subCategories
        //= getSubCategories(allInfo.getData().getProduce(), categoryId);
        Set<? extends MenuItem> subCategories
                = getChildrenMenuItems(categories.getData().getProduce(), categoryId);

        return subCategories;
    }

    Set<? extends MenuItem> filterMarketsByDistrictAndProduct(
            Set<MarketPriceResponse2.Data> marketData, AgProduct product)
            throws MyCustomException {

        Region region = product.getRegion();
        int districtId = product.getDistrictId();
        int productId = product.getSubCategoryId();

        Set<MenuItem> menuItems = new HashSet<>();

        for (MarketPriceResponse2.Data data : marketData) {

            // if (region == Region.ALL)  -> TO-DO: if region/district is ALL
            for (District district : data.getDistricts()) {

                if (region != Region.convertToEnum(data.getRegion())) {
                    break;
                }

                if (district.getId() == districtId) {

                    for (Product itemProduct : district.getProducts()) {

                        if (productId == itemProduct.getId()) {
                            menuItems.addAll(itemProduct.getMarkets());
                            return menuItems;
                        }
                    }
                }
            }
        }
        return menuItems;
    }

    Set<? extends MenuItem> filterMarketPrices(
            Set<MarketPriceResponse2.Data> marketData, AgProduct product)
            throws MyCustomException {

        Region region = product.getRegion();
        int districtId = product.getDistrictId();
        int productId = product.getSubCategoryId();
        int marketId = product.getMarketId();

        Set<MenuItem> menuItems = new HashSet<>();

        for (MarketPriceResponse2.Data data : marketData) {

            // if (region == Region.ALL)  -> TO-DO: if region/district is ALL
            for (District district : data.getDistricts()) {

                if (region != Region.convertToEnum(data.getRegion())) {
                    break;
                }

                if (district.getId() == districtId) {

                    for (Product itemProduct : district.getProducts()) {

                        if (productId == itemProduct.getId()) {

                            for (Product.Market market : itemProduct.getMarkets()) {

                                if (marketId == market.getId()) {
                                    menuItems.addAll(market.getPrices());
                                    return menuItems;
                                }
                            }

                        }
                    }
                }
            }
        }
        return menuItems;
    }

    Set<? extends MenuItem> filterSellersList(
            GetSellersResponse.Data sellers,
            AgProduct product)
            throws MyCustomException {

        int districtId = product.getDistrictId();

        Set<MenuItem> menuItems = new HashSet<>();

        Set<GetSellersResponse.Data.SellerDistrict> districts
                = sellers.getDistricts();

        for (GetSellersResponse.Data.SellerDistrict district : districts) {

            if (district.getId() == districtId) {

                for (Seller seller : district.getSellers()) {

                    String sellerName = seller.getName();

                    for (Seller.Product sellerProduct : seller.getProducts()) {

                        String unit = sellerProduct.getMeasureUnit();
                        int price = sellerProduct.getPrice();
                        int id = sellerProduct.getId();
                        String productName = sellerProduct.getName();

                        menuItems.add(new Item(id, sellerName + " -"
                                + productName + " -" + price + "/" + unit));
                    }

                }
                return menuItems;
            }
        }
        return menuItems;
    }

    Set<? extends MenuItem> filterBuyersList(
            GetBuyerResponse buyers,
            AgProduct product)
            throws MyCustomException {

        int productId = product.getSubCategoryId();

        Set<MenuItem> menuItems = new HashSet<>();

        Set<GetBuyerResponse.Data> dataList = buyers.getData();

        for (GetBuyerResponse.Data data : dataList) {

            for (GetBuyerResponse.Data.Product item : data.getBuying()) {

                //item.getBuyerLocation();
                logger.debug("ProductId: " + productId + ", item.prodId: " + item.getProductId());

                if (productId == item.getProductId()) {
                    // also check for location (nearby | national | interna..)
                    menuItems.add(data);
                }
            }
        }
        return menuItems;
    }

    Seller getSeller(GetSellersResponse.Data sellers,
            AgProduct product) throws MyCustomException {

        int districtId = product.getDistrictId();
        int sellerId = product.getSellerId();

        Set<SellerDistrict> districts = sellers.getDistricts();

        for (SellerDistrict district : districts) {

            if (district.getId() == districtId) {

                for (Seller seller : district.getSellers()) {
                    if (sellerId == seller.getId()) {
                        return seller;
                    }
                }
            }
        }
        return null;
    }

    GetBuyerResponse.Data getBuyer(GetBuyerResponse buyers,
            AgProduct product) throws MyCustomException {

        int buyerId = product.getBuyerId();
        for (GetBuyerResponse.Data data : buyers.getData()) {

            if (buyerId == data.getBuyerId()) {
                return data;
            }
        }
        return null;
    }

    UssdMenuComponents displayMarketDistricts(String requestInput, String msisdn,
            Map<String, String> menuNodes, String newSessionID, Region region)
            throws MyCustomException {

        MenuName responseMenu = MenuName.MARKET_DISTRICTS;

        AgNavigation navigation = getNavigationByMsisdn(msisdn);

        MenuHistory menuHistory
                = getMenuHistoryHelper(navigation.getMenuHistory());
        MenuHistory.Data menuData = getCurrentMenuData(menuHistory);

        DataItem dataItem = getDataValue(menuData, requestInput);
        String itemName = dataItem.getDataValue();
        String itemId = dataItem.getDataId();

        logger.debug("itemName: " + itemName + ", itemId: " + itemId);

        AgProduct product = fetchProduct(msisdn);
        product.setRegion(region);
        internalDbAccess.saveOrUpdateEntity(product);

        MarketPriceResponse markets = getMarketPricesRemote(product);
        cacheAllMarketInfo(markets, product.getUserContact());

        ScreenPagination screenPages = setMarketDistrictsNavigation(product);

        UssdMenuComponents menuComponents
                = breakIntoPages(requestInput, responseMenu,
                        menuNodes, newSessionID, screenPages,
                        new TitleAddition(false, product.getItemName()));

        return menuComponents;
    }

    UssdMenuComponents displayMarkets(String requestInput,
            String msisdn, Map<String, String> menuNodes,
            String newSessionID) throws MyCustomException {

        MenuName responseMenu = MenuName.MARKETS;

        AgNavigation navigation = getNavigationByMsisdn(msisdn);

        MenuHistory menuHistory
                = getMenuHistoryHelper(navigation.getMenuHistory());
        MenuHistory.Data menuData = getCurrentMenuData(menuHistory);

        DataItem data = getDataValue(menuData, requestInput);
        String itemName = data.getDataValue();
        String dataId = data.getDataId();

        AgProduct product = fetchProduct(msisdn);
        product.setDistrictId(Integer.valueOf(dataId));
        product.setDistrictName(itemName);
        internalDbAccess.saveOrUpdateEntity(product);

        ScreenPagination screenPages = setMarketNavigation(product);

        UssdMenuComponents menuComponents
                = breakIntoPages(requestInput, responseMenu,
                        menuNodes, newSessionID, screenPages,
                        new TitleAddition(false, product.getItemName()));

        return menuComponents;
    }

    UssdMenuComponents displayMarketPricesEnd(String requestInput,
            String msisdn, Map<String, String> menuNodes,
            String newSessionID) throws MyCustomException {

        MenuName responseMenu = MenuName.MARKET_PRICES_END;

        AgNavigation navigation = getNavigationByMsisdn(msisdn);

        MenuHistory menuHistory
                = getMenuHistoryHelper(navigation.getMenuHistory());
        MenuHistory.Data menuData = getCurrentMenuData(menuHistory);

        DataItem data = getDataValue(menuData, requestInput);
        String itemName = data.getDataValue();
        String dataId = data.getDataId();

        String split[] = itemName.split("-");
        String name;
        String price;
        String district;

        if (split == null || split.length < 3) {
            name = itemName;
            price = "";
            district = "";
        } else {
            district = split[0].trim();
            name = split[1].trim();
            price = (split[2].trim());
        }

        AgProduct product = fetchProduct(msisdn);
        product.setDistrictId(Integer.valueOf(dataId));
        product.setDistrictName(district);
        product.setItemName(name);
        internalDbAccess.saveOrUpdateEntity(product);

//       MarketPriceResponse markets = getMarketPricesCached(navigation);
//       MarketPriceResponse.Data priceData = getProduct(markets, 
//                Integer.valueOf(dataId));
        String titleAdd = "\n"
                + "Item : " + name + "\n"
                + "Price: " + price + "\n"
                + "District: " + district + "\n"
                + "Region: " + product.getRegion() + "\n";

        UssdMenuComponents menuComponents
                = getUssdMenuComponentsHelper(responseMenu, menuNodes,
                        new TitleAddition(false, titleAdd));
        menuComponents.setNavBar(NavigationBar.NONE);
        menuComponents.setIsIsEnd(true);

        return menuComponents;
    }

    MarketPriceResponse.Data getProduct(MarketPriceResponse data,
            int productId) throws MyCustomException {

        for (MarketPriceResponse.Data item : data.getData()) {

            if (item.getId() == productId) {

                return item;
            }
        }
        return null;
    }

    UssdMenuComponents displayMarketPrices(String requestInput,
            String msisdn, Map<String, String> menuNodes,
            String newSessionID) throws MyCustomException {

        MenuName responseMenu = MenuName.MARKET_PRICES;

        AgNavigation navigation = getNavigationByMsisdn(msisdn);

        MenuHistory menuHistory
                = getMenuHistoryHelper(navigation.getMenuHistory());
        MenuHistory.Data menuData = getCurrentMenuData(menuHistory);

        DataItem data = getDataValue(menuData, requestInput);
        String marketName = data.getDataValue();
        String marketId = data.getDataId();

        AgProduct product = fetchProduct(msisdn);
        product.setMarketId(Integer.valueOf(marketId));
        product.setMarketName(marketName);
        internalDbAccess.saveOrUpdateEntity(product);

        ScreenPagination screenPages = setMarketPriceNavigation(product);
        String title = marketName + " - " + product.getItemName();

        UssdMenuComponents menuComponents
                = breakIntoPages(requestInput, responseMenu,
                        menuNodes, newSessionID, screenPages,
                        new TitleAddition(true, title));

        return menuComponents;
    }

    UssdMenuComponents displaySellerList(String requestInput,
            String msisdn, Map<String, String> menuNodes,
            String newSessionID) throws MyCustomException {

        MenuName responseMenu = MenuName.SELLER_LIST;

        AgNavigation navigation = getNavigationByMsisdn(msisdn);

        MenuHistory menuHistory
                = getMenuHistoryHelper(navigation.getMenuHistory());
        MenuHistory.Data menuData = getCurrentMenuData(menuHistory);

        DataItem data = getDataValue(menuData, requestInput);
        String dataName = data.getDataValue();
        String dataId = data.getDataId();

        AgProduct product = fetchProduct(msisdn);
        product.setDistrictId(Integer.valueOf(dataId));
        product.setDistrictName(dataName);
        internalDbAccess.saveOrUpdateEntity(product);

        ScreenPagination screenPages = setSellersNavigation(product);
        String title = "\n" + product.getItemName() + " - " + dataName;
        UssdMenuComponents menuComponents
                = breakIntoPages(requestInput, responseMenu,
                        menuNodes, newSessionID, screenPages,
                        new TitleAddition(false, title));

        return menuComponents;
    }

    UssdMenuComponents displaySellerDistricts(String requestInput, String msisdn,
            Map<String, String> menuNodes, String newSessionID, Region region)
            throws MyCustomException {

        MenuName responseMenu = MenuName.SELLER_DISTRICTS;

        AgNavigation navigation = getNavigationByMsisdn(msisdn);

        MenuHistory menuHistory
                = getMenuHistoryHelper(navigation.getMenuHistory());
        MenuHistory.Data menuData = getCurrentMenuData(menuHistory);

        DataItem dataItem = getDataValue(menuData, requestInput);
        String itemName = dataItem.getDataValue();
        String itemId = dataItem.getDataId();

        AgProduct product = fetchProduct(msisdn);
        product.setRegion(region);
        internalDbAccess.saveOrUpdateEntity(product);

        GetSellersResponse sellers = getSellersBuyersRemote(product);
        cacheAllSellersInfo(sellers, product.getUserContact());

        ScreenPagination screenPages = setSellerDistrictNavigation(product);

        UssdMenuComponents menuComponents
                = breakIntoPages(requestInput, responseMenu,
                        menuNodes, newSessionID, screenPages,
                        new TitleAddition(false, product.getItemName()));

        return menuComponents;
    }

    UssdMenuComponents displayCategories(String requestInput, String sessionId,
            Map<String, String> menuNodes, AgProduct product, ItemTag tag)
            throws MyCustomException {

        MenuName responseMenu = MenuName.ITEM_CATEGORIES;

        product.setTag(tag);
        internalDbAccess.saveOrUpdateEntity(product);

        GetCategoryResponse categories = getCategoriesRemote(product);
        cacheAllCategoriesInfo(categories, product.getUserContact());

        ScreenPagination screenPages = setCategoryNavigation(product);

        UssdMenuComponents menuComponents
                = breakIntoPages(requestInput, responseMenu,
                        menuNodes, sessionId, screenPages,
                        new TitleAddition(false, product.getItemName()));

        return menuComponents;
    }

    UssdMenuComponents displaySubCategories(String requestInput,
            String msisdn, Map<String, String> menuNodes,
            String newSessionID) throws MyCustomException {

        MenuName responseMenu = MenuName.ITEM_SUBCATEGORIES;

        AgNavigation navigation = getNavigationByMsisdn(msisdn);

        MenuHistory menuHistory
                = getMenuHistoryHelper(navigation.getMenuHistory());
        MenuHistory.Data menuData = getCurrentMenuData(menuHistory);

        DataItem data = getDataValue(menuData, requestInput);
        String menuTitle = data.getDataValue();
        String dataId = data.getDataId();

        AgProduct product = fetchProduct(msisdn);
        product.setCategoryId(Integer.valueOf(dataId));
        internalDbAccess.saveOrUpdateEntity(product);

        ScreenPagination paged = setSubCategoryNavigation(product);

        UssdMenuComponents menuComponents
                = breakIntoPages(requestInput, responseMenu, menuNodes,
                        newSessionID, paged, new TitleAddition(true, menuTitle));

        return menuComponents;
    }

    UssdMenuComponents displayMatchedBuyers(String requestInput, String sessionId,
            Map<String, String> menuNodes, AgProduct product, ItemTag tag)
            throws MyCustomException {

        MenuName responseMenu = MenuName.MATCHED_PRODUCTS;

        product.setTag(tag);
        product.setIsMatched(Boolean.TRUE);
        internalDbAccess.saveOrUpdateEntity(product);

        GetBuyerResponse categories = getBuyersRemote(product);
        cacheAllBuyersInfo(categories, product.getUserContact());

        ScreenPagination screenPages = setMatchedProductsNavigation(product);

        UssdMenuComponents menuComponents
                = breakIntoPages(requestInput, responseMenu,
                        menuNodes, sessionId, screenPages, null);

        return menuComponents;
    }

    UssdMenuComponents displayRegions(String requestInput,
            String msisdn, Map<String, String> menuNodes,
            String newSessionID) throws MyCustomException {

        MenuName responseMenu = MenuName.REGION;

        AgNavigation navigation = getNavigationByMsisdn(msisdn);

        MenuHistory menuHistory
                = getMenuHistoryHelper(navigation.getMenuHistory());
        MenuHistory.Data menuData = getCurrentMenuData(menuHistory);
        menuData.getMenuName();

        DataItem data = getDataValue(menuData, requestInput);
        String itemName = data.getDataValue();
        String dataId = data.getDataId();

        AgProduct product = fetchProduct(msisdn);
        product.setSubCategoryId(Integer.valueOf(dataId));
        product.setItemName(itemName);
        internalDbAccess.saveOrUpdateEntity(product);

        UssdMenuComponents menuComponents
                = getUssdMenuComponentsHelper(responseMenu, menuNodes,
                        new TitleAddition(true, itemName));
        menuComponents.setNavBar(NavigationBar.PREVIOUS_AND_MAIN);

        return menuComponents;
    }

    UssdMenuComponents displayMatchedBuyerLocation(String requestInput,
            String msisdn, Map<String, String> menuNodes,
            String newSessionID) throws MyCustomException {

        MenuName responseMenu = MenuName.BUYER_LOCATION;

        AgNavigation navigation = getNavigationByMsisdn(msisdn);

        MenuHistory menuHistory
                = getMenuHistoryHelper(navigation.getMenuHistory());
        MenuHistory.Data menuData = getCurrentMenuData(menuHistory);
        menuData.getMenuName();

        DataItem data = getDataValue(menuData, requestInput);
        String itemName = data.getDataValue();
        String dataId = data.getDataId();

        AgProduct product = fetchProduct(msisdn);
        product.setSubCategoryId(Integer.valueOf(dataId));
        product.setItemName(itemName);
        internalDbAccess.saveOrUpdateEntity(product);

        UssdMenuComponents menuComponents
                = getUssdMenuComponentsHelper(responseMenu, menuNodes,
                        new TitleAddition(false, itemName));
        menuComponents.setNavBar(NavigationBar.PREVIOUS_AND_MAIN);

        return menuComponents;
    }

    boolean cacheAllSellersInfo(GetSellersResponse sellers, String clientMsisdn)
            throws MyCustomException {

        String allData = GeneralUtils.convertToJson(sellers,
                GetSellersResponse.class);

        AgNavigation navigation = getNavigationByMsisdn(clientMsisdn);
        navigation.setAllSellersInfo(allData);
        internalDbAccess.saveOrUpdateEntity(navigation);

        return true;
    }

    boolean cacheAllMarketInfo(MarketPriceResponse markets, String clientMsisdn)
            throws MyCustomException {

        String allData = GeneralUtils.convertToJson(markets,
                MarketPriceResponse.class);

        AgNavigation navigation = getNavigationByMsisdn(clientMsisdn);
        navigation.setAllMarketInfo(allData);
        internalDbAccess.saveOrUpdateEntity(navigation);

        return true;
    }

    boolean cacheAllCategoriesInfo(GetCategoryResponse categories,
            String clientMsisdn) throws MyCustomException {

        String allData = GeneralUtils.convertToJson(categories,
                GetCategoryResponse.class);

        AgNavigation navigation = getNavigationByMsisdn(clientMsisdn);
        navigation.setAllCategoriesData(allData);
        internalDbAccess.saveOrUpdateEntity(navigation);

        return true;
    }

    boolean cacheAllBuyersInfo(GetBuyerResponse buyers,
            String clientMsisdn) throws MyCustomException {

        String allData = GeneralUtils.convertToJson(buyers,
                GetBuyerResponse.class);

        AgNavigation navigation = getNavigationByMsisdn(clientMsisdn);
        navigation.setAllBuyersInfo(allData);
        internalDbAccess.saveOrUpdateEntity(navigation);

        return true;
    }

    ScreenPagination setMatchedProductsNavigation(AgProduct product)
            throws MyCustomException {

        AgNavigation navigation
                = getNavigationByMsisdn(product.getUserContact());

        GetBuyerResponse categories = getBuyersCached(navigation);

        Set<? extends MenuItem> menuItemList = getMatchedProducts(categories);

        ScreenPagination screenPages = paginateData(menuItemList,
                DEFAULT_ITEMS_PER_SCREEN);
        String pages = GeneralUtils.convertToJson(screenPages,
                ScreenPagination.class);

        navigation.setMatchedProductsNav(pages);
        internalDbAccess.saveOrUpdateEntity(navigation);

        return screenPages;
    }

    ScreenPagination setCategoryNavigation(AgProduct product)
            throws MyCustomException {

        AgNavigation navigation
                = getNavigationByMsisdn(product.getUserContact());

        GetCategoryResponse categories = getCategoriesCached(navigation);

        Set<? extends MenuItem> menuItemList = getCategories(categories);

        ScreenPagination screenPages = paginateData(menuItemList,
                DEFAULT_ITEMS_PER_SCREEN);
        String pages = GeneralUtils.convertToJson(screenPages,
                ScreenPagination.class);

        navigation.setCategoryNav(pages);
        internalDbAccess.saveOrUpdateEntity(navigation);

        return screenPages;
    }

    ScreenPagination setSubCategoryNavigation(AgProduct product)
            throws MyCustomException {

        AgNavigation navigation
                = getNavigationByMsisdn(product.getUserContact());

        GetCategoryResponse categories = getCategoriesCached(navigation);

        Set<? extends MenuItem> menuItemList = getSubCategories(categories,
                product.getCategoryId());

        ScreenPagination screenPages = paginateData(menuItemList,
                DEFAULT_ITEMS_PER_SCREEN);
        String pages = GeneralUtils.convertToJson(screenPages,
                ScreenPagination.class);

        navigation.setSubCategoryNav(pages);
        internalDbAccess.saveOrUpdateEntity(navigation);

        return screenPages;
    }

    ScreenPagination setMarketDistrictsNavigation(AgProduct product)
            throws MyCustomException {

        AgNavigation navigation
                = getNavigationByMsisdn(product.getUserContact());

        MarketPriceResponse markets = getMarketPricesCached(navigation);

        Set<? extends MenuItem> menuItemList
                = listMarketPrices(markets.getData());

        ScreenPagination screenPages = paginateData(menuItemList, 6);
        String pages = GeneralUtils.convertToJson(screenPages,
                ScreenPagination.class);

        navigation.setDistrictMarketsNav(pages);
        internalDbAccess.saveOrUpdateEntity(navigation);

        return screenPages;
    }

    ScreenPagination setSellerDistrictNavigation(AgProduct product)
            throws MyCustomException {

        AgNavigation navigation
                = getNavigationByMsisdn(product.getUserContact());

        GetSellersResponse markets = getSellersCached(navigation);

        Set<? extends MenuItem> menuItemList
                = filterSellersByDistrict(markets.getData(), 0);// get all

        ScreenPagination screenPages = paginateData(menuItemList,
                DEFAULT_ITEMS_PER_SCREEN);
        String pages = GeneralUtils.convertToJson(screenPages,
                ScreenPagination.class);

        navigation.setDistrictSellersNav(pages);
        internalDbAccess.saveOrUpdateEntity(navigation);

        return screenPages;
    }

    ScreenPagination setMarketNavigation(AgProduct product)
            throws MyCustomException {

        AgNavigation navigation
                = getNavigationByMsisdn(product.getUserContact());

        MarketPriceResponse2 markets = getMarketPrices2Cached(navigation);

        Set<? extends MenuItem> districtMarkets
                = filterMarketsByDistrictAndProduct(markets.getData(), product);

        ScreenPagination screenPages = paginateData(districtMarkets,
                DEFAULT_ITEMS_PER_SCREEN);
        String pages = GeneralUtils.convertToJson(screenPages,
                ScreenPagination.class);

        navigation.setMarketsNav(pages);
        internalDbAccess.saveOrUpdateEntity(navigation);

        return screenPages;
    }

    ScreenPagination setMarketPriceNavigation(AgProduct product)
            throws MyCustomException {

        AgNavigation navigation
                = getNavigationByMsisdn(product.getUserContact());

        MarketPriceResponse2 markets = getMarketPrices2Cached(navigation);

        Set<? extends MenuItem> marketPrices
                = filterMarketPrices(markets.getData(), product);

        ScreenPagination screenPages = paginateData(marketPrices,
                DEFAULT_ITEMS_PER_SCREEN);
        String pages = GeneralUtils.convertToJson(screenPages,
                ScreenPagination.class);

        navigation.setMarketPriceNav(pages);
        internalDbAccess.saveOrUpdateEntity(navigation);

        return screenPages;
    }

    ScreenPagination setSellersNavigation(AgProduct product)
            throws MyCustomException {

        AgNavigation navigation
                = getNavigationByMsisdn(product.getUserContact());

        GetSellersResponse sellers = getSellersCached(navigation);

        Set<? extends MenuItem> sellerList
                = filterSellersList(sellers.getData(), product);

        ScreenPagination screenPages = paginateData(sellerList, 5);
        String pages = GeneralUtils.convertToJson(screenPages,
                ScreenPagination.class);

        navigation.setSellersNav(pages);
        internalDbAccess.saveOrUpdateEntity(navigation);

        return screenPages;
    }

    ScreenPagination setBuyerNavigation(AgProduct product)
            throws MyCustomException {

        AgNavigation navigation
                = getNavigationByMsisdn(product.getUserContact());

        GetBuyerResponse buyers = getBuyersCached(navigation);

        Set<? extends MenuItem> buyerList
                = filterBuyersList(buyers, product);

        logger.debug("Called BuyerList size: " + buyerList.size());

        ScreenPagination screenPages = paginateData(buyerList,
                DEFAULT_ITEMS_PER_SCREEN);
        String pages = GeneralUtils.convertToJson(screenPages,
                ScreenPagination.class);

        navigation.setMatchedBuyersNav(pages);
        internalDbAccess.saveOrUpdateEntity(navigation);

        return screenPages;
    }

    UssdMenuComponents breakIntoPages(String requestInput,
            MenuName responseMenu, Map<String, String> menuNodes,
            String newSessionID, ScreenPagination pages,
            TitleAddition titleAddition)
            throws MyCustomException {

        int pagesSize = pages.getScreenData().size();
        int startIndex = 1;

        if (pagesSize <= 0) {
            return getUssdMenuComponentsHelper(MenuName.NO_RECORDS, menuNodes);
        }

        AgUssdMenu menu = retrieveMenuFromDB(responseMenu);
        String menuTitle = convertMenuNodeToString(menu.getMenuTitleText(),
                menuNodes);
        DynamicMenuItem dynamicMenu
                = getDynamicMenuItems(getScreenDetails(pages, 0), startIndex);
        Set<AgMenuItemIndex> menuItems = dynamicMenu.getMenuItems();
        List<MenuHistory.MenuOption> menuOptions = dynamicMenu.getMenuOptions();

        UssdMenuComponents menuComponents
                = getUssdMenuComponentsHelper(responseMenu, menuNodes,
                        menuOptions, 1, pagesSize, titleAddition, menuTitle,
                        menuItems);

        menuComponents.setNavBar(NavigationBar.PREVIOUS_AND_MAIN);
        if (pagesSize > 1) {
            menuComponents.setNavBar(NavigationBar.NEXT_PREVIOUS_MAIN);
        }
        return menuComponents;
    }

    AgProduct saveNewProduct(String clientMsisdn, String newSessionID,
            ItemTag tag, UssdFunction function)
            throws MyCustomException {

        AgProduct product = new AgProduct();
        product.setBuyerId(0);
        product.setBuyerName("");
        product.setBuyerPrice(0);
        product.setCategoryId(0);
        product.setProductId(0);
        product.setSubCategoryId(0);
        product.setItemDescription("");
        product.setDistrictName("");
        product.setPlace("");
        product.setItemName("");
        product.setPaymentMethod("");
        product.setQuantity("");
        product.setSellerPrice(0);
        product.setTransportArea(TransportArea.UNKNOWN);
        product.setRegion(Region.UNKNOWN);
        product.setUserContact(clientMsisdn);
        product.setIsSubmitted(Boolean.FALSE);
        product.setSessionId(newSessionID);
        product.setTag(tag);
        product.setUssdFunction(function);
        product.setIsMatched(Boolean.FALSE);
        product.setBuyerLocation(BuyerLocation.UNKNOWN);

        internalDbAccess.saveOrUpdateEntity(product);

        return product;
    }

    DynamicMenuItem getDynamicMenuItems(List<? extends MenuItem> items, int startIndex) // List<MenuItem> items
            throws MyCustomException {

        List<MenuHistory.MenuOption> menuOptions = new LinkedList<>();
        MenuHistory menuHistory = new MenuHistory();

        Set<AgMenuItemIndex> menuItems = new HashSet<>();
        AgMenuItemIndex menuItem;
        //int startIndex = 1;
        String menuOptionIds = "";

        for (MenuItem item : items) {

            menuOptionIds += (startIndex + "=" + item.getId() + ",");

            logger.debug("Index: " + startIndex + ", id: " + item.getId() + ", name: " + item.getName());

            //String nodeName = item.getName() + ", " + item.getCostPerKg();
            menuItem = new AgMenuItemIndex(item.getName(), startIndex);
            menuItems.add(menuItem);

            MenuOption menuOption = menuHistory.new MenuOption();
            menuOption.setDataId("" + item.getId());
            menuOption.setDataValue(item.getName());
//            menuOption.setDataValue(item.getName()
//                    + (item.getExtra().equals("") ? "" : "-" + item.getExtra()));
            menuOption.setMenuOptionId(startIndex);
            menuOptions.add(menuOption);

            startIndex++;
        }

        return new DynamicMenuItem(menuItems, menuOptions);
    }

    UssdMenuComponents getUssdMenuComponentsHelper(
            MenuName responseMenu,
            Map<String, String> menuNodes) throws MyCustomException {

        AgUssdMenu menu = retrieveMenuFromDB(responseMenu);
        String menuTitle = convertMenuNodeToString(menu.getMenuTitleText(),
                menuNodes);
        Set<AgMenuItemIndex> menuItems = menu.getMenuItems();

        UssdMenuComponents menuComponents = new UssdMenuComponents();
        menuComponents.setMenuItems(menuItems);
        menuComponents.setResponseMenu(responseMenu);
        menuComponents.setMenuTitle(menuTitle);
        menuComponents.setNavBar(NavigationBar.PREVIOUS_AND_MAIN);

        return menuComponents;
    }

    UssdMenuComponents getUssdMenuComponentsHelper(
            MenuName responseMenu, Map<String, String> menuNodes,
            TitleAddition titleAddition) throws MyCustomException {

        AgUssdMenu menu = retrieveMenuFromDB(responseMenu);
        Set<AgMenuItemIndex> menuItems = menu.getMenuItems();

        String menuTitle = convertMenuNodeToString(menu.getMenuTitleText(), menuNodes);
        if (titleAddition.isIsPrefix()) {
            menuTitle = titleAddition.getAddition() + "\n"
                    + menuTitle;
        } else {
            menuTitle = menuTitle + "\n" + titleAddition.getAddition();
        }

        UssdMenuComponents menuComponents = new UssdMenuComponents();
        menuComponents.setMenuItems(menuItems);
        menuComponents.setResponseMenu(responseMenu);
        menuComponents.setMenuTitle(menuTitle);
        menuComponents.setNavBar(NavigationBar.NONE);

        return menuComponents;
    }

    UssdMenuComponents getUssdMenuComponentsHelper(
            MenuName responseMenu, Map<String, String> menuNodes,
            List<MenuHistory.MenuOption> menuOptions, int screenNum,
            int totalScreens, TitleAddition titleAddition, String menuTitle,
            Set<AgMenuItemIndex> menuItems) throws MyCustomException {

        String newTitle;
        if (titleAddition == null || titleAddition.getAddition().isEmpty()) {

            newTitle = menuTitle;

        } else if (titleAddition.isIsPrefix()) {
            newTitle = titleAddition.getAddition() + "\n" + menuTitle;

        } else {
            newTitle = menuTitle + "\n" + titleAddition.getAddition();
        }

        UssdMenuComponents menuComponents = new UssdMenuComponents();
        menuComponents.setMenuItems(menuItems);
        menuComponents.setMenuOptions(menuOptions);
        menuComponents.setResponseMenu(responseMenu);
        menuComponents.setMenuTitle(newTitle);
        menuComponents.setScreenNum(screenNum);
        menuComponents.setTotalScreens(totalScreens);
        menuComponents.setNavBar(NavigationBar.MAIN);

        return menuComponents;
    }

    UssdMenuComponents processPaymentMethod(String requestInput,
            String clientMsisdn, Map<String, String> menuNodes,
            String newSessionID) throws MyCustomException {

        MenuName responseMenu = MenuName.TRANSPORT;
        String payMethod;
        switch (requestInput) {

            case "1":
                payMethod = "CASH";
                break;

            case "2":
                payMethod = "MOMO";
                break;

            default:
                payMethod = "CASH";
                break;
        }

        AgProduct productSale = fetchProduct(clientMsisdn);
        productSale.setPaymentMethod(payMethod);
        internalDbAccess.saveOrUpdateEntity(productSale);

        UssdMenuComponents menuComponents
                = getUssdMenuComponentsHelper(responseMenu, menuNodes);
        menuComponents.setNavBar(NavigationBar.PREVIOUS_AND_MAIN);

        return menuComponents;
    }

    UssdMenuComponents processTransportArea(String requestInput,
            String clientMsisdn, Map<String, String> menuNodes,
            String newSessionID) throws MyCustomException {

        MenuName responseMenu = MenuName.CONFIRM;
        TransportArea transport;
        switch (requestInput) {

            case "1":
                transport = TransportArea.NONE;
                break;

            case "2":
                transport = TransportArea.NATIONAL;
                break;

            case "3":
                transport = TransportArea.INTERNATIONAL;
                break;

            default:
                transport = TransportArea.UNKNOWN;
                break;
        }

        AgProduct productSale = fetchProduct(clientMsisdn);
        productSale.setTransportArea(transport);
        internalDbAccess.saveOrUpdateEntity(productSale);

        String titleAdd = "Item : " + productSale.getItemName() + "\n"
                + "Qnty : " + productSale.getQuantity() + "Kgs" + "\n"
                + "Price: " + productSale.getSellerPrice() + "/Kg" + "\n"
                + "place: " + productSale.getPlace() + "\n"
                + "District: " + productSale.getDistrictName() + "\n"
                + "Region: " + productSale.getRegion() + "\n";

        UssdMenuComponents menuComponents
                = getUssdMenuComponentsHelper(responseMenu, menuNodes,
                        new TitleAddition(false, titleAdd));
        menuComponents.setNavBar(NavigationBar.PREVIOUS_AND_MAIN);
        return menuComponents;
    }

    UssdMenuComponents contactSeller(String requestInput,
            String msisdn, Map<String, String> menuNodes,
            String newSessionID) throws MyCustomException {

        MenuName responseMenu = MenuName.CONFIRM;

        AgNavigation navigation = getNavigationByMsisdn(msisdn);

        MenuHistory menuHistory
                = getMenuHistoryHelper(navigation.getMenuHistory());
        MenuHistory.Data menuData = getCurrentMenuData(menuHistory);
        menuData.getMenuName();

        DataItem data = getDataValue(menuData, requestInput);
        String itemName = data.getDataValue();
        String dataId = data.getDataId();

        AgProduct product = fetchProduct(msisdn);
        product.setSellerId(Integer.valueOf(dataId));
        product.setSellerName(itemName);

        GetSellersResponse sellers = getSellersCached(navigation);
        Seller seller = getSeller(sellers.getData(), product);

//        product.setSellerPrice(seller.getPrice().getAmount());
//        product.setMeasureUnit(seller.getPrice().getMeasureUnit());
//        internalDbAccess.saveOrUpdateEntity(product);
        String split[] = itemName.split("-");

        String name;
        String price;
        String item;
        if (split == null || split.length < 3) {
            name = itemName;
            price = "";
            item = "";
        } else {
            name = split[0].trim();
            item = split[1].trim();
            price = (split[2].trim());
        }

        String titleAdd = "Contact" + "\n\n"
                + "Name : " + name + "\n" //get name out
                + "Item : " + item + "\n"
                + "Price: " + price + "\n";
        //+ "District: " + product.getDistrictName() + "\n";

        UssdMenuComponents menuComponents
                = getUssdMenuComponentsHelper(responseMenu, menuNodes,
                        new TitleAddition(true, titleAdd));
        menuComponents.setNavBar(NavigationBar.PREVIOUS_AND_MAIN);

        return menuComponents;
    }

    UssdMenuComponents contactBuyer(String requestInput,
            String msisdn, Map<String, String> menuNodes,
            String newSessionID) throws MyCustomException {

        MenuName responseMenu = MenuName.CONFIRM;

        AgNavigation navigation = getNavigationByMsisdn(msisdn);

        MenuHistory menuHistory
                = getMenuHistoryHelper(navigation.getMenuHistory());
        MenuHistory.Data menuData = getCurrentMenuData(menuHistory);
        menuData.getMenuName();

        DataItem data = getDataValue(menuData, requestInput);
        String itemName = data.getDataValue();
        String dataId = data.getDataId();

        AgProduct product = fetchProduct(msisdn);
        product.setBuyerId(Integer.valueOf(dataId));
        product.setBuyerName(itemName);

        GetBuyerResponse buyers = getBuyersCached(navigation);
        GetBuyerResponse.Data seller = getBuyer(buyers, product);

        product.setBuyerPrice(0); //TO-DO: Work on these
        product.setMeasureUnit("");
        internalDbAccess.saveOrUpdateEntity(product);

        String split[] = itemName.split("-");
        String name;
        String price;

        if (split == null || split.length < 2) {
            name = itemName;
            price = "";
        } else {
            name = split[0].trim();
            price = (split[1].trim());
        }

        String titleAdd = "Contact Buyer" + "\n\n" //Change this to be dynamic..
                + "Name : " + name + "\n" //get name out
                + "Item : " + product.getItemName() + "\n"
                + "Price: " + price + "\n"
                + "District: " + product.getDistrictName() + "\n";

        UssdMenuComponents menuComponents
                = getUssdMenuComponentsHelper(responseMenu, menuNodes,
                        new TitleAddition(true, titleAdd));
        menuComponents.setNavBar(NavigationBar.PREVIOUS_AND_MAIN);

        return menuComponents;
    }

    UssdMenuComponents processRegion(String requestInput, String msisdn,
            Map<String, String> menuNodes, String newSessionID)
            throws MyCustomException {

        Region region;
        switch (requestInput) {

            case "1":
                region = Region.CENTRAL;
                break;

            case "2":
                region = Region.EASTERN;
                break;

            case "3":
                region = Region.NORTHERN;
                break;

            case "4":
                region = Region.WESTERN;
                break;

            case "5":
                region = Region.INTERNATIONAL;
                break;

            case "6":
                region = Region.ALL;
                break;

            default:
                region = Region.UNKNOWN;
                break;
        }

        MenuName responseMenu = null;
        UssdMenuComponents menuComponents = null;

        AgProduct product = fetchProduct(msisdn);
        UssdFunction function = product.getUssdFunction();

        switch (function) {

            case SELL:
                responseMenu = MenuName.CUSTOM_DISTRICT;
                break;

            case MARKET_PRICES:
                menuComponents = displayMarketDistricts(requestInput, msisdn,
                        menuNodes, newSessionID, region);
                break;

            case SELLERS_TRADERS:
                menuComponents = displaySellerDistricts(requestInput, msisdn,
                        menuNodes, newSessionID, region);
                break;

            case FIND_BUYERS:
                menuComponents = displaySellerDistricts(requestInput, msisdn,
                        menuNodes, newSessionID, region);
                break;

            case INPUT_TOOLS:
                menuComponents = displaySellerDistricts(requestInput, msisdn,
                        menuNodes, newSessionID, region);
                break;

            default:
                responseMenu = MenuName.MARKET_DISTRICTS;
                break;
        }

        if (menuComponents == null) {
            menuComponents = getUssdMenuComponentsHelper(responseMenu, menuNodes);
            menuComponents.setNavBar(NavigationBar.PREVIOUS_AND_MAIN);
        }

        return menuComponents;
    }

    UssdMenuComponents processBuyerLocation(String requestInput,
            String clientMsisdn, Map<String, String> menuNodes,
            String newSessionID) throws MyCustomException {

        MenuName responseMenu = MenuName.MATCHED_BUYER_LIST;
        BuyerLocation location;
        switch (requestInput) {

            case "1":
                location = BuyerLocation.NEARBY;
                break;

            case "2":
                location = BuyerLocation.NATIONAL;
                break;

            case "3":
                location = BuyerLocation.INTERNATIONAL;
                break;

            default:
                location = BuyerLocation.UNKNOWN;
                break;
        }

        AgProduct product = fetchProduct(clientMsisdn);
        product.setBuyerLocation(location);
        internalDbAccess.saveOrUpdateEntity(product);

        ScreenPagination screenPages = setBuyerNavigation(product);

        UssdMenuComponents menuComponents
                = breakIntoPages(requestInput, responseMenu,
                        menuNodes, newSessionID, screenPages,
                        new TitleAddition(true, product.getItemName()));

        return menuComponents;
    }

    UssdMenuComponents confirm(String requestInput, String clientMsisdn,
            Map<String, String> menuNodes, String newSessionID)
            throws MyCustomException {

        AgProduct product = fetchProduct(clientMsisdn);

        MenuName responseMenu;
        boolean isUpload = Boolean.FALSE;

        switch (requestInput) {

            case "1":

                if (product.getUssdFunction()
                        == UssdFunction.SELLERS_TRADERS
                        || product.getUssdFunction()
                        == UssdFunction.INPUT_TOOLS) {

                    responseMenu = MenuName.CONTACT_SELLER_MESSAGE;
                    ContactResponse response = contactRequest(product);

                } else if (product.getUssdFunction()
                        == UssdFunction.MATCHED_BUYERS) {

                    responseMenu = MenuName.CONTACT_SELLER_MESSAGE;
                    ContactResponse response = contactRequest(product);

                } else {
                    responseMenu = MenuName.UPLOAD_MSG;
                    isUpload = uploadItemForSale(product, false);
                }
                break;

            case "2":
                isUpload = Boolean.FALSE;
                responseMenu = MenuName.MAIN_MENU;
                break;

            default:
                isUpload = Boolean.FALSE;
                responseMenu = MenuName.MAIN_MENU;
                break;
        }

        product.setIsSubmitted(isUpload);
        internalDbAccess.saveOrUpdateEntity(product);

        UssdMenuComponents menuComponents
                = getUssdMenuComponentsHelper(responseMenu, menuNodes);
        menuComponents.setNavBar(NavigationBar.NONE);
        menuComponents.setIsIsEnd(true);

        return menuComponents;
    }

    DataItem getDataValue(MenuHistory.Data menuData, String requestInput) {

        String dataValue = "";
        String dataId = "";
        int menuOptionId = 0;

        for (MenuOption option : menuData.getMenuOptions()) {

            if ((option.getMenuOptionId() + "").equals(requestInput)) {

                dataId = option.getDataId();
                dataValue = option.getDataValue();
                menuOptionId = option.getMenuOptionId();
            }
        }
        return new DataItem(dataId, dataValue, menuOptionId);
    }

    Set<? extends MenuItem> getDistrictMarkets(
            Set<District> districtMarketInfo, String districtId) {

        for (District district : districtMarketInfo) {

            if (districtId.equals(String.valueOf(district.getId()))) {
                return district.getProducts();
            }
        }
        return new HashSet<>();
    }

    Set<? extends MenuItem> getProductMarkets(
            Set<Product> productMarketInfo, String productId) {

        for (Product product : productMarketInfo) {

            if (productId.equals(String.valueOf(product.getId()))) {
                return product.getMarkets();
            }
        }
        return new HashSet<>();
    }

    Set<? extends MenuItem> getChildrenMenuItems(
            Set<? extends HasChildrenItems> menuItemList, int dataId) {

        for (HasChildrenItems cat : menuItemList) {

            if (dataId == cat.getId()) {
                return cat.getChildrenItems();
            }
        }
        return new HashSet<>();
    }

//    UssdMenuComponents processBuyerCategories(String requestInput,
//            String clientMsisdn, Map<String, String> menuNodes,
//            String newSessionID) throws MyCustomException {
//
//        MenuName responseMenu;
//        String menuTitle;
//        Set<AgMenuItemIndex> menuItems;
//
//        AgNavigation navigation = getNavigationByMsisdn(clientMsisdn);
//
//        String category = getIdFromChosenInput(requestInput,
//                navigation.getCurrentMenuOptionIds());
//
//        AgProduct product = new AgProduct();
//        product.setBuyerId("");
//        product.setBuyerName("");
//        product.setBuyerPrice("");
//        product.setItemCategory("");
//        product.setItemDescription("");
//        product.setItemLocation("");
//        product.setItemName("");
//        product.setPaymentMethod("");
//        product.setQuantity("");
//        product.setSellerPrice("");
//        product.setTransportArea("");
//        product.setSellerContact(clientMsisdn);
//        product.setIsSubmitted(Boolean.FALSE);
//        product.setSessionId(newSessionID);
//
//        if (category.equalsIgnoreCase("CUSTOM")) {
//
//            responseMenu = MenuName.ENTERED_PRODUCE;
//            AgUssdMenu menu = retrieveMenuFromDB(responseMenu);
//            menuTitle = convertMenuNodeToString(menu.getMenuTitleText(),
//                    menuNodes);
//            menuItems = menu.getMenuItems();
//
//        } else {
//
//            responseMenu = MenuName.BUYER_LIST;
//            menuTitle = category + " (Shs/Kg)";
//            menuItems = getBuyerList(category, clientMsisdn);
//
//            product.setItemCategory(category);
//        }
//
//        internalDbAccess.saveOrUpdateEntity(product);
//
//        UssdMenuComponents menuComponents = new UssdMenuComponents();
//        menuComponents.setMenuItems(menuItems);
//        menuComponents.setResponseMenu(responseMenu);
//        menuComponents.setMenuTitle(menuTitle);
//        menuComponents.setNavBar(NavigationBar.PREVIOUS_AND_MAIN);
//
//        return menuComponents;
//    }
//    UssdMenuComponents processBuyerList(String requestInput,
//            String clientMsisdn, Map<String, String> menuNodes,
//            String sessionId) throws MyCustomException {
//
//        AgNavigation navigation = getNavigationByMsisdn(clientMsisdn);
//
//        String buyerId = getIdFromChosenInput(requestInput,
//                navigation.getCurrentMenuOptionIds());
//
//        AgProduct product = fetchProduct(sessionId,
//                clientMsisdn);
//        product.setBuyerId(buyerId);
//        internalDbAccess.saveOrUpdateEntity(product);
//
//        MenuName responseMenu = MenuName.CONTACT_BUYER;
//        AgUssdMenu menu = retrieveMenuFromDB(responseMenu);
//
//        String menuTitle = convertMenuNodeToString(menu.getMenuTitleText(),
//                menuNodes);
//        Set<AgMenuItemIndex> menuItems = menu.getMenuItems();
//
//        Map<String, String> map = new HashMap<>();
//        map.put("buyer", "buyer"); //change this to buyer name when done retrieving name
//        String title = MapFormat.format(menuTitle, map);
//
//        UssdMenuComponents menuComponents = new UssdMenuComponents();
//        menuComponents.setMenuItems(menuItems);
//        menuComponents.setResponseMenu(responseMenu);
//        menuComponents.setMenuTitle(title);
//        menuComponents.setNavBar(NavigationBar.PREVIOUS_AND_MAIN);
//
//        return menuComponents;
//    }
//    UssdMenuComponents processMatchedBuyerList(String requestInput,
//            String clientMsisdn, Map<String, String> menuNodes,
//            String sessionId) throws MyCustomException {
//
//        AgNavigation navigation = getNavigationByMsisdn(clientMsisdn);
//
//        String buyerId = getIdFromChosenInput(requestInput,
//                navigation.getCurrentMenuOptionIds());
//
//        MenuName responseMenu = MenuName.BUYER_LOCATION;
//        AgUssdMenu menu = retrieveMenuFromDB(responseMenu);
//
//        String menuTitle = convertMenuNodeToString(menu.getMenuTitleText(),
//                menuNodes);
//        Set<AgMenuItemIndex> menuItems = menu.getMenuItems();
//
//        UssdMenuComponents menuComponents = new UssdMenuComponents();
//        menuComponents.setMenuItems(menuItems);
//        menuComponents.setResponseMenu(responseMenu);
//        menuComponents.setMenuTitle(menuTitle);
//        menuComponents.setNavBar(NavigationBar.PREVIOUS_AND_MAIN);
//
//        return menuComponents;
//    }
    AgLanguage updateUserLanguage(String requestInput, String msisdn)
            throws MyCustomException {

        String langId;
        switch (requestInput) {

            case "1":
                langId = "1001";
                break;

            case "2":
                langId = "1002";
                break;

            case "3":
                langId = "1003";
                break;

            default:
                langId = "1001";
                break;
        }

        AgNavigation navigation = getNavigationByMsisdn(msisdn);
        AgLanguage language = internalDbAccess.fetchEntity(AgLanguage.class,
                "langId", langId);
        AgClient client = navigation.getClient();
        client.setLanguage(language);

        internalDbAccess.updateEntity(client);

        return language;
    }

    /**
     *
     *
     * @param navigation
     * @param requestInput
     * @param msisdn
     * @return
     * @throws MyCustomException
     */
    UssdMenuComponents setLanguage(String requestInput, String msisdn)
            throws MyCustomException {

        AgLanguage language = updateUserLanguage(requestInput, msisdn);

        MenuName responseMenu = MenuName.MAIN_MENU;

        AgUssdMenu menu = retrieveMenuFromDB(responseMenu);

        Map<String, String> menuNodes = getLocalisedMenuNodes(language);
        String menuTitle = convertMenuNodeToString(menu.getMenuTitleText(),
                menuNodes);

        UssdMenuComponents menuComponents = new UssdMenuComponents();
        menuComponents.setNavBar(NavigationBar.NONE);
        menuComponents.setMenuItems(menu.getMenuItems());
        menuComponents.setMenuTitle(menuTitle);
        menuComponents.setResponseMenu(responseMenu);

        return menuComponents;
    }

    /**
     *
     * @param languageId
     * @param navig
     * @return
     * @throws MyCustomException
     */
    private String createResponseString(Set<AgMenuItemIndex> menuItems,
            String menuTitle, Map<String, String> menuNodes,
            NavigationBar navBar, boolean isLocalised)
            throws MyCustomException {

        String responseString = "";

        //header
        responseString += menuTitle + "\n";

        //body
        String menuBody = convertItemSetToString(menuItems, menuNodes,
                isLocalised);
        responseString += menuBody;

        logger.debug("Menu Body: " + menuBody);

        //footer
        String nextCode = "99";
        String previousCode = "0 ";
        String homeCode = "00";
        String next = menuNodes.get(nextCode);
        String previous = menuNodes.get(previousCode.trim());
        String homeMenu = menuNodes.get(homeCode);

        switch (navBar) {

            case NEXT:
                responseString += ("\n" + nextCode + ": " + next);
                break;

            case NEXT_PREVIOUS_MAIN:
                responseString += ("\n" + nextCode + ": " + next);
                responseString += ("\n" + previousCode + ": " + previous);
                responseString += ("\n" + homeCode + ": " + homeMenu);
                break;

            case NEXT_AND_MAIN:
                responseString += ("\n" + nextCode + ": " + next);
                responseString += ("\n" + homeCode + ": " + homeMenu);
                break;

            case PREVIOUS_AND_MAIN:
                responseString += ("\n" + previousCode + ": " + previous);
                responseString += ("\n" + homeCode + ": " + homeMenu);
                break;

            case MAIN:
                responseString += ("\n" + homeCode + ": " + homeMenu);
                break;

            case NONE:
                break;

            default:
                responseString += ("\n" + previousCode + ": " + previous);
                responseString += ("\n" + homeCode + ": " + homeMenu);
                break;
        }

        return responseString;
    }

    AgNavigation getNavigationByMsisdn(String msisdn) throws MyCustomException {

        Map<String, Object> resourceProps = new HashMap<>();
        resourceProps.put("msisdn", new HashSet<>(Arrays.asList(msisdn)));
        Set<AgNavigation> navigations = internalDbAccess
                .fetchEntities(AgNavigation.FETCH_NAVIG_BY_MSISDN, resourceProps);

        if (navigations == null || navigations.isEmpty()) {
            return null;
        }
        return (AgNavigation) navigations.toArray()[0];

    }

    AgNavigation getNavigationBySession(String sessionId)
            throws MyCustomException {

        Map<String, Object> resourceProps = new HashMap<>();
        resourceProps.put("sessionId", new HashSet<>(Arrays.asList(sessionId)));
        Set<AgNavigation> navigations = internalDbAccess
                .fetchEntities(AgNavigation.FETCH_NAVIG_BY_SESSIONID, resourceProps);

        if (navigations == null || navigations.isEmpty()) {
            return null;
        }
        return (AgNavigation) navigations.toArray()[0];
        //return navigations.toArray(new AgNavigation[navigations.size()])[0];

    }

    /**
     * Set the currentMenu start point - either at language or home menuData or
     * session-expired menuData
     *
     * @param msisdn
     * @return
     * @throws MyCustomException
     */
    private NextNavigation setStartUssdMenu(String msisdn, String sessionId)
            throws MyCustomException {

        MenuName responseMenu;
        String languageCode;
        MenuHistory menuHistory = null;
        NavigationBar navBar = NavigationBar.MAIN;
        AgNavigation navigation = getNavigationByMsisdn(msisdn);

        if (navigation == null) {

            languageCode = NamedConstants.DEFAULT_USSD_LANGUAGE_CODE;
            responseMenu = MenuName.LANGUAGE_MENU;
            navigation = addNavigatingClient(msisdn, sessionId);

        } else {

            AgClient client = navigation.getClient();
            menuHistory = getMenuHistoryHelper(navigation.getMenuHistory());
            MenuHistory.Data menuData = getCurrentMenuData(menuHistory);

            boolean sessionExpired = !menuData.isIsEnd();
            languageCode = client.getLanguage().getLangCode();
            MenuName currentMenu = getMenuName(menuData); //If on Main/Lang menu stay
            //
            //Conditions
            //1. if previous session expired - on another menuData 
            //   Been on other menuData not on End/Main/Language-menuData >12hrs - goto main
            //2. Reached End - show main
            //3. There is no menuHistory i.e. {} - goto main
            //4. on Main or Language - stay there
            //1. Language notset - goto Language
            //

            //TO-DO
            //Should we clear History Data for every new/Continue Dial ???
            //la-kso navigation.setMenuHistory("{}");
            if (languageCode.equalsIgnoreCase("notset")
                    || currentMenu == MenuName.LANGUAGE_MENU) {

                responseMenu = MenuName.LANGUAGE_MENU;
                languageCode = NamedConstants.DEFAULT_USSD_LANGUAGE_CODE;
                navBar = NavigationBar.NONE;
                navigation.setMenuHistory("{}");

            } else if (currentMenu == MenuName.MAIN_MENU) {
                responseMenu = MenuName.MAIN_MENU;
                navigation.setMenuHistory("{}");

            } else if (sessionExpired) {

                responseMenu = checkIfContinueSession(msisdn, sessionId);

                //To-DO I think if Session Has expired, we just need to display
                //to the userContact the current session menu if less than 12hrs/valid
                //as oppossed to going to the DB and retrieving a menu 
                //line of code below
                //AgUssdMenu menu = retrieveMenuFromDB(responseMenu);
                //See how to treat users who are on menus that are broken down
                // in parts due to there being long e.g. If userContact session expired
                //on the 3rd / 6 buyer list menus - how do we treat this?
            } else {
                responseMenu = MenuName.MAIN_MENU;
                navigation.setMenuHistory("{}");
            }
        }

        AgUssdMenu menu = retrieveMenuFromDB(responseMenu);

        Set<AgMenuItemIndex> newMenuItems = menu.getMenuItems();
        String menuTitle = menu.getMenuTitleText();
        Map<String, String> menuNodes = getLocalisedMenuNodes(languageCode);

        String menuTitleString = convertMenuNodeToString(menuTitle, menuNodes);
        String responseString = createResponseString(
                newMenuItems, menuTitleString, menuNodes, navBar, Boolean.FALSE);

        boolean isMenuEnd = Boolean.FALSE; //might want to use MenuName.END or dynamically get this value from up there

        List<MenuHistory.MenuOption> menuOptions = new LinkedList<>(); //need to get these 1=23,2=44 /menu option id = db id pair
        String updatedHistory = addCurrentMenuToHistory(menuHistory,
                responseMenu.getValue(), responseString,
                isMenuEnd, menuOptions, 1, 1);

        navigation.setMenuHistory(updatedHistory);
        internalDbAccess.saveOrUpdateEntity(navigation);

        return new NextNavigation(navigation, responseString, isMenuEnd);

    }

    MenuHistory getMenuHistoryHelper(String history) throws MyCustomException {

        MenuHistory menuHistory
                = GeneralUtils.convertFromJson(history, MenuHistory.class);
        GeneralUtils.toPrettyJson(history);

        return menuHistory;
    }

    String addCurrentMenuToHistory(
            MenuHistory menuHistory, String responseMenu, String newMenuString,
            boolean isEnd, List<MenuHistory.MenuOption> menuOptions,
            int screenNum, int totalScreens)
            throws MyCustomException {

        if (menuHistory == null) {
            menuHistory = new MenuHistory();
        }

        MenuHistory.Data historyItem = menuHistory.new Data();
        historyItem.setIsEnd(isEnd);
        historyItem.setMenuName(responseMenu);
        historyItem.setMenuString(newMenuString);
        historyItem.setMenuOptions(menuOptions);
        historyItem.setScreenNum(screenNum);
        historyItem.setTotalScreens(totalScreens);
        menuHistory.getMenuHistoryData().add(historyItem);

        return menuHistory.toString();
    }

    MenuHistory popMostRecentMenuFromHistory(MenuHistory menuHistory) {

        List<MenuHistory.Data> historyData = menuHistory.getMenuHistoryData();
        int dataSize = historyData.size();
        historyData.remove(dataSize - 1); //pop current menuData
        menuHistory.setMenuHistoryData(historyData);

        return menuHistory;
    }

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
    MenuHistory.Data getCurrentMenuData(MenuHistory menuHistory)
            throws MyCustomException {

        List<MenuHistory.Data> historyData = menuHistory.getMenuHistoryData();
        int dataSize = historyData.size();

        MenuHistory.Data currentMenu = historyData.get(dataSize - 1);

        return currentMenu;

    }

    MenuHistory.Data getSecondLastMenuData(MenuHistory menuHistory)
            throws MyCustomException {

        List<MenuHistory.Data> historyData = menuHistory.getMenuHistoryData();
        int dataSize = historyData.size();

        MenuHistory.Data currentMenu = historyData.get(dataSize - 2);

        return currentMenu;

    }

    MenuName getMenuName(MenuHistory.Data menuData) throws MyCustomException {

        return MenuName.convertToEnum(menuData.getMenuName());
    }

    MenuName getMenuName(String menuNameString) throws MyCustomException {

        return MenuName.convertToEnum(menuNameString);
    }

    String convertMenuNodeToString(String menuTitle,
            Map<String, String> menuNodes) {

        String localisedMenuTitle = "";

        if (!(menuTitle == null || menuTitle.trim().isEmpty())) {

            localisedMenuTitle = menuNodes.get(menuTitle);

        }
        return localisedMenuTitle;
    }

    String convertItemSetToString(
            Set<AgMenuItemIndex> menuItems,
            Map<String, String> menuNodes, boolean isLocalised) {

        List<AgMenuItemIndex> menuItemList
                = GeneralUtils.convertSetToList(menuItems);
        Collections.sort(menuItemList, new ItemNodeComparator());

        String responseBodyItems = "";

        logger.debug("isLocalised: " + isLocalised);

        for (AgMenuItemIndex node : menuItemList) {

            String menuItem = node.getMenuItemCode();
            if (!isLocalised) {
                menuItem = menuNodes.get(node.getMenuItemCode());
            }
            responseBodyItems += (node.getMenuItemIndex() + ": " + menuItem + "\n");
        }
        return responseBodyItems;
    }

    AgUssdMenu retrieveMenuFromDB(MenuName menuName) throws MyCustomException {

        Map<String, Object> resourceProps = new HashMap<>();
        resourceProps.put("menuName", new HashSet<>(Arrays.asList(menuName)));

        Set<AgUssdMenu> menus = internalDbAccess.fetchEntities(
                AgUssdMenu.FETCH_BY_MENU_NAME, resourceProps);
        AgUssdMenu menu = (AgUssdMenu) menus.toArray()[0];

        return menu;
    }

    Map<String, String> getLocalisedMenuNodes(AgLanguage language) {

        String langCode = language.getLangCode();

        if (langCode.equalsIgnoreCase("notset")) {
            langCode = NamedConstants.DEFAULT_USSD_LANGUAGE_CODE;
        }

        Map<String, String> menuNodes = this.ussdMenuConfig.getUssdMenu(langCode);

        return menuNodes;
    }

    Map<String, String> getLocalisedMenuNodes(String langCode) {

        Map<String, String> menuNodes
                = this.ussdMenuConfig.getUssdMenu(langCode);

        return menuNodes;
    }

    NextNavigation navigateToPrevious(AgNavigation navigation)
            throws MyCustomException {

        String history = navigation.getMenuHistory();
        MenuHistory menuHistory = getMenuHistoryHelper(history);

        menuHistory = popMostRecentMenuFromHistory(menuHistory);
        navigation.setMenuHistory(menuHistory.toString());
        internalDbAccess.saveOrUpdateEntity(navigation);

        MenuHistory.Data currentMenu = getCurrentMenuData(menuHistory);
        return new NextNavigation(navigation,
                currentMenu.getMenuString(), currentMenu.isIsEnd());
    }

    NextNavigation navigateTo(
            AgNavigation navigation,
            Map<String, String> menuNodes,
            List<MenuHistory.MenuOption> menuOptions,
            AgUssdMenu ussdMenu,
            NavigationBar navBar,
            boolean isLocalised,
            boolean isEnd,
            int screenNum,
            int totalScreens)
            throws MyCustomException {

        MenuName responseMenu = ussdMenu.getMenuName();

        String responseString = createResponseString(ussdMenu.getMenuItems(),
                ussdMenu.getMenuTitleText(), menuNodes, navBar, isLocalised);

        MenuHistory menuHistory
                = getMenuHistoryHelper(navigation.getMenuHistory());

        String history = addCurrentMenuToHistory(
                menuHistory, responseMenu.getValue(),
                responseString, isEnd, menuOptions, screenNum, totalScreens);
        navigation.setMenuHistory(history);

        internalDbAccess.saveOrUpdateEntity(navigation);

        return new NextNavigation(navigation, responseString, isEnd);
    }

    NextNavigation processRequestInput(
            AgNavigation navigation, String requestInput,
            Map<String, String> menuNodes) throws MyCustomException {

        UssdMenuComponents ussdMenuComponents;
        MenuHistory menuHistory
                = getMenuHistoryHelper(navigation.getMenuHistory());
        MenuHistory.Data menuData = getCurrentMenuData(menuHistory);

        String sessionID = navigation.getSessionId();
        AgClient client = navigation.getClient();
        String msisdn = client.getMsisdn();
        boolean isRegistered = client.isIsRegistered();

        MenuName oldCurrentMenu = getMenuName(menuData);
        AgUssdMenu menu = retrieveMenuFromDB(oldCurrentMenu);
        MenuType menuType = menu.getMenuType();

        boolean isLocalised = Boolean.FALSE;

        logger.debug("MENUTYPE: " + menuType);
        logger.debug("MENUNAME: " + menu.getMenuName());

        switch (menuType) { //menu type of Old menuData

            case SELECT_DYNAMIC_MENU:
                ussdMenuComponents = processDynamicMenu(requestInput,
                        oldCurrentMenu, msisdn, menuNodes, sessionID,
                        isRegistered);
                break;

            case SELECT_STATIC_MENU:
                ussdMenuComponents = processStaticMenu(requestInput, menu,
                        msisdn, menuNodes, sessionID, isRegistered);
                break;

            case INPUT_MENU: //Menus with userContact input values
                ussdMenuComponents = processInputMenu(oldCurrentMenu,
                        requestInput, menuNodes, sessionID, client);
                break;

            default:
                ussdMenuComponents = processDefaultMenu(oldCurrentMenu,
                        requestInput, msisdn, menuNodes, sessionID, isRegistered);
                break;
        }

        Set<AgMenuItemIndex> menuItems = ussdMenuComponents.getMenuItems();
        List<MenuHistory.MenuOption> menuOptions
                = ussdMenuComponents.getMenuOptions();
        String menuTitle = ussdMenuComponents.getMenuTitle();
        NavigationBar navigBar = ussdMenuComponents.getNavBar();
        MenuName responseMenu = ussdMenuComponents.getResponseMenu();

        AgUssdMenu nextMenu = retrieveMenuFromDB(responseMenu);

        switch (nextMenu.getMenuType()) { //Type of response menuData (next menuData)

            case SELECT_DYNAMIC_MENU:
                isLocalised = Boolean.TRUE;
                break;

            case SELECT_STATIC_MENU:
                isLocalised = Boolean.FALSE;
                break;

            case INPUT_MENU:
                break;

            default:
                break;
        }
        boolean isEnd = ussdMenuComponents.isIsIsEnd();

        String responseString = createResponseString(menuItems, menuTitle,
                menuNodes, navigBar, isLocalised);
        String updatedHistory = addCurrentMenuToHistory(menuHistory,
                responseMenu.getValue(), responseString, isEnd, menuOptions,
                ussdMenuComponents.getScreenNum(),
                ussdMenuComponents.getTotalScreens());

        navigation = getNavigationByMsisdn(msisdn);

        navigation.setMenuHistory(updatedHistory);

        internalDbAccess.saveOrUpdateEntity(navigation);
        return new NextNavigation(navigation, responseString, isEnd);
    }

    NextNavigation showNextContent(AgNavigation navigation,
            Map<String, String> menuNodes)
            throws MyCustomException {

        MenuHistory menuHistory
                = getMenuHistoryHelper(navigation.getMenuHistory());
        MenuHistory.Data menuData = getCurrentMenuData(menuHistory);
        MenuName menu = getMenuName(menuData);

        int itemsPerScreen = DEFAULT_ITEMS_PER_SCREEN;
        String pageData = "";
        AgUssdMenu ussdMenu = retrieveMenuFromDB(menu);
        switch (menu) {

            case ITEM_CATEGORIES:
                pageData = navigation.getCategoryNav();
                break;

            case ITEM_SUBCATEGORIES:
                pageData = navigation.getSubCategoryNav();
                break;

            case MARKET_DISTRICTS:
                pageData = navigation.getDistrictMarketsNav();
                itemsPerScreen = SELLER_ITEMS_PER_SCREEN;
                break;

            case SELLER_DISTRICTS:
                pageData = navigation.getDistrictSellersNav();
                break;

            case MARKETS:
                pageData = navigation.getMarketsNav();
                break;

            case MARKET_PRICES:
                pageData = navigation.getMarketPriceNav();
                break;

            case SELLER_LIST:
                pageData = navigation.getSellersNav();
                itemsPerScreen = SELLER_ITEMS_PER_SCREEN;
                break;

            case MATCHED_BUYER_LIST:
                pageData = navigation.getMatchedBuyersNav();
                break;

            case MATCHED_PRODUCTS:
                pageData = navigation.getMatchedProductsNav();
                break;

            default:
                break;
        }
        NextNavigation nextNavigation = getNextContentHelper(ussdMenu,
                menuData, pageData, navigation, menuNodes, itemsPerScreen);

        return nextNavigation;
    }

    NextNavigation getNextContentHelper(AgUssdMenu ussdMenu,
            MenuHistory.Data menuData, String pageData,
            AgNavigation navigation, Map<String, String> menuNodes,
            int itemsPerScreen) throws MyCustomException {

        ScreenPagination screenPages
                = GeneralUtils.convertFromJson(pageData,
                        ScreenPagination.class);

        int pages = screenPages.getScreenData().size();

        int screenNum = menuData.getScreenNum();
        int totalScrns = menuData.getTotalScreens();

        logger.debug("Pages size: " + pages);
        logger.debug("Screen num: " + screenNum);
        logger.debug("total scrn: " + totalScrns);

        int nextScrn = screenNum + 1;
        if (nextScrn > pages) {
            nextScrn = screenNum;
        }
        int startIndex = 1 + (nextScrn * itemsPerScreen) - itemsPerScreen;
        logger.debug("startIndex: " + startIndex);

        NavigationBar navBar = NavigationBar.NEXT_PREVIOUS_MAIN;
        if (nextScrn >= pages) {
            navBar = NavigationBar.PREVIOUS_AND_MAIN;
        }

        DynamicMenuItem dynamicMenu
                = getDynamicMenuItems(getScreenDetails(screenPages, nextScrn - 1),
                        startIndex);
        Set<AgMenuItemIndex> menuItems = dynamicMenu.getMenuItems();
        List<MenuHistory.MenuOption> menuOptions = dynamicMenu.getMenuOptions();
        String menuTitle = convertMenuNodeToString(ussdMenu.getMenuTitleText(),
                menuNodes);

        ussdMenu.setMenuItems(menuItems);
        ussdMenu.setMenuTitleText(menuTitle);

        NextNavigation nextNavigation
                = navigateTo(navigation, menuNodes, menuOptions, ussdMenu,
                        navBar, true, false, nextScrn, totalScrns);

        return nextNavigation;
    }

    private NextNavigation processUSSDMenu(
            String requestInput,
            String msisdn,
            String sessionId)
            throws MyCustomException {

        AgNavigation navigation = getNavigationByMsisdn(msisdn);
        checkSession(navigation, sessionId);
        AgLanguage language = navigation.getClient().getLanguage();
        Map<String, String> menuNodes = getLocalisedMenuNodes(language);

        AgUssdMenu ussdMenu;
        NextNavigation nextNavigation;
        NavigationInput input = NavigationInput.convertToEnum(requestInput);
        String menuTitle;
        switch (input) {

            case MAIN: //00

                ussdMenu = retrieveMenuFromDB(MenuName.MAIN_MENU);
                menuTitle = convertMenuNodeToString(ussdMenu.getMenuTitleText(),
                        menuNodes);
                ussdMenu.setMenuTitleText(menuTitle);
                nextNavigation = navigateTo(navigation, menuNodes,
                        new LinkedList<>(), ussdMenu,
                        NavigationBar.NONE, false, false, 1, 1);
                break;

            case PREVIOUS: //0
                nextNavigation = navigateToPrevious(navigation);
                break;

            case NEXT: //99
                //TO-DO
                nextNavigation = showNextContent(navigation, menuNodes);
                break;

            case CONTINUE: //88 -> continue - old session
                nextNavigation = continueSession(msisdn, sessionId);
                break;

            case USER_INPUT:// ?
                nextNavigation = processRequestInput(navigation, requestInput,
                        menuNodes);
                break;

            default:
                ussdMenu = retrieveMenuFromDB(MenuName.MAIN_MENU);
                menuTitle = convertMenuNodeToString(ussdMenu.getMenuTitleText(),
                        menuNodes);
                ussdMenu.setMenuTitleText(menuTitle);
                nextNavigation = navigateTo(navigation, menuNodes,
                        new LinkedList<>(), ussdMenu,
                        NavigationBar.NONE, false, false, 1, 1);
                break;
        }
        return nextNavigation;
    }

    private CheckAccountResponse getClientRegistration(String msisdn)
            throws MyCustomException {

        CheckAccountRequest request = new CheckAccountRequest();

        CheckAccountRequest.Params params = request.new Params();
        params.setMsisdn(msisdn);

        Credentials credentials = new Credentials();
        credentials.setApiPassword("");
        credentials.setAppId("");
        credentials.setTokenId("");

        request.setCredentials(credentials);
        request.setMethodName(APIMethodName.ACCOUNT_EXISTS.getValue());
        request.setParams(params);

        String jsonReq = GeneralUtils.convertToJson(request,
                CheckAccountRequest.class);
        GeneralUtils.toPrettyJson(jsonReq);

        String response = clientPool.sendRemoteRequest(jsonReq,
                remoteUnitConfig.getDSMBridgeRemoteUnit());
//        String response = "{\n"
//                + "  \"success\": true,\n"
//                + "  \"data\": {\n"
//                + "    \"user_id\": \"" + msisdn + "\",\n"
//                + "    \"is_exist\":false,\n"
//                + "    \"status\": \"REGISTERED\", // |   REGISTERED\n"
//                + "    \"description\": \"New client registered\"\n"
//                + "  }\n"
//                + "}";

        CheckAccountResponse checkAccResponse = GeneralUtils
                .convertFromJson(response, CheckAccountResponse.class);

        return checkAccResponse;

    }

    private AgNavigation addNavigatingClient(String msisdn, String sessionId)
            throws MyCustomException {

        CheckAccountResponse checkAccResponse = getClientRegistration(msisdn);
        boolean isRegistered = checkAccResponse.getData().isIsExist();

        AgClient client = new AgClient();
        client.setMsisdn(msisdn);
        AgLanguage language = internalDbAccess.fetchEntity(AgLanguage.class,
                "langId", "0000");
        client.setLanguage(language);
        client.setIsRegistered(isRegistered);
        if (isRegistered) {
            client.setName(checkAccResponse.getData().getName());
            client.setDistrict(checkAccResponse.getData().getDistrict());
        }

        AgNavigation navig = new AgNavigation();
        navig.setClient(client);
        navig.setSessionId(sessionId);
        navig.setMenuHistory("{}");

        internalDbAccess.saveOrUpdateEntity(navig);

        return navig;
    }

//    Set<AgMenuItemIndex> getBuyerList(String category, String msisdn)
//            throws MyCustomException {
//
//        Set<AgMenuItemIndex> menuItems = new HashSet<>();
//
//        GetBuyerRequest buyerRequest = new GetBuyerRequest();
//
//        GetBuyerRequest.Params params = buyerRequest.new Params();
//        params.setCategory(category);
//        params.setBuyer("");
//
//        Credentials credentials = new Credentials();
//        credentials.setApiPassword("");
//        credentials.setAppId("");
//        credentials.setTokenId("");
//
//        buyerRequest.setCredentials(credentials);
//        buyerRequest.setMethodName(APIMethodName.GET_BUYERS.getValue());
//        buyerRequest.setLocalise("english");//TODO: get language dynamically
//        buyerRequest.setParams(params);
//
//        String jsonReq
//                = GeneralUtils.convertToJson(buyerRequest, GetBuyerRequest.class);
//        GeneralUtils.toPrettyJson(jsonReq);
//
//        //String response = clientPool.sendRemoteRequest(jsonReq, remoteUnitConfig.getAdCentralRemoteUnit());
//        //delete after tests
//        String response = "{\n"
//                + "    \"success\": true,\n"
//                + "    \"data\": [\n"
//                + "        {\n"
//                + "            \"category\": \"FISH\",\n"
//                + "            \"buyers\": [\n"
//                + "                {\n"
//                + "                    \"id\": 23,\n"
//                + "                    \"name\": \"Ozeki Junior\",\n"
//                + "                    \"contact\": \"256785243798\",\n"
//                + "                    \"cost_per_kg\":\"10,000\",\n"
//                + "                    \"region\": \"CENTRAL\",\n"
//                + "                    \"district\": \"Kampala\"\n"
//                + "                },\n"
//                + "                {\n"
//                + "                    \"id\": 24,\n"
//                + "                    \"name\": \"SmallGod Senior\",\n"
//                + "                    \"contact\": \"256785243790\",\n"
//                + "                    \"cost_per_kg\":\"12,000\",\n"
//                + "                    \"region\": \"CENTRAL\",\n"
//                + "                    \"district\": \"Kampala\"\n"
//                + "                }\n"
//                + "            ]\n"
//                + "        }]\n"
//                + "}";
//
//        GetBuyerResponse buyerResponse
//                = GeneralUtils.convertFromJson(response, GetBuyerResponse.class);
//        GeneralUtils.toPrettyJson(response);
//
//        AgMenuItemIndex menuItem;
//        int index = 1;
//        String menuOptionIds = "";
//
//        GetBuyerResponse.Data data
//                = (GetBuyerResponse.Data) buyerResponse.getData()
//                .toArray()[0];
//
//        Set<CategoryItem> items = data.getItem();
//
//        for (CategoryItem item : items) {
//
//            menuOptionIds += (index + "=" + item.getId() + ",");
//
//            String nodeName = item.getName() + ", " + item.getCostPerKg();
//            menuItem = new AgMenuItemIndex(nodeName, index);
//            menuItems.add(menuItem);
//
//            index++;
//        }
//
//        AgNavigation navigation = getNavigationByMsisdn(msisdn);
//        //navigation.setCurrentMenuOptionIds(menuOptionIds);
//        internalDbAccess.saveOrUpdateEntity(navigation);
//        //internalDbAccess.updateNavigation(menuOptionIds, navigation.getId());
//
//        return menuItems;
//    }
//
//    Set<AgMenuItemIndex> getMatchedProducts(String msisdn)
//            throws MyCustomException {
//
//        Set<AgMenuItemIndex> menuItems = new HashSet<>();
//
//        AgMenuItemIndex menuItem;
//        int index = 1;
//        String menuOptionIds = "";
//
//        GetBuyerResponse checkAccResponse = getBuyers(Boolean.TRUE, msisdn, "");
//        Set<GetBuyerResponse.Data> dataSet = checkAccResponse.getData();
//
//        if (dataSet == null || dataSet.isEmpty()) {
//
//        } else {
//
//            for (GetBuyerResponse.Data dataItem : dataSet) {
//
//                String categoryName = dataItem.getCategory();
//                Set<CategoryItem> items = dataItem.getItem();
//                int size = items.size();
//
//                menuOptionIds += (index + "=" + categoryName + ",");
//
//                String nodeName = categoryName + " (" + size + ")";
//                menuItem = new AgMenuItemIndex(nodeName, index);
//                menuItems.add(menuItem);
//
//                index++;
//            }
//            menuItems.add(new AgMenuItemIndex("Refine search", index));
//
//            AgNavigation navigation = getNavigationByMsisdn(msisdn);
//            navigation.setCurrentMenuOptionIds(menuOptionIds);
//            internalDbAccess.saveOrUpdateEntity(navigation);
//
//            logger.debug("Updated with menuoptionids: "
//                    + navigation.getCurrentMenu()
//                    + " options: " + menuOptionIds);
//        }
//        return menuItems;
//    }
//    Set<AgMenuItemIndex> getBuyerCategoryItems(String msisdn)
//            throws MyCustomException {
//
//        Set<AgMenuItemIndex> menuItems = new HashSet<>();
//
//        AgMenuItemIndex menuItem;
//        int index = 1;
//        String menuOptionIds = "";
//
//        GetBuyerResponse checkAccResponse = getBuyers(Boolean.FALSE, "", "");
//
//        for (GetBuyerResponse.Data dataItem : checkAccResponse.getData()) {
//
//            String categoryName = dataItem.getCategory();
//            Set<CategoryItem> items = dataItem.getItem();
//            int size = items.size();
//
//            menuOptionIds += (index + "=" + categoryName + ",");
//
//            String nodeName = categoryName + " (" + size + ")";
//            menuItem = new AgMenuItemIndex(nodeName, index);
//            menuItems.add(menuItem);
//
//            index++;
//        }
//        menuItems.add(new AgMenuItemIndex("New product", index));
//
//        AgNavigation navigation = getNavigationByMsisdn(msisdn);
//        navigation.setCurrentMenuOptionIds(menuOptionIds);
//        internalDbAccess.saveOrUpdateEntity(navigation);
//
//        logger.debug("Updated with menuoptionids: "
//                + navigation.getCurrentMenu() + " options: " + menuOptionIds);
//
//        return menuItems;
//    }
    GetBuyerResponse getBuyersCached(AgNavigation navigation)
            throws MyCustomException {

        GetBuyerResponse marketInfo
                = GeneralUtils.convertFromJson(
                        navigation.getAllBuyersInfo(),
                        GetBuyerResponse.class);
        return marketInfo;

    }

    GetCategoryResponse getCategoriesCached(AgNavigation navigation)
            throws MyCustomException {

        GetCategoryResponse marketInfo
                = GeneralUtils.convertFromJson(
                        navigation.getAllCategoriesData(),
                        GetCategoryResponse.class);
        return marketInfo;

    }

    MarketPriceResponse getMarketPricesCached(AgNavigation navigation)
            throws MyCustomException {

        MarketPriceResponse marketInfo
                = GeneralUtils.convertFromJson(
                        navigation.getAllMarketInfo(),
                        MarketPriceResponse.class);
        return marketInfo;

    }

    MarketPriceResponse2 getMarketPrices2Cached(AgNavigation navigation)
            throws MyCustomException {

        MarketPriceResponse2 marketInfo
                = GeneralUtils.convertFromJson(
                        navigation.getAllMarketInfo(),
                        MarketPriceResponse2.class);
        return marketInfo;

    }

    GetSellersResponse getSellersCached(AgNavigation navigation)
            throws MyCustomException {

        GetSellersResponse allInfo
                = GeneralUtils.convertFromJson(
                        navigation.getAllSellersInfo(),
                        GetSellersResponse.class);
        return allInfo;

    }

    ContactResponse contactRequest(AgProduct product)
            throws MyCustomException {

        int categoryId = product.getCategoryId();
        String transportArea = product.getTransportArea().getValue();
        String districtName = product.getDistrictName();
        int districtId = product.getDistrictId();
        int marketId = product.getMarketId();
        String region = product.getRegion().getValue();
        int subCategoryId = product.getSubCategoryId();
        ItemTag tag = product.getTag();
        String itemDescription = product.getItemDescription();
        String itemName = product.getItemName();
        String measure = product.getQuantity(); //in KG
        String userContact = product.getUserContact();
        int sellerPrice = product.getSellerPrice();
        String measureUnit = product.getMeasureUnit();
        String place = product.getPlace();
        String payMethod = product.getPaymentMethod();
        int sellerId = product.getSellerId();

        UssdFunction function = product.getUssdFunction();
        int price;
        int contactPersonId;
        APIMethodName method;
        if (function == UssdFunction.SELLERS_TRADERS
                || function == UssdFunction.INPUT_TOOLS) {

            contactPersonId = product.getSellerId();
            price = product.getSellerPrice();
            method = APIMethodName.CONTACT_SELLER;

        } else {
            contactPersonId = product.getBuyerId();
            price = product.getBuyerPrice();
            method = APIMethodName.CONTACT_BUYER;
        }

        ContactRequest request = new ContactRequest();

        ContactRequest.Params params = request.new Params();
        params.setCustomerMsisdn(userContact);
        params.setCategoryId(categoryId);
        params.setSubCategoryId(subCategoryId);
        params.setContactPersonId(contactPersonId);
        params.setPrice(new Price(price, measureUnit));

        Credentials credentials = new Credentials();
        credentials.setApiPassword("");
        credentials.setAppId("");
        credentials.setTokenId("");

        request.setCredentials(credentials);
        request.setMethodName(method.getValue());
        request.setLocalise("english");//TODO: get language dynamically
        request.setParams(params);

        String jsonReq = GeneralUtils.convertToJson(request,
                ContactRequest.class);
        GeneralUtils.toPrettyJson(jsonReq);

        String response = clientPool.sendRemoteRequest(jsonReq,
                remoteUnitConfig.getDSMBridgeRemoteUnit());
        //delete after tests
//        String response
//                = " {\n"
//                + "    \"success\": true,\n"
//                + "    \"data\": []\n"
//                + "}";

        ContactResponse uploadResponse
                = GeneralUtils.convertFromJson(response,
                        ContactResponse.class);

        GeneralUtils.toPrettyJson(response);

        return uploadResponse;
    }

    MarketPriceResponse getMarketPricesRemote(AgProduct product)
            throws MyCustomException {

        int categoryId = product.getCategoryId();
        String transportArea = product.getTransportArea().getValue();
        String districtName = product.getDistrictName();
        int districtId = product.getDistrictId();
        int marketId = product.getMarketId();
        String region = product.getRegion().getValue();
        int subCategoryId = product.getSubCategoryId();
        ItemTag tag = product.getTag();
        String itemDescription = product.getItemDescription();
        String itemName = product.getItemName();
        String measure = product.getQuantity(); //in KG
        String sellerContact = product.getUserContact();
        int sellerPrice = product.getSellerPrice();
        String place = product.getPlace();
        String payMethod = product.getPaymentMethod();

        MarketPriceRequest marketPriceRequest = new MarketPriceRequest();

        MarketPriceRequest.Params params = marketPriceRequest.new Params();
        params.setCategoryId(categoryId);
        params.setSubcategoryId(categoryId);
        params.setCategoryClass(tag.getValue());
        params.setMarketId(marketId);
        params.setDistrictId(districtId);
        params.setTransportArea(transportArea);
        params.setRegion(region);

        Credentials credentials = new Credentials();
        credentials.setApiPassword("");
        credentials.setAppId("");
        credentials.setTokenId("");

        marketPriceRequest.setCredentials(credentials);
        marketPriceRequest.setMethodName(
                APIMethodName.GET_MARKET_PRICES.getValue());
        marketPriceRequest.setLocalise("english");//TODO: get language dynamically
        marketPriceRequest.setParams(params);

        String jsonReq = GeneralUtils.convertToJson(marketPriceRequest,
                MarketPriceRequest.class);
        GeneralUtils.toPrettyJson(jsonReq);

        String response = clientPool.sendRemoteRequest(jsonReq,
                remoteUnitConfig.getDSMBridgeRemoteUnit());
        //delete after tests
//        String response
//                = "{\n"
//                + "    \"success\": true,\n"
//                + "    \"data\": [\n"
//                + "        {\n"
//                + "            \"district_id\":34,\n"
//                + "            \"district_name\": \"Kampala\",\n"
//                + "            \"default_product\": {\n"
//                + "                \"id\":1,\n"
//                + "                \"name\": \"Dried tilapia\",\n"
//                + "                \"price\": 6300,\n"
//                + "                \"measure_unit\": \"KG\"\n"
//                + "            }\n"
//                + "        },\n"
//                + "        {\n"
//                + "            \"district_id\":35,\n"
//                + "            \"district_name\": \"Mukono\",\n"
//                + "            \"default_product\": {\n"
//                + "                \"id\":2,\n"
//                + "                \"name\": \"Mukene\",\n"
//                + "                \"price\": 2500,\n"
//                + "                \"measure_unit\": \"KG\"\n"
//                + "            }\n"
//                + "        },\n"
//                + "        {\n"
//                + "            \"district_id\":36,\n"
//                + "            \"district_name\": \"Kumi\",\n"
//                + "            \"default_product\": {\n"
//                + "                \"id\":3,\n"
//                + "                \"name\": \"Mukene\",\n"
//                + "                \"price\": 2800,\n"
//                + "                \"measure_unit\": \"KG\"\n"
//                + "            }\n"
//                + "        },\n"
//                + "        {\n"
//                + "            \"district_id\":37,\n"
//                + "            \"district_name\": \"Gulu\",\n"
//                + "            \"default_product\": {\n"
//                + "                \"id\":4,\n"
//                + "                \"name\": \"Fresh tilapia\",\n"
//                + "                \"price\": 5400,\n"
//                + "                \"measure_unit\": \"whole\"\n"
//                + "            }\n"
//                + "        },\n"
//                + "        {\n"
//                + "            \"district_id\":38,\n"
//                + "            \"district_name\": \"Kabale\",\n"
//                + "            \"default_product\": {\n"
//                + "                \"id\":6,\n"
//                + "                \"name\": \"Dried tilapia\",\n"
//                + "                \"price\": 6300,\n"
//                + "                \"measure_unit\": \"KG\"\n"
//                + "            }\n"
//                + "        },\n"
//                + "        {\n"
//                + "            \"district_id\":39,\n"
//                + "            \"district_name\": \"Kiboga\",\n"
//                + "            \"default_product\": {\n"
//                + "                \"id\":7,\n"
//                + "                \"name\": \"Mukene\",\n"
//                + "                \"price\": 2500,\n"
//                + "                \"measure_unit\": \"KG\"\n"
//                + "            }\n"
//                + "        },\n"
//                + "        {\n"
//                + "            \"district_id\":40,\n"
//                + "            \"district_name\": \"Nakapiripirit\",\n"
//                + "            \"default_product\": {\n"
//                + "                \"id\":8,\n"
//                + "                \"name\": \"Nkejje\",\n"
//                + "                \"price\": 3800,\n"
//                + "                \"measure_unit\": \"KG\"\n"
//                + "            }\n"
//                + "        },\n"
//                + "        {\n"
//                + "            \"district_id\":41,\n"
//                + "            \"district_name\": \"Soroti\",\n"
//                + "            \"default_product\": {\n"
//                + "                \"id\":9,\n"
//                + "                \"name\": \"Fresh tilapia\",\n"
//                + "                \"price\": 4400,\n"
//                + "                \"measure_unit\": \"whole\"\n"
//                + "            }\n"
//                + "        },\n"
//                + "        {\n"
//                + "            \"district_id\":42,\n"
//                + "            \"district_name\": \"Masaka\",\n"
//                + "            \"default_product\": {\n"
//                + "                \"id\":10,\n"
//                + "                \"name\": \"Tilapia\",\n"
//                + "                \"price\": 4300,\n"
//                + "                \"measure_unit\": \"Kg\"\n"
//                + "            }\n"
//                + "        }\n"
//                + "    ]\n"
//                + "}";

        MarketPriceResponse uploadResponse
                = GeneralUtils.convertFromJson(response,
                        MarketPriceResponse.class);

        GeneralUtils.toPrettyJson(response);

        return uploadResponse;
    }

    GetSellersResponse getSellersBuyersRemote(AgProduct product)
            throws MyCustomException {

        int categoryId = product.getCategoryId();
        int subCategoryId = product.getSubCategoryId();
        String transportArea = product.getTransportArea().getValue();
        String districtName = product.getDistrictName();
        int districtId = product.getDistrictId();
        int marketId = product.getMarketId();
        String region = product.getRegion().getValue();
        ItemTag tag = product.getTag();
        String itemDescription = product.getItemDescription();
        String itemName = product.getItemName();
        String measure = product.getQuantity(); //in KG
        String sellerContact = product.getUserContact();
        int sellerPrice = product.getSellerPrice();
        String place = product.getPlace();
        String payMethod = product.getPaymentMethod();
        boolean isMatched = product.isIsMatched();
        UssdFunction function = product.getUssdFunction();

        GetSellersRequest sellersRequest = new GetSellersRequest();

        GetSellersRequest.Params params = sellersRequest.new Params();
        params.setCategoryId(categoryId);
        params.setSubCategoryId(subCategoryId);
        params.setCategoryClass(tag.getValue().toLowerCase());
        params.setDistrictId(districtId);
        params.setTransport(transportArea);
        params.setRegion(region);
        params.setIsMatched(isMatched);

        Credentials credentials = new Credentials();
        credentials.setApiPassword("");
        credentials.setAppId("");
        credentials.setTokenId("");

        String api = APIMethodName.GET_SELLERS.getValue();

        if (function == UssdFunction.FIND_BUYERS) {
            api = APIMethodName.GET_BUYERS.getValue();
        }

        sellersRequest.setCredentials(credentials);
        sellersRequest.setMethodName(api);
        sellersRequest.setLocalise("english");//TODO: get language dynamically
        sellersRequest.setParams(params);

        String jsonReq = GeneralUtils.convertToJson(sellersRequest,
                GetSellersRequest.class);
        GeneralUtils.toPrettyJson(jsonReq);

        String response = clientPool.sendRemoteRequest(jsonReq,
                remoteUnitConfig.getDSMBridgeRemoteUnit());
        //delete after tests
//        String response
//                = "{\n"
//                + "    \"success\": true,\n"
//                + "    \"data\": {\n"
//                + "        \"category_id\": 123,\n"
//                + "        \"sub_category_id\": 22,\n"
//                + "        \"category_class\": \"inputs\",\n"
//                + "        \"region\": \"CENTRAL\",\n"
//                + "        \"districts\": [\n"
//                + "            {\n"
//                + "                \"id\": 21,\n"
//                + "                \"name\": \"Kampala\",\n"
//                + "                \"sellers\": [\n"
//                + "                    {\n"
//                + "                        \"id\": 3,\n"
//                + "                        \"name\": \"Ozeki\",\n"
//                + "                        \"contact\": \"256785243798\",\n"
//                + "                        \"products\": [\n"
//                + "                            {\n"
//                + "                                \"id\": 44,\n"
//                + "                                \"item_name\": \"Dried tilapia\",\n"
//                + "                                \"price\": 2300,\n"
//                + "                                \"measure_unit\": \"KG\"\n"
//                + "                            },\n"
//                + "                            {\n"
//                + "                                \"id\": 45,\n"
//                + "                                \"item_name\": \"Fresh tilapia\",\n"
//                + "                                \"price\": 2250,\n"
//                + "                                \"measure_unit\": \"KG\"\n"
//                + "                            }\n"
//                + "                        ]\n"
//                + "                    },\n"
//                + "                    {\n"
//                + "                        \"id\": 4,\n"
//                + "                        \"name\": \"SmallG\",\n"
//                + "                        \"contact\": \"25674990990\",\n"
//                + "                        \"products\": [\n"
//                + "                            {\n"
//                + "                                \"id\": 46,\n"
//                + "                                \"item_name\": \"mukene omusiike\",\n"
//                + "                                \"price\": 500,\n"
//                + "                                \"measure_unit\": \"omukono\"\n"
//                + "                            }\n"
//                + "                        ]\n"
//                + "                    }\n"
//                + "                ]\n"
//                + "            },\n"
//                + "            {\n"
//                + "                \"id\": 23,\n"
//                + "                \"name\": \"Mukono\",\n"
//                + "                \"sellers\": [\n"
//                + "                    {\n"
//                + "                        \"id\": 3,\n"
//                + "                        \"name\": \"SmallG\",\n"
//                + "                        \"contact\": \"25674990990\",\n"
//                + "                        \"products\": [\n"
//                + "                            {\n"
//                + "                                \"id\": 44,\n"
//                + "                                \"item_name\": \"Gonja\",\n"
//                + "                                \"price\": 15000,\n"
//                + "                                \"measure_unit\": \"enkota\"\n"
//                + "                            }\n"
//                + "                        ]\n"
//                + "                    }\n"
//                + "                ]\n"
//                + "            }\n"
//                + "        ]\n"
//                + "    }\n"
//                + "}";

        GetSellersResponse uploadResponse
                = GeneralUtils.convertFromJson(response,
                        GetSellersResponse.class);

        GeneralUtils.toPrettyJson(response);

        return uploadResponse;
    }

    GetCategoryResponse getCategoriesRemote(AgProduct product)
            throws MyCustomException {

        String categoryClass = product.getTag().getValue().toLowerCase();
        int categoryId = product.getCategoryId();
        int subCategoryId = product.getSubCategoryId();

        GetCategoryRequest categoryRequest = new GetCategoryRequest();

        GetCategoryRequest.Params params = categoryRequest.new Params();
        params.setCategoryClass(categoryClass);
        params.setCategoryId(categoryId);
        params.setSubCategoryId(subCategoryId);

        Credentials credentials = new Credentials();
        credentials.setApiPassword("");
        credentials.setAppId("");
        credentials.setTokenId("");

        categoryRequest.setCredentials(credentials);
        categoryRequest.setMethodName(APIMethodName.GET_CATEGORIES.getValue());
        categoryRequest.setLocalise("english");//TODO: get language dynamically
        categoryRequest.setParams(params);

        String jsonReq = GeneralUtils.convertToJson(categoryRequest,
                GetCategoryRequest.class);
        GeneralUtils.toPrettyJson(jsonReq);

        String response = clientPool.sendRemoteRequest(jsonReq,
                remoteUnitConfig.getDSMBridgeRemoteUnit());
        //delete after tests
//        String response = "{\n"
//                + "    \"success\": true,\n"
//                + "    \"data\": {\n"
//                + "        \"produce\": [\n"
//                + "            {\n"
//                + "                \"category_id\": 99,\n"
//                + "                \"category_name\": \"Other\",\n"
//                + "                \"sub_categories\": []\n"
//                + "            },\n"
//                + "            {\n"
//                + "                \"category_id\": 50,\n"
//                + "                \"category_name\": \"Farmed Fish\",\n"
//                + "                \"sub_categories\": [\n"
//                + "                    {\n"
//                + "                        \"id\": 21,\n"
//                + "                        \"name\": \"Tilapia\"\n"
//                + "                    },\n"
//                + "                    {\n"
//                + "                        \"id\": 22,\n"
//                + "                        \"name\": \"Mukene\"\n"
//                + "                    }\n"
//                + "                ]\n"
//                + "            },\n"
//                + "            {\n"
//                + "                \"category_id\": 51,\n"
//                + "                \"category_name\": \"Cereals\",\n"
//                + "                \"sub_categories\": [\n"
//                + "                    {\n"
//                + "                        \"id\": 30,\n"
//                + "                        \"name\": \"Beans\"\n"
//                + "                    },\n"
//                + "                    {\n"
//                + "                        \"id\": 31,\n"
//                + "                        \"name\": \"G.Nuts\"\n"
//                + "                    }\n"
//                + "                ]\n"
//                + "            },\n"
//                + "            {\n"
//                + "                \"category_id\": 52,\n"
//                + "                \"category_name\": \"Fruits\",\n"
//                + "                \"sub_categories\": [\n"
//                + "                    {\n"
//                + "                        \"id\": 32,\n"
//                + "                        \"name\": \"Mangoes\"\n"
//                + "                    },\n"
//                + "                    {\n"
//                + "                        \"id\": 33,\n"
//                + "                        \"name\": \"Oranges\"\n"
//                + "                    },\n"
//                + "                    {\n"
//                + "                        \"id\": 34,\n"
//                + "                        \"name\": \"Bananas\"\n"
//                + "                    },\n"
//                + "                    {\n"
//                + "                        \"id\": 35,\n"
//                + "                        \"name\": \"Onions\"\n"
//                + "                    },\n"
//                + "                    {\n"
//                + "                        \"id\": 36,\n"
//                + "                        \"name\": \"Grapes\"\n"
//                + "                    },\n"
//                + "                    {\n"
//                + "                        \"id\": 37,\n"
//                + "                        \"name\": \"Mangada\"\n"
//                + "                    },\n"
//                + "                    {\n"
//                + "                        \"id\": 38,\n"
//                + "                        \"name\": \"Mpaafu\"\n"
//                + "                    },\n"
//                + "                    {\n"
//                + "                        \"id\": 39,\n"
//                + "                        \"name\": \"Gonja\"\n"
//                + "                    },\n"
//                + "                    {\n"
//                + "                        \"id\": 40,\n"
//                + "                        \"name\": \"Dates\"\n"
//                + "                    },\n"
//                + "                    {\n"
//                + "                        \"id\": 41,\n"
//                + "                        \"name\": \"Sugar canes\"\n"
//                + "                    },\n"
//                + "                    {\n"
//                + "                        \"id\": 32,\n"
//                + "                        \"name\": \"Mangoes\"\n"
//                + "                    },\n"
//                + "                    {\n"
//                + "                        \"id\": 33,\n"
//                + "                        \"name\": \"Oranges\"\n"
//                + "                    },\n"
//                + "                    {\n"
//                + "                        \"id\": 34,\n"
//                + "                        \"name\": \"Bananas\"\n"
//                + "                    },\n"
//                + "                    {\n"
//                + "                        \"id\": 35,\n"
//                + "                        \"name\": \"Onions\"\n"
//                + "                    },\n"
//                + "                    {\n"
//                + "                        \"id\": 36,\n"
//                + "                        \"name\": \"Grapes\"\n"
//                + "                    },\n"
//                + "                    {\n"
//                + "                        \"id\": 37,\n"
//                + "                        \"name\": \"Mangada\"\n"
//                + "                    },\n"
//                + "                    {\n"
//                + "                        \"id\": 38,\n"
//                + "                        \"name\": \"Mpaafu\"\n"
//                + "                    },\n"
//                + "                    {\n"
//                + "                        \"id\": 39,\n"
//                + "                        \"name\": \"Gonja\"\n"
//                + "                    },\n"
//                + "                    {\n"
//                + "                        \"id\": 40,\n"
//                + "                        \"name\": \"Dates\"\n"
//                + "                    },\n"
//                + "                    {\n"
//                + "                        \"id\": 41,\n"
//                + "                        \"name\": \"Sugar canes\"\n"
//                + "                    },\n"
//                + "                    {\n"
//                + "                        \"id\": 32,\n"
//                + "                        \"name\": \"Mangoes\"\n"
//                + "                    },\n"
//                + "                    {\n"
//                + "                        \"id\": 33,\n"
//                + "                        \"name\": \"Oranges\"\n"
//                + "                    },\n"
//                + "                    {\n"
//                + "                        \"id\": 34,\n"
//                + "                        \"name\": \"Bananas\"\n"
//                + "                    },\n"
//                + "                    {\n"
//                + "                        \"id\": 35,\n"
//                + "                        \"name\": \"Onions\"\n"
//                + "                    }\n"
//                + "                ]\n"
//                + "            },\n"
//                + "            {\n"
//                + "                \"category_id\": 53,\n"
//                + "                \"category_name\": \"Coffee\",\n"
//                + "                \"sub_categories\": [\n"
//                + "                    {\n"
//                + "                        \"id\": 42,\n"
//                + "                        \"name\": \"Arabic\"\n"
//                + "                    }\n"
//                + "                ]\n"
//                + "            },\n"
//                + "            {\n"
//                + "                \"category_id\": 54,\n"
//                + "                \"category_name\": \"Legumes\",\n"
//                + "                \"sub_categories\": [\n"
//                + "                    {\n"
//                + "                        \"id\": 43,\n"
//                + "                        \"name\": \"Peas\"\n"
//                + "                    }\n"
//                + "                ]\n"
//                + "            },\n"
//                + "            {\n"
//                + "                \"category_id\": 55,\n"
//                + "                \"category_name\": \"Malakwang\",\n"
//                + "                \"sub_categories\": [\n"
//                + "                    {\n"
//                + "                        \"id\": 46,\n"
//                + "                        \"name\": \"keng\"\n"
//                + "                    }\n"
//                + "                ]\n"
//                + "            },\n"
//                + "            {\n"
//                + "                \"category_id\": 56,\n"
//                + "                \"category_name\": \"Greens\",\n"
//                + "                \"sub_categories\": [\n"
//                + "                    {\n"
//                + "                        \"id\": 47,\n"
//                + "                        \"name\": \"Ddodo\"\n"
//                + "                    },\n"
//                + "                    {\n"
//                + "                        \"id\": 48,\n"
//                + "                        \"name\": \"Nakkati\"\n"
//                + "                    }\n"
//                + "                ]\n"
//                + "            },\n"
//                + "            {\n"
//                + "                \"category_id\": 57,\n"
//                + "                \"category_name\": \"Nsujju\",\n"
//                + "                \"sub_categories\": [\n"
//                + "                    {\n"
//                + "                        \"id\": 48,\n"
//                + "                        \"name\": \"subcategory\"\n"
//                + "                    }\n"
//                + "                ]\n"
//                + "            }\n"
//                + "        ],\n"
//                + "        \"vap\": [\n"
//                + "            {\n"
//                + "                \"category_id\": 58,\n"
//                + "                \"category_name\": \"vap cat\",\n"
//                + "                \"sub_categories\": [\n"
//                + "                    {\n"
//                + "                        \"id\": 49,\n"
//                + "                        \"name\": \"vap subcat\"\n"
//                + "                    }\n"
//                + "                ]\n"
//                + "            }\n"
//                + "        ],\n"
//                + "        \"inputs\": [\n"
//                + "            {\n"
//                + "                \"category_id\": 59,\n"
//                + "                \"category_name\": \"input cat\",\n"
//                + "                \"sub_categories\": [\n"
//                + "                    {\n"
//                + "                        \"id\": 51,\n"
//                + "                        \"name\": \"input subcat\"\n"
//                + "                    }\n"
//                + "                ]\n"
//                + "            }\n"
//                + "        ]\n"
//                + "    }\n"
//                + "}";

        GetCategoryResponse categoryResponse = GeneralUtils
                .convertFromJson(response, GetCategoryResponse.class);
        return categoryResponse;
    }

    GetBuyerResponse getBuyersRemote(AgProduct product)
            throws MyCustomException {

        int categoryId = product.getCategoryId();
        String transportArea = product.getTransportArea().getValue();
        String districtName = product.getDistrictName();
        int districtId = product.getDistrictId();
        int marketId = product.getMarketId();
        String region = product.getRegion().getValue();
        int subCategoryId = product.getSubCategoryId();
        ItemTag tag = product.getTag();
        String itemDescription = product.getItemDescription();
        String itemName = product.getItemName();
        String measure = product.getQuantity(); //in KG
        String userContact = product.getUserContact();
        int sellerPrice = product.getSellerPrice();
        String place = product.getPlace();
        String payMethod = product.getPaymentMethod();
        boolean matchedBuyers = product.isIsMatched();

        GetBuyerRequest request = new GetBuyerRequest();

        GetBuyerRequest.Params params = request.new Params();
        params.setMatchedBuyers(matchedBuyers);
        params.setBuyer("");
        params.setCategoryClass(tag.getValue());
        params.setCategoryId(categoryId);
        params.setSubCategoryId(subCategoryId);
        params.setCustomerMsisdn(userContact);
        params.setDistrict(districtName);
        params.setTransport(transportArea);
        params.setRegion(region);

        Credentials credentials = new Credentials();
        credentials.setApiPassword("");
        credentials.setAppId("");
        credentials.setTokenId("");

        request.setCredentials(credentials);
        request.setMethodName(
                APIMethodName.GET_BUYERS.getValue());
        request.setLocalise("english");//TODO: get language dynamically
        request.setParams(params);

        String jsonReq = GeneralUtils.convertToJson(request,
                GetBuyerRequest.class);
        GeneralUtils.toPrettyJson(jsonReq);

        String response = clientPool.sendRemoteRequest(jsonReq,
                remoteUnitConfig.getDSMBridgeRemoteUnit());
        //delete after tests
//        String response
//                = "{\n"
//                + "    \"success\": true,\n"
//                + "    \"data\": [\n"
//                + "        {\n"
//                + "            \"buyer_id\": 23,\n"
//                + "            \"buyer_name\": \"Ozeki Junior\",\n"
//                + "            \"contact\": \"256785243798\",\n"
//                + "            \"buying\": [\n"
//                + "                {\n"
//                + "                    \"product_id\": 7487,\n"
//                + "                    \"product_name\": \"Beans\",\n"
//                + "                    \"category_class\":\"produce\",\n"
//                + "                    \"transport\": \"NATIONAL\",\n"
//                + "                    \"region\": \"WESTERN\",\n"
//                + "                    \"district\": \"Kampala\",\n"
//                + "                    \"buying_price\": {\n"
//                + "                        \"amount\": 10000,\n"
//                + "                        \"measure_unit\": \"KG\"\n"
//                + "                    }\n"
//                + "                },\n"
//                + "                {\n"
//                + "                    \"product_id\": 123,\n"
//                + "                    \"product_name\": \"Fish fingerlings\",\n"
//                + "                    \"category_class\":\"inputs\",\n"
//                + "                    \"transport\": \"NATIONAL\",\n"
//                + "                    \"region\": \"WESTERN\",\n"
//                + "                    \"district\": \"Kampala\",\n"
//                + "                    \"buying_price\": {\n"
//                + "                        \"amount\": 10000,\n"
//                + "                        \"measure_unit\": \"KG\"\n"
//                + "                    }\n"
//                + "                }\n"
//                + "            ]\n"
//                + "        },\n"
//                + "        {\n"
//                + "            \"buyer_id\": 13,\n"
//                + "            \"buyer_name\": \"Ozeki Senior\",\n"
//                + "            \"contact\": \"256774983602\",\n"
//                + "            \"buying\": [\n"
//                + "                {\n"
//                + "                    \"product_id\": 103,\n"
//                + "                    \"product_name\": \"Maize\",\n"
//                + "                    \"category_class\":\"produce\",\n"
//                + "                    \"transport\": \"NATIONAL\",\n"
//                + "                    \"region\": \"WESTERN\",\n"
//                + "                    \"district\": \"Kampala\",\n"
//                + "                    \"buying_price\": {\n"
//                + "                        \"amount\": 10000,\n"
//                + "                        \"per\": \"KG\"\n"
//                + "                    }\n"
//                + "                }\n"
//                + "\n"
//                + "            ]\n"
//                + "        }\n"
//                + "    ]\n"
//                + "}";

        GetBuyerResponse buyerResponse
                = GeneralUtils.convertFromJson(response,
                        GetBuyerResponse.class);

        GeneralUtils.toPrettyJson(response);

        return buyerResponse;
    }

//    GetBuyerResponse getBuyers(boolean onlyMatchedBuyers,
//            String sellerContact, String itemCategory)
//            throws MyCustomException {
//
//        GetBuyerRequest buyerRequest = new GetBuyerRequest();
//
//        GetBuyerRequest.Params params = buyerRequest.new Params();
//        params.setCategory(itemCategory);
//        params.setBuyer(sellerContact);
//        params.setOnlyMatchedBuyers(onlyMatchedBuyers);
//
//        Credentials credentials = new Credentials();
//        credentials.setApiPassword("");
//        credentials.setAppId("");
//        credentials.setTokenId("");
//
//        buyerRequest.setCredentials(credentials);
//        buyerRequest.setMethodName(APIMethodName.GET_BUYERS.getValue());
//        buyerRequest.setLocalise("english");//TODO: get language dynamically
//        buyerRequest.setParams(params);
//
//        String jsonReq = GeneralUtils.convertToJson(buyerRequest,
//                GetBuyerRequest.class);
//        GeneralUtils.toPrettyJson(jsonReq);
//
//        //String response = clientPool.sendRemoteRequest(jsonReq, dsmRemoteUnit);
//        //delete after tests
//        String response = "{\n"
//                + "    \"success\": true,\n"
//                + "    \"data\": [\n"
//                + "        {\n"
//                + "            \"category\": \"FISH\",\n"
//                + "            \"buyers\": [\n"
//                + "                {\n"
//                + "                    \"id\": 23,\n"
//                + "                    \"name\": \"Ozeki Junior\",\n"
//                + "                    \"contact\": \"256785243798\",\n"
//                + "                    \"cost_per_kg\":\"10,000\",\n"
//                + "                    \"region\": \"CENTRAL\",\n"
//                + "                    \"district\": \"Kampala\"\n"
//                + "                },\n"
//                + "                {\n"
//                + "                    \"id\": 24,\n"
//                + "                    \"name\": \"SmallGod Senior\",\n"
//                + "                    \"contact\": \"256785243790\",\n"
//                + "                    \"cost_per_kg\":\"12,000\",\n"
//                + "                    \"region\": \"CENTRAL\",\n"
//                + "                    \"district\": \"Kampala\"\n"
//                + "                }\n"
//                + "            ]\n"
//                + "        },\n"
//                + "        {\n"
//                + "            \"category\": \"MAIZE\",\n"
//                + "            \"buyers\": [\n"
//                + "                {\n"
//                + "                    \"id\": 3,\n"
//                + "                    \"name\": \"Ozeki Junior\",\n"
//                + "                    \"contact\": \"256785243798\",\n"
//                + "                    \"cost_per_kg\":10000,\n"
//                + "                    \"region\": \"CENTRAL\",\n"
//                + "                    \"district\": \"Kampala\"\n"
//                + "                },\n"
//                + "                {\n"
//                + "                    \"id\": 6,\n"
//                + "                    \"name\": \"SmallGod Senior\",\n"
//                + "                    \"contact\": \"256785243790\",\n"
//                + "                    \"cost_per_kg\":10000,\n"
//                + "                    \"region\": \"CENTRAL\",\n"
//                + "                    \"district\": \"Kampala\"\n"
//                + "                }\n"
//                + "            ]\n"
//                + "        }\n"
//                + "    ]\n"
//                + "}";
//
//        GetBuyerResponse buyerResponse
//                = GeneralUtils.convertFromJson(response, GetBuyerResponse.class);
//        return buyerResponse;
//    }
    boolean uploadItemForSale(AgProduct productSale,
            boolean isContactBuyer) throws MyCustomException {

        int categoryId = productSale.getCategoryId();
        String transportArea = productSale.getTransportArea().getValue();
        String districtName = productSale.getDistrictName();
        int districtId = productSale.getDistrictId();
        int marketId = productSale.getMarketId();
        String region = productSale.getRegion().getValue();
        int subCategoryId = productSale.getSubCategoryId();
        ItemTag tag = productSale.getTag();
        String itemDescription = productSale.getItemDescription();
        String itemName = productSale.getItemName();
        String measure = productSale.getQuantity(); //in KG
        String sellerContact = productSale.getUserContact();
        int sellerPrice = productSale.getSellerPrice();
        String place = productSale.getPlace();
        String payMethod = productSale.getPaymentMethod();

        ItemUploadRequest itemUploadRequest = new ItemUploadRequest();

        ItemUploadRequest.Params params = itemUploadRequest.new Params();

        ItemUploadRequest.Params.ItemLocation itemLocation
                = params.new ItemLocation();
        itemLocation.setPlace(place);
        itemLocation.setDistrict(districtName);
        itemLocation.setRegion(region);

        ItemUploadRequest.Params.Quantity quantity = params.new Quantity();
        quantity.setMeasure(measure);
        quantity.setUnit("KG"); //default

        params.setSellingPrice(new Price(sellerPrice, "KG"));
        params.setQuantity(quantity);
        params.setItemLocation(itemLocation);
        params.setCategoryId(categoryId);
        params.setSubCategoryId(subCategoryId);
        params.setItemDescription(itemDescription);
        params.setItemName(itemName);
        params.setPaymentMethod(payMethod);
        params.setTransportArea(transportArea);
        params.setSellerContact(sellerContact);
        params.setTag(tag.getValue());

        Credentials credentials = new Credentials();
        credentials.setApiPassword("");
        credentials.setAppId("");
        credentials.setTokenId("");

        itemUploadRequest.setCredentials(credentials);
        itemUploadRequest.setMethodName(
                APIMethodName.UPLOAD_ITEM_FOR_SALE.getValue());
        itemUploadRequest.setLocalise("english");//TODO: get language dynamically
        itemUploadRequest.setParams(params);

        String jsonReq = GeneralUtils.convertToJson(itemUploadRequest,
                ItemUploadRequest.class);
        GeneralUtils.toPrettyJson(jsonReq);

        String response = clientPool.sendRemoteRequest(jsonReq,
                remoteUnitConfig.getDSMBridgeRemoteUnit());
        //delete after tests
//        String response
//                = "{\n"
//                + "  \"success\": true,\n"
//                + "  \"data\": {\n"
//                + "    \"status\": \"SUCCESSFUL\",\n"
//                + "    \"description\": \"Uploaded successfuly\"\n"
//                + "  }\n"
//                + "}";

        ItemUploadResponse uploadResponse
                = GeneralUtils.convertFromJson(response,
                        ItemUploadResponse.class);

        GeneralUtils.toPrettyJson(response);

        return uploadResponse.isSuccess();
    }

    boolean sendContactBuyerRequest(AgProduct productSale,
            boolean isContactBuyer) throws MyCustomException {

        int buyerId = productSale.getBuyerId();
        int buyerPrice = productSale.getBuyerPrice();
        String buyerName = productSale.getBuyerName();
        String itemDescription = productSale.getItemDescription();
        String itemName = productSale.getItemName();
        String quantity = productSale.getQuantity();
        String sellerContact = productSale.getUserContact();
        int sellerPrice = productSale.getSellerPrice();
        String transportArea = productSale.getTransportArea().getValue();
        String itemLocation = productSale.getDistrictName();
        String payMethod = productSale.getPaymentMethod();

        ContactBuyerRequest buyerRequest = new ContactBuyerRequest();

        ContactBuyerRequest.Params params = buyerRequest.new Params();
        ContactBuyerRequest.Selling selling = buyerRequest.new Selling();

        selling.setItemDescription(itemDescription);
        selling.setItemLocation(itemLocation);
        selling.setItemName(itemName);
        selling.setPaymentMethod(payMethod);
        selling.setSellerPrice(sellerPrice);
        selling.setQuantity(quantity);
        selling.setTransportArea(transportArea);

        params.setIsContactBuyer(isContactBuyer);
        params.setBuyerId("" + buyerId);
        params.setSellerContact(sellerContact);
        params.setSelling(selling);

        Credentials credentials = new Credentials();
        credentials.setApiPassword("");
        credentials.setAppId("");
        credentials.setTokenId("");

        buyerRequest.setCredentials(credentials);
        buyerRequest.setMethodName(APIMethodName.CONTACT_BUYER.getValue());
        buyerRequest.setLocalise("english");//TODO: get language dynamically
        buyerRequest.setParams(params);

        String jsonReq = GeneralUtils.convertToJson(buyerRequest,
                ContactBuyerRequest.class);
        GeneralUtils.toPrettyJson(jsonReq);

        String response = clientPool.sendRemoteRequest(jsonReq,
                remoteUnitConfig.getDSMBridgeRemoteUnit());
        //delete after tests
//        String response
//                = "{\n"
//                + "    \"success\": true,\n"
//                + "    \"data\": []\n"
//                + "}";

        ContactResponse buyerResponse
                = GeneralUtils.convertFromJson(response,
                        ContactResponse.class);

        GeneralUtils.toPrettyJson(response);

        return buyerResponse.isSuccess();
    }

    String getIdFromChosenInput(String requestInput, String menuOptionIds) {

        String[] menuOptions = menuOptionIds.split(",");
        for (int i = 0; i < menuOptions.length; i++) {

            if (requestInput.equalsIgnoreCase(String.valueOf(menuOptions.length + 1))) {
                return "CUSTOM";
            }

            String str = menuOptions[i];

            String[] split = str.split("=");
            String index = split[0];
            String id = split[1];

            if (index.equalsIgnoreCase(requestInput)) {
                return id;
            }
        }
        return null;
    }

    MenuName chosenMarket() {

        MenuName newMenu = MenuName.MARKET_SERVICES;

        //do some processing if any
        return newMenu;
    }

    MenuName contactSellerConfirm() {

        MenuName newMenu = MenuName.CONTACT_BUYER_MESSAGE;

        //do some processing if any
        return newMenu;
    }

    MenuName contactSellerCancel() {

        MenuName newMenu = MenuName.MAIN_MENU;

        //do some processing if any
        return newMenu;
    }

    MenuName contactSeller() {

        MenuName newMenu = MenuName.CONTACT_SELLER;

        //do some processing if any
        return newMenu;
    }

    MenuName sellingCentralKampal() {

        MenuName newMenu = MenuName.SELLER_LIST;

        //do some processing if any
        return newMenu;
    }

    MenuName sellingRegionCentral() {

        MenuName newMenu = MenuName.SELLING_REGION_CENTRAL;

        //do some processing if any
        return newMenu;
    }

    MenuName sellingRegionEast() {

        MenuName newMenu = MenuName.SELLING_REGION_EASTERN;

        //do some processing if any
        return newMenu;
    }

    MenuName sellingProduceCatfish() {

        MenuName newMenu = MenuName.REGION;

        //do some processing if any
        return newMenu;
    }

    MenuName sellingProduceTilapia() {

        MenuName newMenu = MenuName.REGION;

        //do some processing if any
        return newMenu;
    }

    MenuName sellingProduceRice() {

        MenuName newMenu = MenuName.REGION;

        //do some processing if any
        return newMenu;
    }

    MenuName sellingProduceushroom() {

        MenuName newMenu = MenuName.REGION;

        //do some processing if any
        return newMenu;
    }

    MenuName easternCommodityPrice() {

        MenuName newMenu = MenuName.COMMODITY_PRICE_DISPLAY;

        //do some processing if any
        return newMenu;
    }

    MenuName westernCommodityPrice() {

        MenuName newMenu = MenuName.COMMODITY_PRICE_DISPLAY;

        //do some processing if any
        return newMenu;
    }

    MenuName northernCommodityPrice() {

        MenuName newMenu = MenuName.COMMODITY_PRICE_DISPLAY;

        //do some processing if any
        return newMenu;
    }

    MenuName centralCommodityPrice() {

        MenuName newMenu = MenuName.COMMODITY_PRICE_DISPLAY;

        //do some processing if any
        return newMenu;
    }

    MenuName regionAreaMarketPrice() {

        MenuName newMenu = MenuName.MARKETS;

        //do some processing if any
        return newMenu;
    }

    MenuName centralMarketPrice() {

        MenuName newMenu = MenuName.MARKET_PRICES;

        //do some processing if any
        return newMenu;
    }

    MenuName easternMarketPrice() {

        MenuName newMenu = MenuName.MARKET_PRICES;

        //do some processing if any
        return newMenu;
    }

    MenuName westernMarketPrice() {

        MenuName newMenu = MenuName.MARKET_PRICES;

        //do some processing if any
        return newMenu;
    }

    MenuName northernMarketPrice() {

        MenuName newMenu = MenuName.MARKET_PRICES;

        //do some processing if any
        return newMenu;
    }

    MenuName tilapiaCommodityPrice() {

        MenuName newMenu = MenuName.COMMODITY_PRICE_REGION;

        //do some processing if any
        return newMenu;
    }

    MenuName vanillaCommodityPrice() {

        MenuName newMenu = MenuName.COMMODITY_PRICE_REGION;

        //do some processing if any
        return newMenu;
    }

    MenuName catFishCommodityPrice() {

        MenuName newMenu = MenuName.COMMODITY_PRICE_REGION;

        //do some processing if any
        return newMenu;
    }

    MenuName contactBuyer() {

        MenuName newMenu = MenuName.CONTACT_BUYER_MESSAGE;

        //do some processing if any
        return newMenu;
    }

    MenuName dontContactBuyer() {

        MenuName newMenu = MenuName.MAIN_MENU;

        //do some processing if any
        return newMenu;
    }

    MenuName ayubuBuyer() {

        MenuName newMenu = MenuName.CONTACT_BUYER;

        //do some processing if any
        return newMenu;
    }

    MenuName smallGodBuyer() {

        MenuName newMenu = MenuName.CONTACT_BUYER;

        //do some processing if any
        return newMenu;
    }

    MenuName willyBuyer() {

        MenuName newMenu = MenuName.CONTACT_BUYER;

        //do some processing if any
        return newMenu;
    }

    MenuName tilapiaBuyers() {

        MenuName newMenu = MenuName.BUYER_LOCATION;

        //do some processing if any
        return newMenu;
    }

    MenuName catFishBuyers() {

        MenuName newMenu = MenuName.BUYER_LOCATION;

        //do some processing if any
        return newMenu;
    }

    MenuName yesUpload() {

        MenuName newMenu = MenuName.UPLOAD_MSG;

        //do some processing if any
        return newMenu;
    }

    MenuName cancelUpload() {

        MenuName newMenu = MenuName.MAIN_MENU;

        //do some processing if any
        return newMenu;
    }

    MenuName toBePicked() {

        MenuName newMenu = MenuName.CONFIRM;

        //do some processing if any
        return newMenu;
    }

    MenuName nationWideTransport() {

        MenuName newMenu = MenuName.CONFIRM;

        //do some processing if any
        return newMenu;
    }

    MenuName internationTransport() {

        MenuName newMenu = MenuName.CONFIRM;

        //do some processing if any
        return newMenu;
    }

    MenuName cashPayment() {

        MenuName newMenu = MenuName.TRANSPORT;

        //do some processing if any
        return newMenu;
    }

    MenuName mmPayment() {

        MenuName newMenu = MenuName.TRANSPORT;

        //do some processing if any
        return newMenu;
    }

    NextNavigation continueSession(String msisdn, String newSessionId)
            throws MyCustomException {

        AgNavigation navigation = getNavigationByMsisdn(msisdn);

        logger.debug("Menu History: " + navigation.getMenuHistory());

        MenuHistory menuHistory
                = getMenuHistoryHelper(navigation.getMenuHistory());
        menuHistory //remove continue_sess
                = popMostRecentMenuFromHistory(menuHistory);

        MenuHistory.Data menuData = getCurrentMenuData(menuHistory);

        navigation.setSessionId(newSessionId);
        navigation.setMenuHistory(menuHistory.toString());
        internalDbAccess.saveOrUpdateEntity(navigation);

        return new NextNavigation(navigation,
                menuData.getMenuString(), menuData.isIsEnd());
    }

    MenuName checkIfContinueSession(String msisdn, String newSessionId)
            throws MyCustomException {

        AgNavigation navigation = getNavigationByMsisdn(msisdn);

        MenuName newMenu;

        int hours = DateUtils.getHoursTakenToNow(navigation.getDateLastModified());

        //Put this in Config File
        if (hours > NamedConstants.SAVED_SESSION_VALIDITY) {

            newMenu = MenuName.MAIN_MENU;
            navigation.setMenuHistory("{}"); //overwrite/delete History dataItem

            return newMenu;
        }

        return MenuName.CONTINUE_SESSION;
    }

    MenuName inputsAndTools() {

        MenuName newMenu = MenuName.INPUT_TOOLS;

        //do some processing if any
        return newMenu;
    }

    MenuName phoneFarming() {

        MenuName newMenu = MenuName.LEARNING_CATEGORIES;

        //do some processing if any
        return newMenu;
    }

    MenuName learningCategory() {

        MenuName newMenu = MenuName.LEARNING_SUBCATEGORY1;

        //do some processing if any
        return newMenu;
    }

    MenuName learningSubCategories1() {

        MenuName newMenu = MenuName.LEARNING_SUBCATEGORY2;

        //do some processing if any
        return newMenu;
    }

    MenuName learningSubCategories2() {

        MenuName newMenu = MenuName.LEARNING_SUBCATEGORY3;

        //do some processing if any
        return newMenu;
    }

    MenuName weather() {

        MenuName newMenu = MenuName.WEATHER_REGION;

        //do some processing if any
        return newMenu;
    }

    MenuName weatherRegion() {

        MenuName newMenu = MenuName.WEATHER_DISTRICT;

        //do some processing if any
        return newMenu;
    }

    MenuName weatherDistrict() {

        MenuName newMenu = MenuName.SUCCESS_PAGE;

        //do some processing if any
        return newMenu;
    }

    MenuName learningSubCategories3() {

        MenuName newMenu = MenuName.SUCCESS_PAGE;

        //do some processing if any
        return newMenu;
    }

    MenuName chosenInputToolsProduce() {

        MenuName newMenu = MenuName.INPUT_TOOLS_LIST;

        //do some processing if any
        return newMenu;
    }

    MenuName chosenInputToolsItem() {

        MenuName newMenu = MenuName.INPUT_TOOLS_FILTERBY;

        //do some processing if any
        return newMenu;
    }

    MenuName chosenInputToolsByRegion() {

        MenuName newMenu = MenuName.INPUT_TOOLS_FILTERBY_REGION;

        //do some processing if any
        return newMenu;
    }

    MenuName chosenInputToolsByDistrict() {

        MenuName newMenu = MenuName.INPUT_TOOLS_FILTERBY_DISTRICT;

        //do some processing if any
        return newMenu;
    }

    MenuName inputToolsSellerList() {

        MenuName newMenu = MenuName.INPUT_TOOLS_SELLER_LIST;

        //do some processing if any
        return newMenu;
    }

    MenuName confirm() {

        MenuName newMenu = MenuName.SUCCESS_PAGE;

        //do some processing if any
        return newMenu;
    }

    MenuName goToCheckOutPage() {

        MenuName newMenu = MenuName.CHECK_OUT_PAGE;

        //do some processing if any
        return newMenu;
    }

    MenuName gotoMainMenu() {

        MenuName newMenu = MenuName.MAIN_MENU;

        //do some processing if any
        return newMenu;
    }

    MenuName locationDescription() {

        MenuName newMenu = MenuName.QUANTITY;

        //do some processing if any
        return newMenu;
    }

    MenuName quantity() {

        MenuName newMenu = MenuName.PRICE;

        //do some processing if any
        return newMenu;
    }

    MenuName price() {

        MenuName newMenu = MenuName.PAY_METHOD;

        //do some processing if any
        return newMenu;
    }

    MenuName acceptablePayMethod() {

        MenuName newMenu = MenuName.TRANSPORT;

        //do some processing if any
        return newMenu;
    }

    MenuName getAdvice() {

        MenuName newMenu = MenuName.ADVICE_TOPICS;

        //do some processing if any
        return newMenu;
    }

    MenuName adviceTopic() {

        MenuName newMenu = MenuName.ADVICE_QUERY;

        //do some processing if any
        return newMenu;
    }

    MenuName adviceQueryMessage() {

        MenuName newMenu = MenuName.SUCCESS_PAGE;

        //do some processing if any
        return newMenu;
    }

    MenuName confirmProductUpload() {

        MenuName newMenu = MenuName.UPLOAD_MSG;

        //do some processing if any
        return newMenu;
    }

    MenuName rice() {

        MenuName newMenu = MenuName.PRODUCT_DESCRIPTION;

        //do some processing if any
        return newMenu;
    }

    MenuName bananas() {

        MenuName newMenu = MenuName.PRODUCT_DESCRIPTION;

        //do some processing if any
        return newMenu;
    }

    MenuName customProduce() {

        MenuName newMenu = MenuName.ENTERED_PRODUCE;

        //do some processing if any
        return newMenu;
    }

    MenuName cageTilapia() {

        MenuName newMenu = MenuName.PRODUCT_DESCRIPTION;

        //do some processing if any
        return newMenu;
    }

    MenuName pondTilapia() {

        MenuName newMenu = MenuName.PRODUCT_DESCRIPTION;

        //do some processing if any
        return newMenu;
    }

    MenuName cageCatfish() {

        MenuName newMenu = MenuName.PRODUCT_DESCRIPTION;

        //do some processing if any
        return newMenu;
    }

    MenuName pondCatfish() {

        MenuName newMenu = MenuName.PRODUCT_DESCRIPTION;

        //do some processing if any
        return newMenu;
    }

    MenuName fish() {

        MenuName newMenu = MenuName.FISH_TYPE;

        //do some processing if any
        return newMenu;
    }

    MenuName produce() {

        MenuName newMenu = MenuName.SELECTED_PRODUCE;

        //do some processing if any
        return newMenu;
    }

    MenuName myAccount() {

        MenuName newMenu = MenuName.MY_ACCOUNT;

        //do some processing if any
        return newMenu;
    }

    MenuName marketPrices() {

        MenuName newMenu = MenuName.MARKET_PRICE_REGION;

        //do some processing if any
        return newMenu;
    }

    MenuName commodityPrices() {

        MenuName newMenu = MenuName.COMMODITY_PRICE;

        //do some processing if any
        return newMenu;
    }

    MenuName underMaintenance() {

//        if (userInput.equalsIgnoreCase("00")) {
//            //user should input '00' to go to main menuData
//        }
        MenuName newMenu = MenuName.UNDER_MAINTENANCE;

        //do some processing if any
        return newMenu;
    }

    boolean checkSession(AgNavigation navigation, String newSessionID) throws MyCustomException {

        boolean isSessionOk = Boolean.TRUE;

        String oldSessionId = navigation.getSessionId();
        if (!oldSessionId.equalsIgnoreCase(newSessionID)) {
            logger.error("SessionID changed in the middle of a session, "
                    + "Old id: " + oldSessionId + " New id: " + newSessionID);
            logger.error("Going to update SessionID in DB with new unexpected Id!");

            navigation.setSessionId(newSessionID);
            internalDbAccess.saveOrUpdateEntity(navigation);

            isSessionOk = Boolean.FALSE;
        }
        return isSessionOk;
    }

    private void unKnownMethodNameError() throws MyCustomException {

        MyCustomException error
                = GeneralUtils.getSingleError(ErrorCode.INTERNAL_ERR,
                        NamedConstants.GENERIC_ERR_DESC,
                        "Failed to get a corresponding function name");
        throw error;
    }

    private void generalError() throws MyCustomException {

        MyCustomException error
                = GeneralUtils.getSingleError(ErrorCode.INTERNAL_ERR,
                        NamedConstants.GENERIC_ERR_DESC,
                        "Failed to process, expected to process either "
                        + "a valid function name or a valid menu node");
        throw error;
    }
}
