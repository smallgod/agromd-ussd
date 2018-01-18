package com.agromarketday.ussd.datamodel;

import com.agromarketday.ussd.constant.MenuName;
import com.agromarketday.ussd.constant.NavigationBar;
import com.agromarketday.ussd.datamodel.json.MenuHistory;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author smallgod
 */
public class UssdMenuComponents {

    private Set<AgMenuItemIndex> menuItems;
    private List<MenuHistory.MenuOption> menuOptions;
    private String menuTitle;
    private NavigationBar navBar;
    private MenuName responseMenu;
    private int screenNum;
    private int totalScreens;
    private boolean isIsEnd;

    public UssdMenuComponents() {
        this.menuOptions = new LinkedList<>();
        this.screenNum = 1;
        this.totalScreens = 1;
        this.isIsEnd = false;
    }

    public UssdMenuComponents(Set<AgMenuItemIndex> menuItems,
            List<MenuHistory.MenuOption> menuOptions,
            String menuTitle, NavigationBar navBar, MenuName responseMenu) {

        this.menuItems = menuItems;
        this.menuOptions = menuOptions;
        this.menuTitle = menuTitle;
        this.navBar = navBar;
        this.responseMenu = responseMenu;
        this.isIsEnd = false;
    }

    public Set<AgMenuItemIndex> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(Set<AgMenuItemIndex> menuItems) {
        this.menuItems = menuItems;
    }

    public String getMenuTitle() {
        return menuTitle;
    }

    public void setMenuTitle(String menuTitle) {
        this.menuTitle = menuTitle;
    }

    public NavigationBar getNavBar() {
        return navBar;
    }

    public void setNavBar(NavigationBar navBar) {
        this.navBar = navBar;
    }

    public MenuName getResponseMenu() {
        return responseMenu;
    }

    public void setResponseMenu(MenuName responseMenu) {
        this.responseMenu = responseMenu;
    }

    public List<MenuHistory.MenuOption> getMenuOptions() {
        return menuOptions;
    }

    public void setMenuOptions(List<MenuHistory.MenuOption> menuOptions) {
        this.menuOptions = menuOptions;
    }

    public int getScreenNum() {
        return screenNum;
    }

    public void setScreenNum(int screenNum) {
        this.screenNum = screenNum;
    }

    public int getTotalScreens() {
        return totalScreens;
    }

    public void setTotalScreens(int totalScreens) {
        this.totalScreens = totalScreens;
    }

    public boolean isIsIsEnd() {
        return isIsEnd;
    }

    public void setIsIsEnd(boolean isIsEnd) {
        this.isIsEnd = isIsEnd;
    }

}
