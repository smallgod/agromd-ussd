//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.04.07 at 11:55:46 AM EAT 
//


package com.agromarketday.ussd.datamodel.jaxb.config;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for statustype.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="statustype"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="CREATED"/&gt;
 *     &lt;enumeration value="PENDING"/&gt;
 *     &lt;enumeration value="COMPLETED"/&gt;
 *     &lt;enumeration value="FAILED"/&gt;
 *     &lt;enumeration value="EXPIRED"/&gt;
 *     &lt;enumeration value="UNKNOWN"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "statustype")
@XmlEnum
public enum Statustype {

    CREATED,
    PENDING,
    COMPLETED,
    FAILED,
    EXPIRED,
    UNKNOWN;

    public String value() {
        return name();
    }

    public static Statustype fromValue(String v) {
        return valueOf(v);
    }

}
