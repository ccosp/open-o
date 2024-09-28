//CHECKSTYLE:OFF
/**
 * Copyright (c) 2008-2012 Indivica Inc.
 * <p>
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

/*
 * HL7Handler
 * Upload handler
 *
 */
package ca.openosp.openo.oscarLab.ca.all.upload.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import ca.openosp.openo.Misc;
import ca.openosp.openo.ehrutil.MiscUtils;
import org.apache.logging.log4j.Logger;
import ca.openosp.openo.common.dao.Hl7TextInfoDao;
import ca.openosp.openo.common.model.Hl7TextInfo;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.SpringUtils;

import ca.openosp.openo.oscarLab.ca.all.parsers.Factory;
import ca.openosp.openo.oscarLab.ca.all.upload.MessageUploader;
import ca.openosp.openo.oscarLab.ca.all.util.Utilities;

/**
 *
 */
public class TDISHandler implements MessageHandler {

    Logger logger = MiscUtils.getLogger();

    public TDISHandler() {
        logger.info("NEW TDISHandler UPLOAD HANDLER instance just instantiated. ");
    }

    public String parse(LoggedInInfo loggedInInfo, String serviceName, String fileName, int fileId, String ipAddr) {
        logger.info("ABOUT TO PARSE!");


        int i = 0;
        try {
            ArrayList messages = Utilities.separateMessages(fileName);

            for (i = 0; i < messages.size(); i++) {

                String msg = (String) messages.get(i);


                MessageUploader.routeReport(loggedInInfo, serviceName, "TDIS", msg, fileId);

            }

            // Since the gdml labs show more than one lab on the same page when
            // grouped
            // by accession number their abnormal status must be updated to
            // reflect the
            // other labs that they are grouped with aswell
            updateLabStatus(messages.size());
            logger.info("Parsed OK");
        } catch (Exception e) {
            MessageUploader.clean(fileId);
            logger.error("Could not upload message", e);
            return null;
        }
        return ("success");

    }

    // recheck the abnormal status of the last 'n' labs
    private void updateLabStatus(int n) {
        Hl7TextInfoDao hl7TextInfoDao = (Hl7TextInfoDao) SpringUtils.getBean(Hl7TextInfoDao.class);
        List<Hl7TextInfo> labList = hl7TextInfoDao.getAllLabsByLabNumberResultStatus();
        Collections.sort(labList, Collections.reverseOrder(new Comparator<Hl7TextInfo>() {
            public int compare(Hl7TextInfo o1, Hl7TextInfo o2) {
                return o1.getId().compareTo(o2.getId());
            }
        }));
        ListIterator<Hl7TextInfo> iter = labList.listIterator();

        while (iter.hasNext() && n > 0) {
            Hl7TextInfo lab = iter.next();
            if (!Misc.getString(lab.getResultStatus()).equals("A")) {
                ca.openosp.openo.oscarLab.ca.all.parsers.MessageHandler h = Factory.getHandler(((Integer) lab.getLabNumber()).toString());

                int i = 0;
                int j = 0;
                String resultStatus = "";
                while (resultStatus.equals("") && i < h.getOBRCount()) {
                    j = 0;
                    while (resultStatus.equals("") && j < h.getOBXCount(i)) {
                        logger.info("obr(" + i + ") obx(" + j + ") abnormal ? : " + h.getOBXAbnormalFlag(i, j));
                        if (h.isOBXAbnormal(i, j)) {
                            resultStatus = "A";
                            hl7TextInfoDao.updateResultStatusByLabId("A", lab.getLabNumber());

                        }
                        j++;
                    }
                    i++;
                }
            }
            n--;
        }
    }

}
