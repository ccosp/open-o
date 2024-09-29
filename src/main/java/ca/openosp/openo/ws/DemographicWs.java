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


package ca.openosp.openo.ws;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.cxf.annotations.GZIP;
import org.apache.logging.log4j.Logger;
import ca.openosp.openo.common.Gender;
import ca.openosp.openo.common.model.Consent;
import ca.openosp.openo.common.model.ConsentType;
import ca.openosp.openo.common.model.Demographic;
import ca.openosp.openo.common.model.PHRVerification;
import ca.openosp.openo.managers.DemographicManager;
import ca.openosp.openo.managers.PatientConsentManager;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.MiscUtils;
import ca.openosp.openo.ws.transfer_objects.DemographicTransfer;
import ca.openosp.openo.ws.transfer_objects.DemographicTransfer2;
import ca.openosp.openo.ws.transfer_objects.PhrVerificationTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@WebService
@Component
@GZIP(threshold = AbstractWs.GZIP_THRESHOLD)
public class DemographicWs extends AbstractWs {
    private static Logger logger = MiscUtils.getLogger();

    @Autowired
    private DemographicManager demographicManager;

    @Autowired
    private PatientConsentManager patientConsentManager;

    public DemographicTransfer getDemographic(Integer demographicId) {
        Demographic demographic = demographicManager.getDemographicWithExt(getLoggedInInfo(), demographicId);
        return (DemographicTransfer.toTransfer(demographic));
    }

    public DemographicTransfer2 getDemographic2(Integer demographicId) {
        Demographic demographic = demographicManager.getDemographic(getLoggedInInfo(), demographicId);
        return (DemographicTransfer2.toTransfer(demographic));
    }

    public DemographicTransfer getDemographicByMyOscarUserName(String myOscarUserName) {
        Demographic demographic = demographicManager.getDemographicByMyOscarUserName(getLoggedInInfo(), myOscarUserName);
        return (DemographicTransfer.toTransfer(demographic));
    }

    public DemographicTransfer[] searchDemographicByName(String searchString, int startIndex, int itemsToReturn) {
        List<Demographic> demographics = demographicManager.searchDemographicByName(getLoggedInInfo(), searchString, startIndex, itemsToReturn);
        return (DemographicTransfer.toTransfers(demographics));
    }

    public PhrVerificationTransfer getLatestPhrVerificationByDemographic(Integer demographicId) {
        PHRVerification phrVerification = demographicManager.getLatestPhrVerificationByDemographicId(getLoggedInInfo(), demographicId);
        return (PhrVerificationTransfer.toTransfer(phrVerification));
    }

    /**
     * This method should only return true if the demographic passed in is "phr verified" to a sufficient level to allow a provider to send this phr account messages.
     */
    public boolean isPhrVerifiedToSendMessages(Integer demographicId) {
        boolean result = demographicManager.isPhrVerifiedToSendMessages(getLoggedInInfo(), demographicId);
        return (result);
    }

    /**
     * This method should only return true if the demographic passed in is "phr verified" to a sufficient level to allow a provider to send this phr account medicalData.
     */
    public boolean isPhrVerifiedToSendMedicalData(Integer demographicId) {
        boolean result = demographicManager.isPhrVerifiedToSendMedicalData(getLoggedInInfo(), demographicId);
        return (result);
    }

    /**
     * @see DemographicManager.searchDemographicsByAttributes for parameter details
     */
    public DemographicTransfer[] searchDemographicsByAttributes(String hin, String firstName, String lastName, Gender gender, Calendar dateOfBirth, String city, String province, String phone, String email, String alias, int startIndex, int itemsToReturn) {
        List<Demographic> demographics = demographicManager.searchDemographicsByAttributes(getLoggedInInfo(), hin, firstName, lastName, gender, dateOfBirth, city, province, phone, email, alias, startIndex, itemsToReturn);
        return (DemographicTransfer.toTransfers(demographics));
    }

