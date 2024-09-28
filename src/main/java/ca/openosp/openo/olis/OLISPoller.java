//CHECKSTYLE:OFF
/**
 * Copyright (c) 2008-2012 Indivica Inc.
 * <p>
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */
package ca.openosp.openo.olis;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.openosp.openo.Misc;
import ca.openosp.openo.oscarLab.ca.all.upload.HandlerClassFactory;
import ca.openosp.openo.oscarLab.ca.all.upload.handlers.MessageHandler;
import org.apache.commons.io.FileUtils;
import org.apache.http.impl.cookie.DateUtils;
import org.apache.logging.log4j.Logger;
import ca.openosp.openo.PMmodule.dao.ProviderDao;
import ca.openosp.openo.common.dao.UserPropertyDAO;
import ca.openosp.openo.common.model.Provider;
import ca.openosp.openo.common.model.UserProperty;
import ca.openosp.openo.olis.dao.OLISProviderPreferencesDao;
import ca.openosp.openo.olis.dao.OLISSystemPreferencesDao;
import ca.openosp.openo.olis.model.OLISProviderPreferences;
import ca.openosp.openo.olis.model.OLISSystemPreferences;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.MiscUtils;
import ca.openosp.openo.ehrutil.SpringUtils;

import ca.openosp.openo.oscarLab.FileUploadCheck;
import ca.openosp.openo.oscarLab.ca.all.parsers.Factory;
import ca.openosp.openo.oscarLab.ca.all.parsers.OLISHL7Handler;
import ca.openosp.openo.oscarLab.ca.all.util.Utilities;

import com.indivica.olis.Driver;
import com.indivica.olis.parameters.OBR22;
import com.indivica.olis.parameters.ORC21;
import com.indivica.olis.parameters.ZRP1;
import com.indivica.olis.queries.Z04Query;
import com.indivica.olis.queries.Z06Query;

/**
 * @author Indivica
 */
public class OLISPoller {
    private static final Logger logger = MiscUtils.getLogger();

    public OLISPoller() {
        super();
    }

    public static void startAutoFetch(LoggedInInfo loggedInInfo) {
        OLISPoller olisPoller = new OLISPoller();
        logger.info("===== OLIS FETCH INITIATED ...");

        String[] dateFormat = new String[]{"yyyyMMddHHmmssZ"};

        ProviderDao providerDao = (ProviderDao) SpringUtils.getBean(ProviderDao.class);
        List<Provider> allProvidersList = providerDao.getActiveProviders();

        OLISSystemPreferencesDao olisSystemPreferencesDao = (OLISSystemPreferencesDao) SpringUtils.getBean(OLISSystemPreferencesDao.class);
        ;
        OLISSystemPreferences olisSystemPreferences = olisSystemPreferencesDao.getPreferences();

        OLISProviderPreferencesDao olisProviderPreferencesDao = (OLISProviderPreferencesDao) SpringUtils.getBean(OLISProviderPreferencesDao.class);
        OLISProviderPreferences olisProviderPreferences;

        String defaultStartTime = Misc.getStr(olisSystemPreferences.getStartTime(), "").trim();
        String defaultEndTime = Misc.getStr(olisSystemPreferences.getEndTime(), "").trim();

        Z04Query providerQuery;
        UserPropertyDAO userPropertyDAO = (UserPropertyDAO) SpringUtils.getBean(UserPropertyDAO.class);
        for (Provider provider : allProvidersList) {
            try {
                providerQuery = new Z04Query();
                olisProviderPreferences = olisProviderPreferencesDao.findById(provider.getProviderNo());

                // Creating OBR22 for this request.
                OBR22 obr22 = new OBR22();
                List<Date> dateList = new LinkedList<Date>();
                if (olisProviderPreferences != null) {
                    if (olisProviderPreferences.getStartTime() == null) {
                        olisProviderPreferences.setStartTime(defaultStartTime);
                    }
                    obr22.setValue(DateUtils.parseDate(olisProviderPreferences.getStartTime(), dateFormat));
                } else {
                    if (defaultEndTime.equals("")) {
                        obr22.setValue(DateUtils.parseDate(defaultStartTime, dateFormat));
                    } else {
                        dateList.add(DateUtils.parseDate(defaultStartTime, dateFormat));
                        dateList.add(DateUtils.parseDate(defaultEndTime, dateFormat));
                        obr22.setValue(dateList);
                    }

                    olisProviderPreferences = new OLISProviderPreferences();
                    olisProviderPreferences.setProviderId(provider.getProviderNo());
                }
                providerQuery.setStartEndTimestamp(obr22);

                // Setting HIC for Z04 Request
                ZRP1 zrp1 = new ZRP1(provider.getPractitionerNo(), userPropertyDAO.getStringValue(provider.getProviderNo(), UserProperty.OFFICIAL_OLIS_IDTYPE), "ON", "HL70347",
                        userPropertyDAO.getStringValue(provider.getProviderNo(), UserProperty.OFFICIAL_LAST_NAME),
                        userPropertyDAO.getStringValue(provider.getProviderNo(), UserProperty.OFFICIAL_FIRST_NAME),
                        userPropertyDAO.getStringValue(provider.getProviderNo(), UserProperty.OFFICIAL_SECOND_NAME));
                providerQuery.setRequestingHic(zrp1);
                String response = Driver.submitOLISQuery(loggedInInfo, null, providerQuery);
                if (!response.matches("<Request xmlns=\"http://www.ssha.ca/2005/HIAL\"><Content><![CDATA[.*]]></Content></Request>")) {
                    break;
                }
                List<String> resultsList = olisPoller.parsePollResults(response);
                if (resultsList.size() == 0) {
                    continue;
                }
                olisPoller.importResults(loggedInInfo, resultsList);

                Pattern p = Pattern.compile("@OBR\\.22\\^(\\d{14}-\\d{4})~");
                Matcher matcher = p.matcher(response);
                if (matcher.find()) {
                    olisProviderPreferences.setStartTime(matcher.group(1));
                }
                if (olisProviderPreferences.getId() != null) {
                    olisProviderPreferencesDao.merge(olisProviderPreferences);
                } else {
                    olisProviderPreferencesDao.persist(olisProviderPreferences);
                }
            } catch (Exception e) {
                logger.error("Error polling OLIS for provider " + provider.getProviderNo(), e);
            }
        }

        try {
            Z06Query facilityQuery = new Z06Query();
            olisProviderPreferences = olisProviderPreferencesDao.findById("-1");
            // Creating OBR22 for this request.
            OBR22 obr22 = new OBR22();
            List<Date> dateList = new LinkedList<Date>();
            if (olisProviderPreferences != null) {
                if (olisProviderPreferences.getStartTime() == null) {
                    olisProviderPreferences.setStartTime(defaultStartTime);
                }
                obr22.setValue(DateUtils.parseDate(olisProviderPreferences.getStartTime(), dateFormat));
            } else {
                if (defaultEndTime.equals("")) {
                    obr22.setValue(DateUtils.parseDate(defaultStartTime, dateFormat));
                } else {
                    dateList.add(DateUtils.parseDate(defaultStartTime, dateFormat));
                    dateList.add(DateUtils.parseDate(defaultEndTime, dateFormat));
                    obr22.setValue(dateList);
                }

                olisProviderPreferences = new OLISProviderPreferences();
                olisProviderPreferences.setProviderId("-1");
            }
            facilityQuery.setStartEndTimestamp(obr22);
            ORC21 orc21 = new ORC21();
            orc21.setValue(6, 2, "CN=EMR.MCMUN2.CST,OU=Applications,OU=eHealthUsers,OU=Subscribers,DC=subscribers,DC=ssh");
            orc21.setValue(6, 3, "X500");
            facilityQuery.setOrderingFacilityId(orc21);

            String response = Driver.submitOLISQuery(loggedInInfo, null, facilityQuery);
            if (!response.matches("<Request xmlns=\"http://www.ssha.ca/2005/HIAL\"><Content><![CDATA[.*]]></Content></Request>")) {
                return;
            }
            List<String> resultsList = olisPoller.parsePollResults(response);
            if (resultsList.size() == 0) {
                return;
            }
            olisPoller.importResults(loggedInInfo, resultsList);

            Pattern p = Pattern.compile("@OBR\\.22\\^(\\d{14}-\\d{4})~");
            Matcher matcher = p.matcher(response);
            if (matcher.find()) {
                olisProviderPreferences.setStartTime(matcher.group(1));
            }
            if (olisProviderPreferences.getId() != null) {
                olisProviderPreferencesDao.merge(olisProviderPreferences);
            } else {
                olisProviderPreferencesDao.persist(olisProviderPreferences);
            }
        } catch (Exception e) {
            logger.error("Error polling OLIS for facility", e);
        }
    }

