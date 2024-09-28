//CHECKSTYLE:OFF
package ca.openosp.openo.oscarEncounter.oceanEReferal.pageUtil;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.oscarehr.common.dao.EReferAttachmentDaoImpl;
import org.oscarehr.common.dao.EReferAttachmentDataDaoImpl;
import org.oscarehr.common.model.EReferAttachment;
import org.oscarehr.common.model.EReferAttachmentData;
import org.oscarehr.util.SpringUtils;

public class OceanEReferralAttachmentUtil {
    private static EReferAttachmentDataDaoImpl eReferAttachmentDataDao = SpringUtils.getBean(EReferAttachmentDataDaoImpl.class);
    private static EReferAttachmentDaoImpl eReferAttachmentDao = SpringUtils.getBean(EReferAttachmentDaoImpl.class);

    public static void detachOceanEReferralConsult(String docId, String type) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        EReferAttachmentData eReferAttachmentData = eReferAttachmentDataDao.getRecentByDocumentId(Integer.parseInt(docId), type, calendar.getTime());
        if (eReferAttachmentData == null) {
            return;
        }

        List<EReferAttachmentData> eReferAttachmentDataList = eReferAttachmentData.geteReferAttachment().getAttachments();
        boolean deleteEReferAttachment = eReferAttachmentDataList.size() == 1;

        eReferAttachmentDataDao.remove(eReferAttachmentData.getId());
        if (deleteEReferAttachment) {
            eReferAttachmentDao.remove(eReferAttachmentData.getId().getEReferAttachment().getId());
        }
    }

    public static void attachOceanEReferralConsult(String documentId, Integer demographicNo, String type) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        EReferAttachment eReferAttachment = eReferAttachmentDao.getRecentByDemographic(demographicNo, calendar.getTime());
        if (eReferAttachment == null) {
            eReferAttachment = new EReferAttachment(demographicNo);
        }
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
