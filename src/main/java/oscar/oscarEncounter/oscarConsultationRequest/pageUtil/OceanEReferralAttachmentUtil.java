package oscar.oscarEncounter.oscarConsultationRequest.pageUtil;

import java.util.Collections;
import java.util.List;

import org.oscarehr.common.dao.EReferAttachmentDao;
import org.oscarehr.common.dao.EReferAttachmentDataDao;
import org.oscarehr.common.model.EReferAttachment;
import org.oscarehr.common.model.EReferAttachmentData;
import org.oscarehr.util.SpringUtils;

public class OceanEReferralAttachmentUtil {
    private static EReferAttachmentDataDao eReferAttachmentDataDao = SpringUtils.getBean(EReferAttachmentDataDao.class);
    private static EReferAttachmentDao eReferAttachmentDao = SpringUtils.getBean(EReferAttachmentDao.class);

    public static void detachOceanEReferralConsult(String docId, String type) {
        EReferAttachmentData eReferAttachmentData = eReferAttachmentDataDao.getByDocumentId(Integer.parseInt(docId), type);        
        if (eReferAttachmentData == null) { return; }

        List<EReferAttachmentData> eReferAttachmentDataList = eReferAttachmentData.geteReferAttachment().getAttachments();
        boolean deleteEReferAttachment = eReferAttachmentDataList.size() == 1;

        eReferAttachmentDataDao.remove(eReferAttachmentData.getId());
        if (deleteEReferAttachment) { eReferAttachmentDao.remove(eReferAttachmentData.getId().getEReferAttachment().getId()); }
    }

    public static void attachOceanEReferralConsult(String documentId, Integer demographicNo, String type) {
        EReferAttachment eReferAttachment = eReferAttachmentDao.getRecentByDemographic(demographicNo);
        if (eReferAttachment == null) { eReferAttachment = new EReferAttachment(demographicNo); }
        EReferAttachmentData attachmentData = new EReferAttachmentData(eReferAttachment, Integer.parseInt(documentId), type);

        if (eReferAttachment.getId() != null) {
            // If an eReferAttachment record exists, save the new attachment data only
            eReferAttachmentDataDao.persist(attachmentData);
        } else {
            // If an eReferAttachment doesn't exist, save the attachment data with the new 
            // eReferAttachment
            List<EReferAttachmentData> attachments = Collections.singletonList(attachmentData);
            eReferAttachment.setAttachments(attachments);
            eReferAttachmentDao.persist(eReferAttachment);
        }
    }
}
