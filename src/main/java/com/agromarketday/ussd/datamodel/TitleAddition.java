package com.agromarketday.ussd.datamodel;

/**
 *
 * @author smallgod
 */
public class TitleAddition {

    private boolean isPrefix;
    private String addition;

    public TitleAddition(boolean isPrefix, String addition) {
        this.isPrefix = isPrefix;
        this.addition = addition;
    }

    public boolean isIsPrefix() {
        return isPrefix;
    }

    public void setIsPrefix(boolean isPrefix) {
        this.isPrefix = isPrefix;
    }

    public String getAddition() {
        return addition;
    }

    public void setAddition(String addition) {
        this.addition = addition;
    }
}
