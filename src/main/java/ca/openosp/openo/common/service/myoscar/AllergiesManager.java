//CHECKSTYLE:OFF
/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */


package ca.openosp.openo.common.service.myoscar;

import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.logging.log4j.Logger;
import ca.openosp.openo.common.dao.AllergyDao;
import ca.openosp.openo.common.dao.SentToPHRTrackingDao;
import ca.openosp.openo.common.model.Allergy;
import ca.openosp.openo.common.model.SentToPHRTracking;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.MiscUtils;
import ca.openosp.openo.ehrutil.SpringUtils;
import ca.openosp.openo.ehrutil.XmlUtils;
import org.w3c.dom.Document;

public final class AllergiesManager {
    private static final Logger logger = MiscUtils.getLogger();
    private static final String OSCAR_ALLERGIES_DATA_TYPE = "ALLERGY";
    private static final SentToPHRTrackingDao sentToPHRTrackingDao = (SentToPHRTrackingDao) SpringUtils.getBean(SentToPHRTrackingDao.class);

    public static Document toXml(Allergy allergy) throws ParserConfigurationException {
        Document doc = XmlUtils.newDocument("Allergy");

        String temp = StringUtils.trimToNull(allergy.getDescription());
        if (temp != null) XmlUtils.appendChildToRootIgnoreNull(doc, "Description", temp);

        temp = StringUtils.trimToNull(allergy.getReaction());
        if (temp != null) XmlUtils.appendChildToRootIgnoreNull(doc, "Reaction", temp);

        Integer tempInt = allergy.getHiclSeqno();
        if (tempInt != null) XmlUtils.appendChildToRoot(doc, "HiclSeqno", tempInt.toString());

        tempInt = allergy.getHicSeqno();
        if (tempInt != null) XmlUtils.appendChildToRoot(doc, "HicSeqno", tempInt.toString());

        tempInt = allergy.getAgcsp();
        if (tempInt != null) XmlUtils.appendChildToRoot(doc, "Agcsp", tempInt.toString());

        tempInt = allergy.getAgccs();
        if (tempInt != null) XmlUtils.appendChildToRoot(doc, "Agccs", tempInt.toString());

        tempInt = allergy.getTypeCode();
        if (tempInt != null) XmlUtils.appendChildToRoot(doc, "TypeCode", tempInt.toString());

        temp = StringUtils.trimToNull(allergy.getDrugrefId());
        if (temp != null) XmlUtils.appendChildToRootIgnoreNull(doc, "DrugrefId", temp);

        if (allergy.getStartDate() != null) {
            temp = DateFormatUtils.ISO_DATETIME_FORMAT.format(allergy.getStartDate());
            XmlUtils.appendChildToRootIgnoreNull(doc, "StartDate", temp);
        }

        temp = StringUtils.trimToNull(allergy.getAgeOfOnset());
        if (temp != null) XmlUtils.appendChildToRootIgnoreNull(doc, "AgeOfOnset", temp);

        temp = StringUtils.trimToNull(allergy.getSeverityOfReaction());
        if (temp != null) {
            // not too worries about i18n, just sending something better than a number
            if ("1".equals(temp)) temp = "Mild";
            else if ("2".equals(temp)) temp = "Moderate";
            else if ("3".equals(temp)) temp = "Severe";
            else if ("4".equals(temp)) temp = "Unknown";

            XmlUtils.appendChildToRootIgnoreNull(doc, "SeverityOfReaction", temp);
        }

        temp = StringUtils.trimToNull(allergy.getOnsetOfReaction());
        if (temp != null) {
            // not too worries about i18n, just sending something better than a number
            if ("1".equals(temp)) temp = "Immediate";
            else if ("2".equals(temp)) temp = "Gradual";
            else if ("3".equals(temp)) temp = "Slow";
            else if ("4".equals(temp)) temp = "Unknown";

            XmlUtils.appendChildToRootIgnoreNull(doc, "OnsetOfReaction", temp);
        }

        temp = StringUtils.trimToNull(allergy.getRegionalIdentifier());
        if (temp != null) XmlUtils.appendChildToRootIgnoreNull(doc, "RegionalIdentifier", temp);

        temp = StringUtils.trimToNull(allergy.getLifeStage());
        if (temp != null) XmlUtils.appendChildToRootIgnoreNull(doc, "LifeStage", temp);

        return (doc);
    }


}
