package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.FacilityMessage;

public interface FacilityMessageDao extends AbstractDao<FacilityMessage> {
    List<FacilityMessage> getMessages();
    List<FacilityMessage> getMessagesByFacilityId(Integer facilityId);
    List<FacilityMessage> getMessagesByFacilityIdOrNull(Integer facilityId);
    List<FacilityMessage> getMessagesByFacilityIdAndProgramId(Integer facilityId, Integer programId);
    List<FacilityMessage> getMessagesByFacilityIdOrNullAndProgramIdOrNull(Integer facilityId, Integer programId);
}
