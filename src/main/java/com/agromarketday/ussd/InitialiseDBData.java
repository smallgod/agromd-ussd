package com.agromarketday.ussd;

import com.agromarketday.ussd.constant.MenuName;
import com.agromarketday.ussd.constant.MenuType;
import com.agromarketday.ussd.database.CustomHibernate;
import com.agromarketday.ussd.datamodel.AgLanguage;
import com.agromarketday.ussd.datamodel.AgUssdMenu;
import com.agromarketday.ussd.datamodel.AgMenuItemIndex;
import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.logger.LoggerUtil;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author smallgod
 */
public class InitialiseDBData {

    private static final LoggerUtil logger = new LoggerUtil(InitialiseDBData.class);

    private final CustomHibernate customHibernate;

    public InitialiseDBData(CustomHibernate customHibernate) {
        this.customHibernate = customHibernate;
    }

    public void gummaaza() throws MyCustomException {
    }

    /**
     * Initialise Database
     *
     * @throws MyCustomException
     */
    public void initDB() throws MyCustomException {

        if (menusInitialised()) {
            logger.debug("Database already initialised, skipping!");
            return;
        }

        addMenuLanguages();
        addMenuTree();
    }

    /**
     * Add all available languages
     *
     * @throws MyCustomException
     */
    public void addMenuLanguages() throws MyCustomException {

        Set<AgLanguage> languages = new HashSet<>();

        AgLanguage notSet = new AgLanguage("notset", "0000", "not set", true); //users who haven't set lang yet
        AgLanguage english = new AgLanguage("english", "1001", "English", true);
        AgLanguage acholi = new AgLanguage("luganda", "1002", "Acooli", true);
        AgLanguage swahili = new AgLanguage("acholi", "1003", "Luganda", true);

        languages.add(notSet);
        languages.add(english);
        languages.add(acholi);
        languages.add(swahili);

        customHibernate.saveBulk(languages);
    }

