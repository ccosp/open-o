//CHECKSTYLE:OFF
package ca.openosp.openo.common.dao;

import ca.openosp.openo.common.model.EmailConfig;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.Collections;
import java.util.List;

public interface EmailConfigDao extends AbstractDao<EmailConfig> {
    public EmailConfig findActiveEmailConfig(EmailConfig emailConfig);

    public EmailConfig findActiveEmailConfig(String senderEmail);

    @SuppressWarnings("unchecked")
    public List<EmailConfig> fillAllActiveEmailConfigs();
}
