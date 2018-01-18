/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agromarketday.ussd.datamodel;

import com.agromarketday.ussd.datamodel.json.MenuHistory;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 *
 * @author smallgod
 */
public class DynamicMenuItem {

    private Set<AgMenuItemIndex> menuItems;
    private List<MenuHistory.MenuOption> menuOptions;

    public DynamicMenuItem(Set<AgMenuItemIndex> menuItems,
            List<MenuHistory.MenuOption> menuOptions) {
        this.menuItems = menuItems;
        this.menuOptions = menuOptions;
    }

    public Set<AgMenuItemIndex> getMenuItems() {
        return Collections.unmodifiableSet(menuItems);
    }

    public void setMenuItems(Set<AgMenuItemIndex> menuItems) {
        this.menuItems = menuItems;
    }

    public List<MenuHistory.MenuOption> getMenuOptions() {
        return Collections.unmodifiableList(menuOptions);
    }

    public void setMenuOptions(List<MenuHistory.MenuOption> menuOptions) {
        this.menuOptions = menuOptions;
    }

}
