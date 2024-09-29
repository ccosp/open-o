//CHECKSTYLE:OFF
package ca.openosp.openo.common.dao;

import ca.openosp.openo.common.model.EmailLog;

import java.util.Date;
import java.util.List;

public interface EmailLogDao extends AbstractDao<EmailLog> {
    public List<EmailLog> getEmailStatusByDateDemographicSenderStatus(Date dateBegin, Date dateEnd, String demographicNo, String senderEmailAddress, String emailStatus);
}