    public static HashMap<String, OLISHL7Handler> pollResultsMap = new HashMap<String, OLISHL7Handler>();

    private List<String> parsePollResults(String response) {
        try {
            UUID uuid = UUID.randomUUID();

            File tempFile = new File(System.getProperty("java.io.tmpdir") + "/olis_" + uuid.toString() + ".response");
            FileUtils.writeStringToFile(tempFile, response);

            @SuppressWarnings("unchecked")
            ArrayList<String> messages = Utilities.separateMessages(System.getProperty("java.io.tmpdir") + "/olis_" + uuid.toString() + ".response");

            List<String> resultList = new LinkedList<String>();

            if (messages != null && !(messages.size() == 1 && messages.get(0).trim().equals(""))) {
                for (String message : messages) {

                    logger.info("message:" + message);
                    // Parse the HL7 string...
                    ca.openosp.openo.oscarLab.ca.all.parsers.MessageHandler h = Factory.getHandler("OLIS_HL7", message);

                    String resultUuid = UUID.randomUUID().toString();

                    pollResultsMap.put(resultUuid, (OLISHL7Handler) h);
                    resultList.add(resultUuid);
                }
                return resultList;
            }

        } catch (Exception e) {
            MiscUtils.getLogger().error("Can't pull out messages from OLIS response.", e);
        }
        return null;
    }

    private void importResults(LoggedInInfo loggedInInfo, List<String> resultList) {
        for (String uuidToAdd : resultList) {

            String fileLocation = System.getProperty("java.io.tmpdir") + "/olis_" + uuidToAdd + ".response";
            File file = new File(fileLocation);
            MessageHandler msgHandler = HandlerClassFactory.getHandler("OLIS_HL7");

            try {
                InputStream is = new FileInputStream(fileLocation);
                int check = FileUploadCheck.addFile(file.getName(), is, "0");
                if (check != FileUploadCheck.UNSUCCESSFUL_SAVE) {
                    if (msgHandler.parse(loggedInInfo, "OLIS_HL7", fileLocation, check, null) != null) {
                        logger.info("Lab successfully added.");
                    } else {
                        logger.info("Error adding lab.");
                    }
                } else {
                    logger.info("Lab already in system.");
                }
                is.close();

            } catch (Exception e) {
                MiscUtils.getLogger().error("Couldn't add requested OLIS lab to Inbox.", e);
            }
        }
    }
}
