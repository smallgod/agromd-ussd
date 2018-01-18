package com.agromarketday.ussd.util;

import com.agromarketday.ussd.datamodel.AgMenuItemIndex;
import java.util.Comparator;

/**
 *
 * @author smallgod
 */
public class ItemNodeComparator implements Comparator<AgMenuItemIndex> {

    // I don't know why this isn't in Long...
    private static int compare(long a, long b) {
        return a < b ? -1 : a > b ? 1 : 0;
    }

    @Override
    public int compare(AgMenuItemIndex o1, AgMenuItemIndex o2) {
        // the value 0 if this Integer is equal to the argument Integer; 
        //a value less than 0 if this Integer is numerically less than the argument Integer; 
        //and a value greater than 0 if this Integer is numerically greater than the argument Integer (signed comparison).
        //int startComparison = compare(x.timeStarted, y.timeStarted);
        //return startComparison != 0 ? startComparison : compare(x.timeEnded, y.timeEnded);
        if (this == null || o2 == null) {
            return 3;
        }

        Integer x = o1.getMenuItemIndex();
        Integer y = o2.getMenuItemIndex();

        return (x.compareTo(y));
    }

}
