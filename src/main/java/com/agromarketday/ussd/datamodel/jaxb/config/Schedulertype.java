//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.04.07 at 11:55:46 AM EAT 
//


package com.agromarketday.ussd.datamodel.jaxb.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for schedulertype complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="schedulertype"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;all&gt;
 *         &lt;element name="triggername" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="jobname" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="groupname" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="interval" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "schedulertype", propOrder = {

})
public class Schedulertype {

    @XmlElement(required = true)
    protected String triggername;
    @XmlElement(required = true)
    protected String jobname;
    @XmlElement(required = true)
    protected String groupname;
    protected int interval;

    /**
     * Gets the value of the triggername property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTriggername() {
        return triggername;
    }

    /**
     * Sets the value of the triggername property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTriggername(String value) {
        this.triggername = value;
    }

    /**
     * Gets the value of the jobname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJobname() {
        return jobname;
    }

    /**
     * Sets the value of the jobname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJobname(String value) {
        this.jobname = value;
    }

    /**
     * Gets the value of the groupname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupname() {
        return groupname;
    }

    /**
     * Sets the value of the groupname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupname(String value) {
        this.groupname = value;
    }

    /**
     * Gets the value of the interval property.
     * 
     */
    public int getInterval() {
        return interval;
    }

    /**
     * Sets the value of the interval property.
     * 
     */
    public void setInterval(int value) {
        this.interval = value;
    }

}
