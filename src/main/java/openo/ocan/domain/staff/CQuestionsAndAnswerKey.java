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


package openo.ocan.domain.staff;

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
 *         &lt;element ref="{}CNEED__Q1___0_No_problem__1__No_Moderate_problem_due_to_help_given__2_Serious_problem__9__Not_Kn"/>
 *         &lt;element ref="{}CHELP__Q2_and_3_a_b___0_None__1__Low_Help__2_Moderate_help__3__High_help__9___Unknown"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "cneedq10NoProblem1NoModerateProblemDueToHelpGiven2SeriousProblem9NotKn",
        "chelpq2And3AB0None1LowHelp2ModerateHelp3HighHelp9Unknown"
})
@XmlRootElement(name = "CQuestions_and_Answer_Key")
public class CQuestionsAndAnswerKey {

    @XmlElement(name = "CNEED__Q1___0_No_problem__1__No_Moderate_problem_due_to_help_given__2_Serious_problem__9__Not_Kn", required = true)
    protected CNEEDQ10NoProblem1NoModerateProblemDueToHelpGiven2SeriousProblem9NotKn cneedq10NoProblem1NoModerateProblemDueToHelpGiven2SeriousProblem9NotKn;
    @XmlElement(name = "CHELP__Q2_and_3_a_b___0_None__1__Low_Help__2_Moderate_help__3__High_help__9___Unknown", required = true)
    protected CHELPQ2And3AB0None1LowHelp2ModerateHelp3HighHelp9Unknown chelpq2And3AB0None1LowHelp2ModerateHelp3HighHelp9Unknown;

    /**
     * Gets the value of the cneedq10NoProblem1NoModerateProblemDueToHelpGiven2SeriousProblem9NotKn property.
     *
     * @return possible object is
     * {@link CNEEDQ10NoProblem1NoModerateProblemDueToHelpGiven2SeriousProblem9NotKn }
     */
    public CNEEDQ10NoProblem1NoModerateProblemDueToHelpGiven2SeriousProblem9NotKn getCNEEDQ10NoProblem1NoModerateProblemDueToHelpGiven2SeriousProblem9NotKn() {
        return cneedq10NoProblem1NoModerateProblemDueToHelpGiven2SeriousProblem9NotKn;
    }

    /**
     * Sets the value of the cneedq10NoProblem1NoModerateProblemDueToHelpGiven2SeriousProblem9NotKn property.
     *
     * @param value allowed object is
     *              {@link CNEEDQ10NoProblem1NoModerateProblemDueToHelpGiven2SeriousProblem9NotKn }
     */
    public void setCNEEDQ10NoProblem1NoModerateProblemDueToHelpGiven2SeriousProblem9NotKn(CNEEDQ10NoProblem1NoModerateProblemDueToHelpGiven2SeriousProblem9NotKn value) {
        this.cneedq10NoProblem1NoModerateProblemDueToHelpGiven2SeriousProblem9NotKn = value;
    }

    /**
     * Gets the value of the chelpq2And3AB0None1LowHelp2ModerateHelp3HighHelp9Unknown property.
     *
     * @return possible object is
     * {@link CHELPQ2And3AB0None1LowHelp2ModerateHelp3HighHelp9Unknown }
     */
    public CHELPQ2And3AB0None1LowHelp2ModerateHelp3HighHelp9Unknown getCHELPQ2And3AB0None1LowHelp2ModerateHelp3HighHelp9Unknown() {
        return chelpq2And3AB0None1LowHelp2ModerateHelp3HighHelp9Unknown;
    }

    /**
     * Sets the value of the chelpq2And3AB0None1LowHelp2ModerateHelp3HighHelp9Unknown property.
     *
     * @param value allowed object is
     *              {@link CHELPQ2And3AB0None1LowHelp2ModerateHelp3HighHelp9Unknown }
     */
    public void setCHELPQ2And3AB0None1LowHelp2ModerateHelp3HighHelp9Unknown(CHELPQ2And3AB0None1LowHelp2ModerateHelp3HighHelp9Unknown value) {
        this.chelpq2And3AB0None1LowHelp2ModerateHelp3HighHelp9Unknown = value;
    }

}