    /**
     * @programId can be null for all / any program
     */
    public Integer[] getAdmittedDemographicIdsByProgramProvider(Integer programId, String providerNo) {
        logger.debug("programId=" + programId + ", providerNo=" + providerNo);
        List<Integer> results = demographicManager.getAdmittedDemographicIdsByProgramAndProvider(getLoggedInInfo(), programId, providerNo);
        return (results.toArray(new Integer[0]));
    }

    public Integer[] getDemographicIdsWithMyOscarAccounts(@WebParam(name = "startDemographicIdExclusive") Integer startDemographicIdExclusive, @WebParam(name = "itemsToReturn") int itemsToReturn) {
        List<Integer> results = demographicManager.getDemographicIdsWithMyOscarAccounts(getLoggedInInfo(), startDemographicIdExclusive, itemsToReturn);
        return (results.toArray(new Integer[0]));
    }

    public DemographicTransfer[] getDemographics(Integer[] demographicIds) {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        for (Integer i : demographicIds) {
            ids.add(i);
        }

        List<Demographic> demographics = demographicManager.getDemographics(getLoggedInInfo(), ids);
        return (DemographicTransfer.toTransfers(demographics));
    }

    public DemographicTransfer[] getActiveDemographicsAfter(@WebParam(name = "lastUpdate") Calendar lastUpdate, @WebParam(name = "fields") String fields) {
        Date afterDateExclusive = lastUpdate != null ? lastUpdate.getTime() : null;
        List<Demographic> demographics = demographicManager.getActiveDemographicAfter(getLoggedInInfo(), afterDateExclusive);

        List<DemographicTransfer> result = new ArrayList<DemographicTransfer>();
        if (demographics != null) {
            String[] fieldList = fields != null ? fields.split(",") : null;

            for (Demographic d : demographics) {
                DemographicTransfer dto = DemographicTransfer.toTransfer(d);

                if (fieldList != null) {
                    result.add(dto.filter(fieldList));
                } else {
                    result.add(dto);
                }
            }
        }
        return result.toArray(new DemographicTransfer[0]);
    }

    public DemographicTransfer2[] getActiveDemographicsAfter2(@WebParam(name = "lastUpdate") Calendar lastUpdate, @WebParam(name = "fields") String fields) {
        Date afterDateExclusive = lastUpdate != null ? lastUpdate.getTime() : null;
        List<Demographic> demographics = demographicManager.getActiveDemographicAfter(getLoggedInInfo(), afterDateExclusive);

        List<DemographicTransfer2> result = new ArrayList<DemographicTransfer2>();
        if (demographics != null) {
            String[] fieldList = fields != null ? fields.split(",") : null;

            for (Demographic d : demographics) {
                DemographicTransfer2 dto = DemographicTransfer2.toTransfer(d);

                if (fieldList != null) {
                    result.add(dto.filter(fieldList));
                } else {
                    result.add(dto);
                }
            }
        }
        return result.toArray(new DemographicTransfer2[0]);
    }

    public String writePHRId(@WebParam(name = "demographicNo") Integer demographicNo, @WebParam(name = "phrId") String phrId) {

        if (demographicNo != null && phrId != null) {
            Demographic demo = demographicManager.getDemographic(getLoggedInInfo(), demographicNo);
            demo.setMyOscarUserName(phrId);
            demographicManager.updateDemographic(getLoggedInInfo(), demo);

            return "success";
        }
        return "fail";
    }

    public Integer[] getConsentedDemographicIdsAfter(@WebParam(name = "lastUpdate") Calendar lastUpdate) {
        LoggedInInfo loggedInInfo = getLoggedInInfo();
        ConsentType consentType = patientConsentManager.getProviderSpecificConsent(loggedInInfo);
        List<Consent> consents = patientConsentManager.getConsentsByTypeAndEditDate(loggedInInfo, consentType, lastUpdate.getTime());
        List<Integer> demoIds = new ArrayList<Integer>();
        for (Consent c : consents) {
            if (!demoIds.contains(c.getDemographicNo())) demoIds.add(c.getDemographicNo());
        }

        return demoIds.toArray(new Integer[0]);
    }
}
