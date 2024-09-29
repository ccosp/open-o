//CHECKSTYLE:OFF
package ca.openosp.openo.common.dao;

import ca.openosp.openo.common.model.EReferAttachmentData;

import java.util.Date;

public interface EReferAttachmentDataDao extends AbstractDao<EReferAttachmentData> {
    public EReferAttachmentData getRecentByDocumentId(Integer docId, String type, Date expiry);
}
