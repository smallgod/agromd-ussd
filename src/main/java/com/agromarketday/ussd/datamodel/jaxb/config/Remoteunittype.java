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
 * <p>Java class for remoteunittype complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="remoteunittype"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;all&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="jsonurl" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="xmlurl" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="previewurl" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="username" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "remoteunittype", propOrder = {

})
public class Remoteunittype {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String jsonurl;
    @XmlElement(required = true)
    protected String xmlurl;
    @XmlElement(required = true)
    protected String previewurl;
    @XmlElement(required = true)
    protected String username;
    @XmlElement(required = true)
    protected String password;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the jsonurl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJsonurl() {
        return jsonurl;
    }

    /**
     * Sets the value of the jsonurl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJsonurl(String value) {
        this.jsonurl = value;
    }

    /**
     * Gets the value of the xmlurl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlurl() {
        return xmlurl;
    }

    /**
     * Sets the value of the xmlurl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlurl(String value) {
        this.xmlurl = value;
    }

    /**
     * Gets the value of the previewurl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPreviewurl() {
        return previewurl;
    }

    /**
     * Sets the value of the previewurl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPreviewurl(String value) {
        this.previewurl = value;
    }

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

}