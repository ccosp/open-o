//CHECKSTYLE:OFF
/**
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * <p>
 * This software was written for
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-793 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.05.24 at 10:52:14 PM EDT 
//


package oscar.ocan.domain.staff;

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
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}CCharges_withdrawn"/>
 *         &lt;element ref="{}CStay_of_proceedings"/>
 *         &lt;element ref="{}CAwaiting_sentence"/>
 *         &lt;element ref="{}CNCR"/>
 *         &lt;element ref="{}CConditional_Discharge"/>
 *         &lt;element ref="{}CConditional_Sentence"/>
 *         &lt;element ref="{}CRestraining_order"/>
 *         &lt;element ref="{}CPeace_bond"/>
 *         &lt;element ref="{}CSuspended_sentence"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "cChargesWithdrawn",
        "cStayOfProceedings",
        "cAwaitingSentence",
        "cncr",
        "cConditionalDischarge",
        "cConditionalSentence",
        "cRestrainingOrder",
        "cPeaceBond",
        "cSuspendedSentence"
})
@XmlRootElement(name = "COutcomes")
public class COutcomes {

    @XmlElement(name = "CCharges_withdrawn", required = true)
    protected String cChargesWithdrawn;
    @XmlElement(name = "CStay_of_proceedings", required = true)
    protected String cStayOfProceedings;
    @XmlElement(name = "CAwaiting_sentence", required = true)
    protected String cAwaitingSentence;
    @XmlElement(name = "CNCR", required = true)
    protected String cncr;
    @XmlElement(name = "CConditional_Discharge", required = true)
    protected String cConditionalDischarge;
    @XmlElement(name = "CConditional_Sentence", required = true)
    protected String cConditionalSentence;
    @XmlElement(name = "CRestraining_order", required = true)
    protected String cRestrainingOrder;
    @XmlElement(name = "CPeace_bond", required = true)
    protected String cPeaceBond;
    @XmlElement(name = "CSuspended_sentence", required = true)
    protected String cSuspendedSentence;

    /**
     * Gets the value of the cChargesWithdrawn property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCChargesWithdrawn() {
        return cChargesWithdrawn;
    }

    /**
     * Sets the value of the cChargesWithdrawn property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCChargesWithdrawn(String value) {
        this.cChargesWithdrawn = value;
    }

    /**
     * Gets the value of the cStayOfProceedings property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCStayOfProceedings() {
        return cStayOfProceedings;
    }

    /**
     * Sets the value of the cStayOfProceedings property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCStayOfProceedings(String value) {
        this.cStayOfProceedings = value;
    }

    /**
     * Gets the value of the cAwaitingSentence property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCAwaitingSentence() {
        return cAwaitingSentence;
    }

    /**
     * Sets the value of the cAwaitingSentence property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCAwaitingSentence(String value) {
        this.cAwaitingSentence = value;
    }

    /**
     * Gets the value of the cncr property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCNCR() {
        return cncr;
    }

    /**
     * Sets the value of the cncr property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCNCR(String value) {
        this.cncr = value;
    }

    /**
     * Gets the value of the cConditionalDischarge property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCConditionalDischarge() {
        return cConditionalDischarge;
    }

    /**
     * Sets the value of the cConditionalDischarge property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCConditionalDischarge(String value) {
        this.cConditionalDischarge = value;
    }

    /**
     * Gets the value of the cConditionalSentence property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCConditionalSentence() {
        return cConditionalSentence;
    }

    /**
     * Sets the value of the cConditionalSentence property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCConditionalSentence(String value) {
        this.cConditionalSentence = value;
    }

    /**
     * Gets the value of the cRestrainingOrder property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCRestrainingOrder() {
        return cRestrainingOrder;
    }

    /**
     * Sets the value of the cRestrainingOrder property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCRestrainingOrder(String value) {
        this.cRestrainingOrder = value;
    }

    /**
     * Gets the value of the cPeaceBond property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCPeaceBond() {
        return cPeaceBond;
    }

    /**
     * Sets the value of the cPeaceBond property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCPeaceBond(String value) {
        this.cPeaceBond = value;
    }

    /**
     * Gets the value of the cSuspendedSentence property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCSuspendedSentence() {
        return cSuspendedSentence;
    }

    /**
     * Sets the value of the cSuspendedSentence property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCSuspendedSentence(String value) {
        this.cSuspendedSentence = value;
    }

}