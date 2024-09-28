//CHECKSTYLE:OFF
package ca.openosp.openo.common.dao;

import ca.openosp.openo.common.model.EReferAttachment;

import java.util.Date;


public interface EReferAttachmentDao extends AbstractDao<EReferAttachment> {
    public EReferAttachment getRecentByDemographic(Integer demographicNo, Date expiry);
}
