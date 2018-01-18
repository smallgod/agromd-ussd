package com.agromarketday.ussd.config;

import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.util.FileUtilities;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author smallgod
 */
public class UssdMenuConfig {

    private final Map<String, Map<String, String>> menuTree;

    public UssdMenuConfig(String ussdMenuDir) throws MyCustomException {

        this.menuTree = loadAllUssdMenus(ussdMenuDir);
    }

    private Map<String, Map<String, String>> loadAllUssdMenus(final String ussdMenuDir) throws MyCustomException {

        Map<String, Map<String, String>> menu = new HashMap<>();

        File dir = new File(ussdMenuDir + File.separator);
        if (dir.isDirectory()) {

            for (File file : dir.listFiles()) {

                if (file.isFile()) {

                    String langCode = FileUtilities.removeFileExtension(file.getName());
                    Map<String, String> defaultLangMenuTree = FileUtilities.readTextFile(file);

                    menu.put(langCode, defaultLangMenuTree);
                }
            }
        }
        return menu;
    }

    /**
     * Retrieve USSD menu from memory corresponding to given language
     *
     * @param langCode
     * @return
     */
    public Map<String, String> getUssdMenu(String langCode) {

        return (this.menuTree.get(langCode));
    }
}
