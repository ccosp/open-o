package org.oscarehr.common.dao;

import org.oscarehr.common.model.EmailConfig;

import java.util.List;

public interface EmailConfigDao extends AbstractDao<EmailConfig> {
    public EmailConfig findActiveEmailConfig(EmailConfig emailConfig);

    public EmailConfig findActiveEmailConfig(String senderEmail);

    @SuppressWarnings("unchecked")
    public List<EmailConfig> fillAllActiveEmailConfigs();
}
