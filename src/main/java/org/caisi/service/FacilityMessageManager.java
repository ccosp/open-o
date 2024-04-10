package org.caisi.service;

import java.util.List;
import org.oscarehr.common.model.FacilityMessage;

public interface FacilityMessageManager {
    FacilityMessage getMessage(String messageId);
    void saveFacilityMessage(FacilityMessage msg);
    List<FacilityMessage> getMessages();
    List<FacilityMessage> getMessagesByFacilityId(Integer facilityId);
    List<FacilityMessage> getMessagesByFacilityIdOrNull(Integer facilityId);
    List<FacilityMessage> getMessagesByFacilityIdAndProgramId(Integer facilityId, Integer programId);
    List<FacilityMessage> getMessagesByFacilityIdOrNullAndProgramIdOrNull(Integer facilityId, Integer programId);
}
