//CHECKSTYLE:OFF
/**
 * Copyright (c) 2008-2012 Indivica Inc.
 * <p>
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package ca.openosp.openo.oscarLab.ca.all.upload.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import ca.openosp.openo.ehrutil.MiscUtils;
import org.apache.logging.log4j.Logger;
import ca.openosp.openo.common.dao.Hl7TextInfoDao;
import ca.openosp.openo.common.model.Hl7TextInfo;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.SpringUtils;

import ca.openosp.openo.oscarLab.ca.all.parsers.Factory;
import ca.openosp.openo.oscarLab.ca.all.upload.MessageUploader;
import ca.openosp.openo.oscarLab.ca.all.util.MEDVUEUtilities;

public class MEDVUEHandler implements MessageHandler {

    Logger logger = MiscUtils.getLogger();

    public MEDVUEHandler() {
        logger.info("NEW MEDVUEHandler UPLOAD HANDLER instance just instantiated. ");
    }

    public String parse(LoggedInInfo loggedInInfo, String serviceName, String fileName, int fileId, String ipAddr) {
        logger.info("ABOUT TO PARSE!");

        MEDVUEUtilities u = new MEDVUEUtilities();

        int i = 0;
        try {
            ArrayList<String> messages = u.separateMessages(fileName);

            for (i = 0; i < messages.size(); i++) {

                String msg = messages.get(i);


                MessageUploader.routeReport(loggedInInfo, serviceName, "MEDVUE", msg, fileId);

            }

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
        ListIterator<Hl7TextInfo> iter = labList.listIterator();


        while (iter.hasNext() && n > 0) {
            if (!iter.next().getResultStatus().equals("A")) {
                ca.openosp.openo.oscarLab.ca.all.parsers.MessageHandler h = Factory.getHandler(((Integer) iter.next().getLabNumber()).toString());

                int i = 0;
                int j = 0;
                String resultStatus = "";
                while (resultStatus.equals("") && i < h.getOBRCount()) {
                    j = 0;
                    while (resultStatus.equals("") && j < h.getOBXCount(i)) {
                        logger.info("obr(" + i + ") obx(" + j + ") abnormal ? : " + h.getOBXAbnormalFlag(i, j));
                        if (h.isOBXAbnormal(i, j)) {
                            resultStatus = "A";
                            hl7TextInfoDao.updateResultStatusByLabId("A", iter.next().getLabNumber());

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
