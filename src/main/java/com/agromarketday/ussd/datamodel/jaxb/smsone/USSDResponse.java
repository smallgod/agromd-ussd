//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.11.08 at 06:27:23 PM EAT 
//


package com.agromarketday.ussd.datamodel.jaxb.smsone;

import com.agromarketday.ussd.util.XMLObject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TransactionId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="TransactionTime" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="USSDResponseString" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="USSDAction" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "transactionId",
    "transactionTime",
    "ussdResponseString",
    "ussdAction"
})
@XmlRootElement(name = "USSDResponse")
public class USSDResponse implements XMLObject{

    @XmlElement(name = "TransactionId", required = true)
    protected String transactionId;
    @XmlElement(name = "TransactionTime", required = true)
    protected String transactionTime;
    @XmlElement(name = "USSDResponseString", required = true)
    protected String ussdResponseString;
    @XmlElement(name = "USSDAction", required = true)
    protected String ussdAction;

    /**
     * Gets the value of the transactionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the value of the transactionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionId(String value) {
        this.transactionId = value;
    }

    /**
     * Gets the value of the transactionTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionTime() {
        return transactionTime;
    }

    /**
     * Sets the value of the transactionTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionTime(String value) {
        this.transactionTime = value;
    }

    /**
     * Gets the value of the ussdResponseString property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUSSDResponseString() {
        return ussdResponseString;
    }

    /**
     * Sets the value of the ussdResponseString property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUSSDResponseString(String value) {
        this.ussdResponseString = value;
    }

    /**
     * Gets the value of the ussdAction property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUSSDAction() {
        return ussdAction;
    }

    /**
     * Sets the value of the ussdAction property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUSSDAction(String value) {
        this.ussdAction = value;
    }

    @Override
    public XMLObject getXMLObject() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}