    /**
     * Add all menus to DB
     *
     * @throws MyCustomException
     */
    public void addMenuTree() throws MyCustomException {
        //To-DO: We might want to get AgMenuItemIndex set from a DB for example
        //If I want to pick types of fish - this might not be static - might have to get it from DB
        AgUssdMenu previousMenu, mainMenu, errorMenu, startMenu, endMenu, maintenanceMenu,
                menu1, menu2, menu3, menu4, menu5, menu6, menu7, menu8, menu9,
                menu10, menu11, menu12, menu13, menu14, menu15, menu16, menu17,
                menu18, menu19, menu20, menu21, menu22, menu23, menu24, menu25,
                menu26, menu27, menu28, menu29, menu30, menu31, menu32, menu33,
                menu34, menu35, menu36, menu37, menu38, menu39, menu40,
                menu41, menu42, menu43, menu44, menu45, menu46, menu47,
                menu48, menu49, menu50, menu51, menu52, menu53, menu54,
                menu55, menu56, menu57, menu58, menu59, menu60, menu61,
                menu62, menu63, menu64, menu65, menu66, menu67, menu68;

        Set<AgUssdMenu> menus = new HashSet<>();
        Set<AgMenuItemIndex> defaultMenuItems;

        //Select Language
        menu1 = new AgUssdMenu();
        menu1.setMenuTitleText("1000");
        menu1.setMenuName(MenuName.LANGUAGE_MENU);
        menu1.setMenuType(MenuType.SELECT_STATIC_MENU); //change back to Dynamic menu after making it dynamic
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1001", 1));
        //defaultMenuItems.add(new AgMenuItemIndex("1002", 2));
        menu1.setMenuItems(defaultMenuItems);

        //Main screen
        menu2 = new AgUssdMenu();
        menu2.setMenuTitleText("1009");
        menu2.setMenuName(MenuName.MAIN_MENU);
        menu2.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1019", 1));
        defaultMenuItems.add(new AgMenuItemIndex("1010", 2));
        defaultMenuItems.add(new AgMenuItemIndex("1008", 3));
        //defaultMenuItems.add(new AgMenuItemIndex("1018", 2));
        //defaultMenuItems.add(new AgMenuItemIndex("1011", 3));
        defaultMenuItems.add(new AgMenuItemIndex("1012", 4));
        defaultMenuItems.add(new AgMenuItemIndex("1013", 5));
        //defaultMenuItems.add(new AgMenuItemIndex("1015", 8));
        defaultMenuItems.add(new AgMenuItemIndex("1016", 6));
        defaultMenuItems.add(new AgMenuItemIndex("1014", 7));
        defaultMenuItems.add(new AgMenuItemIndex("1017", 8));
        menu2.setMenuItems(defaultMenuItems);

        //Sales category
        menu3 = new AgUssdMenu();
        menu3.setMenuTitleText("1020");
        menu3.setMenuName(MenuName.SELECT_ITEM_TYPE);
        menu3.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1021", 1));
        defaultMenuItems.add(new AgMenuItemIndex("1022", 2));
        defaultMenuItems.add(new AgMenuItemIndex("1023", 3));
        menu3.setMenuItems(defaultMenuItems);

        menu4 = new AgUssdMenu();
        menu4.setMenuTitleText("1030");
        menu4.setMenuName(MenuName.SELECTED_PRODUCE);
        menu4.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1031", 1));
        defaultMenuItems.add(new AgMenuItemIndex("1032", 2));
        defaultMenuItems.add(new AgMenuItemIndex("1033", 3));
        defaultMenuItems.add(new AgMenuItemIndex("1034", 4));
        menu4.setMenuItems(defaultMenuItems);

        menu16 = new AgUssdMenu();
        menu16.setMenuTitleText("1035");
        menu16.setMenuName(MenuName.ENTERED_PRODUCE);
        menu16.setMenuType(MenuType.INPUT_MENU);
        menu16.setMenuItems(new HashSet<>());

        menu5 = new AgUssdMenu();
        menu5.setMenuTitleText("");
        menu5.setMenuName(MenuName.FISH_TYPE);
        menu5.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1041", 1));
        defaultMenuItems.add(new AgMenuItemIndex("1042", 2));
        defaultMenuItems.add(new AgMenuItemIndex("1043", 3));
        defaultMenuItems.add(new AgMenuItemIndex("1044", 4));
        menu5.setMenuItems(defaultMenuItems);

        menu6 = new AgUssdMenu();
        menu6.setMenuTitleText("1050");
        menu6.setMenuName(MenuName.PRODUCT_DESCRIPTION);
        menu6.setMenuType(MenuType.INPUT_MENU);
        menu6.setMenuItems(new HashSet<>());

        menu7 = new AgUssdMenu();
        menu7.setMenuTitleText("1051");
        menu7.setMenuName(MenuName.ITEM_PLACE);
        menu7.setMenuType(MenuType.INPUT_MENU);
        menu7.setMenuItems(new HashSet<>());

        menu8 = new AgUssdMenu();
        menu8.setMenuTitleText("1052");
        menu8.setMenuName(MenuName.QUANTITY);
        menu8.setMenuType(MenuType.INPUT_MENU);
        menu8.setMenuItems(new HashSet<>());

        menu9 = new AgUssdMenu();
        menu9.setMenuTitleText("1053");
        menu9.setMenuName(MenuName.PRICE);
        menu9.setMenuType(MenuType.INPUT_MENU);
        menu9.setMenuItems(new HashSet<>());

        menu10 = new AgUssdMenu();
        menu10.setMenuTitleText("1060");
        menu10.setMenuName(MenuName.PAY_METHOD);
        menu10.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1061", 1));
        defaultMenuItems.add(new AgMenuItemIndex("1062", 2));
        menu10.setMenuItems(defaultMenuItems);

        menu11 = new AgUssdMenu();
        menu11.setMenuTitleText("1070");
        menu11.setMenuName(MenuName.TRANSPORT);
        menu11.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1071", 1));
        defaultMenuItems.add(new AgMenuItemIndex("1072", 2));
        defaultMenuItems.add(new AgMenuItemIndex("1073", 3));
        menu11.setMenuItems(defaultMenuItems);

        menu12 = new AgUssdMenu();
        menu12.setMenuTitleText("1080");
        menu12.setMenuName(MenuName.CONFIRM);
        menu12.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1081", 1));
        defaultMenuItems.add(new AgMenuItemIndex("1082", 2));
        menu12.setMenuItems(defaultMenuItems);

        menu13 = new AgUssdMenu();
        menu13.setMenuTitleText("1090");
        menu13.setMenuName(MenuName.UPLOAD_MSG);
        menu13.setMenuType(MenuType.INPUT_MENU);
        menu13.setMenuItems(new HashSet<>());

        menu14 = new AgUssdMenu();
        menu14.setMenuTitleText("1100");
        menu14.setMenuName(MenuName.MY_ACCOUNT);
        menu14.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
//      defaultMenuItems.add(new AgMenuItemIndex("1101", 1));
//      defaultMenuItems.add(new AgMenuItemIndex("1102", 2));
//      defaultMenuItems.add(new AgMenuItemIndex("1103", 3));
//      defaultMenuItems.add(new AgMenuItemIndex("1104", 4));
        defaultMenuItems.add(new AgMenuItemIndex("1105", 1));
        defaultMenuItems.add(new AgMenuItemIndex("1107", 2));
//        defaultMenuItems.add(new AgMenuItemIndex("1106", 6));
        menu14.setMenuItems(defaultMenuItems);

        menu15 = new AgUssdMenu();
        menu15.setMenuTitleText("1110");
        menu15.setMenuName(MenuName.CONTINUE_SESSION);
        menu15.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1111", 88));
        menu15.setMenuItems(defaultMenuItems);

        menu17 = new AgUssdMenu();
        menu17.setMenuTitleText("1120");
        menu17.setMenuName(MenuName.MATCHED_PRODUCTS);
        menu17.setMenuType(MenuType.SELECT_DYNAMIC_MENU);
        menu17.setMenuItems(new HashSet<>());

        menu18 = new AgUssdMenu();
        menu18.setMenuTitleText("1130");
        menu18.setMenuName(MenuName.ITEM_LOCATION);
        menu18.setMenuType(MenuType.SELECT_STATIC_DYNAMIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1131", 1));
        defaultMenuItems.add(new AgMenuItemIndex("1132", 2));
        defaultMenuItems.add(new AgMenuItemIndex("1133", 3));
        //defaultMenuItems.add(new AgMenuItemIndex("1134", 4));
        menu18.setMenuItems(defaultMenuItems);

        menu19 = new AgUssdMenu();
        menu19.setMenuTitleText("1140");
        menu19.setMenuName(MenuName.BUYER_LIST);
        menu19.setMenuType(MenuType.SELECT_DYNAMIC_MENU);
        menu19.setMenuItems(new HashSet<>());

        menu20 = new AgUssdMenu();
        menu20.setMenuTitleText("1150");
        menu20.setMenuName(MenuName.CONTACT_BUYER);
        menu20.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1151", 1));
        defaultMenuItems.add(new AgMenuItemIndex("1152", 2));
        menu20.setMenuItems(defaultMenuItems);

        menu21 = new AgUssdMenu();
        menu21.setMenuTitleText("1153"); //has space holder for buyer name
        menu21.setMenuName(MenuName.CONTACT_BUYER_MESSAGE); //
        menu21.setMenuType(MenuType.INPUT_MENU);
        menu21.setMenuItems(new HashSet<>());

        menu22 = new AgUssdMenu();
        menu22.setMenuTitleText("1160");
        menu22.setMenuName(MenuName.SELLING_CATEGORIES);
        menu22.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1161", 1));
        defaultMenuItems.add(new AgMenuItemIndex("1162", 2));
        defaultMenuItems.add(new AgMenuItemIndex("1163", 3));
        menu22.setMenuItems(defaultMenuItems);

        menu23 = new AgUssdMenu();
        menu23.setMenuTitleText("1170");
        menu23.setMenuName(MenuName.ITEM_CATEGORIES);
        menu23.setMenuType(MenuType.SELECT_DYNAMIC_MENU);
        menu23.setMenuItems(new HashSet<>());

        menu24 = new AgUssdMenu();
        menu24.setMenuTitleText("1180");
        menu24.setMenuName(MenuName.REGION);
        menu24.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1181", 1));
        defaultMenuItems.add(new AgMenuItemIndex("1182", 2));
        defaultMenuItems.add(new AgMenuItemIndex("1183", 3));
        defaultMenuItems.add(new AgMenuItemIndex("1184", 4));
        defaultMenuItems.add(new AgMenuItemIndex("1185", 5));
        defaultMenuItems.add(new AgMenuItemIndex("1186", 6));
        menu24.setMenuItems(defaultMenuItems);

        menu25 = new AgUssdMenu();
        menu25.setMenuTitleText("");
        menu25.setMenuName(MenuName.SELLING_REGION_CENTRAL);
        menu25.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1191", 1));
        defaultMenuItems.add(new AgMenuItemIndex("1192", 2));
        defaultMenuItems.add(new AgMenuItemIndex("1193", 3));
        menu25.setMenuItems(defaultMenuItems);

        menu26 = new AgUssdMenu();
        menu26.setMenuTitleText("");
        menu26.setMenuName(MenuName.SELLING_REGION_EASTERN);
        menu26.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1201", 1));
        defaultMenuItems.add(new AgMenuItemIndex("1202", 2));
        menu26.setMenuItems(defaultMenuItems);

        menu27 = new AgUssdMenu();
        menu27.setMenuTitleText("1171");
        menu27.setMenuName(MenuName.BUYER_SELLER_LIST);
        menu27.setMenuType(MenuType.SELECT_DYNAMIC_MENU);
        menu27.setMenuItems(new HashSet<>());

        menu28 = new AgUssdMenu();
        menu28.setMenuTitleText("1220");
        menu28.setMenuName(MenuName.CONTACT_SELLER);
        menu28.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1221", 1));
        defaultMenuItems.add(new AgMenuItemIndex("1222", 2));
        menu28.setMenuItems(defaultMenuItems);

        menu29 = new AgUssdMenu();
        menu29.setMenuTitleText("1230");
        menu29.setMenuName(MenuName.COMMODITY_PRICE);
        menu29.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1231", 1));
        defaultMenuItems.add(new AgMenuItemIndex("1232", 2));
        defaultMenuItems.add(new AgMenuItemIndex("1233", 3));
        menu29.setMenuItems(defaultMenuItems);

        menu30 = new AgUssdMenu();
        menu30.setMenuTitleText("1240");
        menu30.setMenuName(MenuName.COMMODITY_PRICE_REGION);
        menu30.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1241", 1));
        defaultMenuItems.add(new AgMenuItemIndex("1242", 2));
        defaultMenuItems.add(new AgMenuItemIndex("1243", 3));
        defaultMenuItems.add(new AgMenuItemIndex("1244", 4));
        menu30.setMenuItems(defaultMenuItems);

        menu31 = new AgUssdMenu();
        menu31.setMenuTitleText("1250");
        menu31.setMenuName(MenuName.COMMODITY_PRICE_DISPLAY);
        menu31.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1251", 1));
        defaultMenuItems.add(new AgMenuItemIndex("1252", 2));
        defaultMenuItems.add(new AgMenuItemIndex("1253", 3));
        defaultMenuItems.add(new AgMenuItemIndex("1254", 4));
        menu31.setMenuItems(defaultMenuItems);

        //////
        menu32 = new AgUssdMenu();
        menu32.setMenuTitleText("1260");
        menu32.setMenuName(MenuName.MARKET_PRICE_REGION);
        menu32.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1261", 1));
        defaultMenuItems.add(new AgMenuItemIndex("1262", 2));
        defaultMenuItems.add(new AgMenuItemIndex("1263", 3));
        defaultMenuItems.add(new AgMenuItemIndex("1264", 4));
        defaultMenuItems.add(new AgMenuItemIndex("1265", 5));
        menu32.setMenuItems(defaultMenuItems);

        menu33 = new AgUssdMenu();
        menu33.setMenuTitleText("");
        menu33.setMenuName(MenuName.MARKET_PRICES);
        menu33.setMenuType(MenuType.SELECT_DYNAMIC_MENU);
        menu33.setMenuItems(new HashSet<>());

        menu34 = new AgUssdMenu();
        menu34.setMenuTitleText("1280");
        menu34.setMenuName(MenuName.MARKETS);
        menu34.setMenuType(MenuType.SELECT_DYNAMIC_MENU);
        menu34.setMenuItems(new HashSet<>()); //Dynamically fetched

        menu35 = new AgUssdMenu();
        menu35.setMenuTitleText("1290");
        menu35.setMenuName(MenuName.MARKET_SERVICES);
        menu35.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1291", 1));
        defaultMenuItems.add(new AgMenuItemIndex("1292", 2));
        defaultMenuItems.add(new AgMenuItemIndex("1293", 3));
        menu35.setMenuItems(defaultMenuItems);

        menu36 = new AgUssdMenu();
        menu36.setMenuTitleText("1300");
        menu36.setMenuName(MenuName.INPUT_TOOLS);
        menu36.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1301", 1));
        defaultMenuItems.add(new AgMenuItemIndex("1302", 2));
        menu36.setMenuItems(defaultMenuItems);

        menu37 = new AgUssdMenu();
        menu37.setMenuTitleText("1310");
        menu37.setMenuName(MenuName.INPUT_TOOLS_LIST);
        menu37.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1311", 1)); //DB fetch
        defaultMenuItems.add(new AgMenuItemIndex("1312", 2));
        defaultMenuItems.add(new AgMenuItemIndex("1313", 3));
        defaultMenuItems.add(new AgMenuItemIndex("1314", 4));
        menu37.setMenuItems(defaultMenuItems);

        menu38 = new AgUssdMenu();
        menu38.setMenuTitleText("1320");
        menu38.setMenuName(MenuName.INPUT_TOOLS_FILTERBY);
        menu38.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1321", 1)); //DB fetch
        defaultMenuItems.add(new AgMenuItemIndex("1322", 2));
        defaultMenuItems.add(new AgMenuItemIndex("1323", 3));
        menu38.setMenuItems(defaultMenuItems);

        menu39 = new AgUssdMenu();
        menu39.setMenuTitleText("1330");
        menu39.setMenuName(MenuName.INPUT_TOOLS_FILTERBY_REGION);
        menu39.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1331", 1)); //DB fetch
        defaultMenuItems.add(new AgMenuItemIndex("1332", 2));
        defaultMenuItems.add(new AgMenuItemIndex("1333", 3));
        defaultMenuItems.add(new AgMenuItemIndex("1334", 4));
        defaultMenuItems.add(new AgMenuItemIndex("1335", 5));
        menu39.setMenuItems(defaultMenuItems);

        menu40 = new AgUssdMenu();
        menu40.setMenuTitleText("1340");
        menu40.setMenuName(MenuName.INPUT_TOOLS_FILTERBY_DISTRICT);
        menu40.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1341", 1)); //DB fetch
        defaultMenuItems.add(new AgMenuItemIndex("1342", 2));
        defaultMenuItems.add(new AgMenuItemIndex("1343", 3));
        menu40.setMenuItems(defaultMenuItems);

        menu41 = new AgUssdMenu();
        menu41.setMenuTitleText("1350");
        menu41.setMenuName(MenuName.INPUT_TOOLS_SELLER_LIST);
        menu41.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1351", 1)); //DB fetch
        defaultMenuItems.add(new AgMenuItemIndex("1352", 2));
        menu41.setMenuItems(defaultMenuItems);

        menu42 = new AgUssdMenu();
        menu42.setMenuTitleText("1360"); //Compose on the fly
        menu42.setMenuName(MenuName.CHECK_OUT_PAGE); //should work for all check out scenarios
        menu42.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1361", 1)); //DB fetch
        defaultMenuItems.add(new AgMenuItemIndex("1362", 2));
        menu42.setMenuItems(defaultMenuItems);

        menu43 = new AgUssdMenu();
        menu43.setMenuTitleText("1153"); //Compose on the fly
        menu43.setMenuName(MenuName.SUCCESS_PAGE); //should work for all check out scenarios
        menu43.setMenuType(MenuType.INPUT_MENU);
        menu43.setMenuItems(new HashSet<>());

        menu44 = new AgUssdMenu();
        menu44.setMenuTitleText("1370"); //Compose on the fly
        menu44.setMenuName(MenuName.LEARNING_CATEGORIES); //should work for all check out scenarios
        menu44.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1371", 1));
        menu44.setMenuItems(defaultMenuItems);

        menu45 = new AgUssdMenu();
        menu45.setMenuTitleText("1380");
        menu45.setMenuName(MenuName.FARMING_TIPS_CATEGORY);
        menu45.setMenuType(MenuType.SELECT_DYNAMIC_MENU);
        menu45.setMenuItems(new HashSet<>());

        menu47 = new AgUssdMenu();
        menu47.setMenuTitleText("1400");
        menu47.setMenuName(MenuName.FARMING_TIPS_TOPICS);
        menu47.setMenuType(MenuType.SELECT_DYNAMIC_MENU);
        menu47.setMenuItems(new HashSet<>());

        menu48 = new AgUssdMenu();
        menu48.setMenuTitleText("1401");
        menu48.setMenuName(MenuName.FARMING_TIPS_CHAPTERS);
        menu48.setMenuType(MenuType.SELECT_DYNAMIC_MENU);
        menu48.setMenuItems(new HashSet<>());

       

        menu46 = new AgUssdMenu();
        menu46.setMenuTitleText("1420"); //Compose on the fly
        menu46.setMenuName(MenuName.WEATHER_REGION); //should work for all check out scenarios
        menu46.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1421", 1));
        defaultMenuItems.add(new AgMenuItemIndex("1422", 2));
        defaultMenuItems.add(new AgMenuItemIndex("1423", 3));
        defaultMenuItems.add(new AgMenuItemIndex("1424", 4));
        menu46.setMenuItems(defaultMenuItems);

        menu49 = new AgUssdMenu();
        menu49.setMenuTitleText(""); //Compose on the fly
        menu49.setMenuName(MenuName.WEATHER_DISTRICT); //should work for all check out scenarios
        menu49.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1431", 1));
        defaultMenuItems.add(new AgMenuItemIndex("1432", 2));
        defaultMenuItems.add(new AgMenuItemIndex("1433", 3));
        menu49.setMenuItems(defaultMenuItems);

        menu50 = new AgUssdMenu();
        menu50.setMenuTitleText("1440"); //Compose on the fly
        menu50.setMenuName(MenuName.ADVICE_TOPICS); //should work for all check out scenarios
        menu50.setMenuType(MenuType.SELECT_STATIC_MENU);
        defaultMenuItems = new HashSet<>();
        defaultMenuItems.add(new AgMenuItemIndex("1441", 1));
        defaultMenuItems.add(new AgMenuItemIndex("1442", 2));
        menu50.setMenuItems(defaultMenuItems);

        menu51 = new AgUssdMenu();
        menu51.setMenuTitleText("1450"); //Compose on the fly
        menu51.setMenuName(MenuName.ADVICE_QUERY);
        menu51.setMenuType(MenuType.INPUT_MENU);
        menu51.setMenuItems(new HashSet<>());

        menu52 = new AgUssdMenu();
        menu52.setMenuTitleText("1451");
        menu52.setMenuName(MenuName.BUYER_CATEGORY);
        menu52.setMenuType(MenuType.SELECT_DYNAMIC_MENU);
        menu52.setMenuItems(new HashSet<>()); //Dynamically fetched

        //Register new account
        menu53 = new AgUssdMenu();
        menu53.setMenuTitleText("1610");
        menu53.setMenuName(MenuName.REGISTER_NAME);
        menu53.setMenuType(MenuType.INPUT_MENU);
        menu53.setMenuItems(new HashSet<>());

        menu54 = new AgUssdMenu();
        menu54.setMenuTitleText("1611");
        menu54.setMenuName(MenuName.REGISTER_DISTRICT);
        menu54.setMenuType(MenuType.INPUT_MENU);
        menu54.setMenuItems(new HashSet<>());

        menu55 = new AgUssdMenu();
        menu55.setMenuTitleText("");
        menu55.setMenuName(MenuName.ITEM_SUBCATEGORIES);
        menu55.setMenuType(MenuType.SELECT_DYNAMIC_MENU);
        menu55.setMenuItems(new HashSet<>());

        menu56 = new AgUssdMenu();
        menu56.setMenuTitleText("1030");
        menu56.setMenuName(MenuName.CUSTOM_DISTRICT);
        menu56.setMenuType(MenuType.INPUT_MENU);
        menu56.setMenuItems(new HashSet<>());

        menu57 = new AgUssdMenu();
        menu57.setMenuTitleText("");
        //menu57.setMenuTitleText("1190");
        menu57.setMenuName(MenuName.MARKET_DISTRICT_PRICES);
        menu57.setMenuType(MenuType.SELECT_DYNAMIC_MENU);
        menu57.setMenuItems(new HashSet<>());

        menu58 = new AgUssdMenu();
        menu58.setMenuTitleText("1510");
        menu58.setMenuName(MenuName.NO_RECORDS);
        menu58.setMenuType(MenuType.INPUT_MENU);
        menu58.setMenuItems(new HashSet<>());

        menu59 = new AgUssdMenu();
        menu59.setMenuTitleText("1190");
        menu59.setMenuName(MenuName.BUYER_SELLER_DISTRICTS);
        menu59.setMenuType(MenuType.SELECT_DYNAMIC_MENU);
        menu59.setMenuItems(new HashSet<>());

        menu60 = new AgUssdMenu();
        menu60.setMenuTitleText("1153");
        menu60.setMenuName(MenuName.CONTACT_SELLER_MESSAGE);
        menu60.setMenuType(MenuType.INPUT_MENU);
        menu60.setMenuItems(new HashSet<>());

        menu61 = new AgUssdMenu();
        menu61.setMenuTitleText("1281");
        menu61.setMenuName(MenuName.MATCHED_BUYER_LIST);
        menu61.setMenuType(MenuType.SELECT_DYNAMIC_MENU);
        menu61.setMenuItems(new HashSet<>());

        menu62 = new AgUssdMenu();
        menu62.setMenuTitleText("1700");
        menu62.setMenuName(MenuName.MARKET_PRICES_END);
        menu62.setMenuType(MenuType.INPUT_MENU);
        menu62.setMenuItems(new HashSet<>());

        menu63 = new AgUssdMenu();
        menu63.setMenuTitleText("1612");
        menu63.setMenuName(MenuName.SUCCESS_REGISTRATION);
        menu63.setMenuType(MenuType.INPUT_MENU);
        menu63.setMenuItems(new HashSet<>());
        
         menu64 = new AgUssdMenu();
        menu64.setMenuTitleText("");
        menu64.setMenuName(MenuName.FARMING_TIPS_CONTENT);
        menu64.setMenuType(MenuType.INPUT_MENU);
        menu64.setMenuItems(new HashSet<>());
        
        
        menu65 = new AgUssdMenu();
        menu65.setMenuTitleText("1190");
        menu65.setMenuName(MenuName.SELECT_DISTRICT);
        menu65.setMenuType(MenuType.SELECT_DYNAMIC_MENU);
        menu65.setMenuItems(new HashSet<>());
        

//        menu45 = new AgUssdMenu();
//        menu45.setMenuTitleText("1370"); 
//        menu45.setMenuName(MenuName.INPUT_TOOLS_FILTERBY_ALL);
//        menu45.setMenuType(MenuType.SELECT_STATIC_MENU);
//        defaultMenuItems = new HashSet<>();
//        defaultMenuItems.add(new AgMenuItemIndex("1371", 1)); //DB fetch
//        defaultMenuItems.add(new AgMenuItemIndex("1372", 2));
//        menu45.setMenuItems(defaultMenuItems);
        ///////
        startMenu = new AgUssdMenu();
        startMenu.setMenuTitleText("");
        startMenu.setMenuName(MenuName.START); //on first dial
        startMenu.setMenuType(MenuType.SELECT_STATIC_MENU);
        startMenu.setMenuItems(new HashSet<>());

        endMenu = new AgUssdMenu();
        endMenu.setMenuTitleText("END");
        endMenu.setMenuName(MenuName.END);// on quit dial
        endMenu.setMenuType(MenuType.SELECT_STATIC_MENU); //doesn't matter
        endMenu.setMenuItems(new HashSet<>());

        errorMenu = new AgUssdMenu();
        errorMenu.setMenuTitleText("ERROR");
        errorMenu.setMenuName(MenuName.ERROR_MENU);// 
        errorMenu.setMenuType(MenuType.INPUT_MENU);
        errorMenu.setMenuItems(new HashSet<>());

        maintenanceMenu = new AgUssdMenu();
        maintenanceMenu.setMenuTitleText("MAINTENANCE");
        maintenanceMenu.setMenuName(MenuName.UNDER_MAINTENANCE);
        maintenanceMenu.setMenuType(MenuType.INPUT_MENU);
        maintenanceMenu.setMenuItems(new HashSet<>());

        menus.add(menu1);
        menus.add(menu2);
        menus.add(menu3);
        menus.add(menu4);
        menus.add(menu5);
        menus.add(menu6);
        menus.add(menu7);
        menus.add(menu8);
        menus.add(menu9);
        menus.add(menu10);
        menus.add(menu11);
        menus.add(menu12);
        menus.add(menu13);
        menus.add(menu14);
        menus.add(menu15);
        menus.add(menu16);
        menus.add(menu17);
        menus.add(menu18);
        menus.add(menu19);
        menus.add(menu20);
        menus.add(menu21);
        menus.add(menu22);
        menus.add(menu23);
        menus.add(menu24);
        menus.add(menu25);
        menus.add(menu26);
        menus.add(menu27);
        menus.add(menu28);
        menus.add(menu29);
        menus.add(menu30);
        menus.add(menu31);
        menus.add(menu32);
        menus.add(menu33);
        menus.add(menu34);
        menus.add(menu35);
        menus.add(menu36);
        menus.add(menu37);
        menus.add(menu38);
        menus.add(menu39);
        menus.add(menu40);
        menus.add(menu41);
        menus.add(menu42);
        menus.add(menu43);
        menus.add(menu44);
        menus.add(menu45);
        menus.add(menu46);
        menus.add(menu47);
        menus.add(menu48);
        menus.add(menu49);
        menus.add(menu50);
        menus.add(menu51);
        menus.add(menu52);
        menus.add(menu53);
        menus.add(menu54);
        menus.add(menu55);
        menus.add(menu56);
        menus.add(menu57);
        menus.add(menu58);
        menus.add(menu59);
        menus.add(menu60);
        menus.add(menu61);
        menus.add(menu62);
        menus.add(menu63);
        menus.add(menu64);
        menus.add(menu65);

        menus.add(maintenanceMenu);
        menus.add(errorMenu);
        menus.add(startMenu);
        menus.add(endMenu);

        customHibernate.saveBulk(menus);
    }

    /**
     *
     * @return @throws MyCustomException
     */
    private boolean menusInitialised() throws MyCustomException {

        int count = customHibernate.countRows(AgUssdMenu.class, null).intValue();

        if (count > 0) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;

    }
}
