package org.oscarehr.common.dao;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import org.oscarehr.common.model.EmailLog;

@Repository
public class EmailLogDao extends AbstractDao<EmailLog> {

    public EmailLogDao() {
    	super(EmailLog.class);
    }

    @SuppressWarnings("unchecked")
    public List<EmailLog> getAllEmailLogs() {
        Query query = entityManager.createQuery("SELECT el FROM EmailLog el");
        List<EmailLog> emailLogs = query.getResultList();
        Collections.sort(emailLogs);
        return emailLogs;
    }

    /**
     * This method is used for retrieving email logs based on various filters such as date range, demographic number,
     * sender email address, and email status. It is called from the 'Admin > Emails > Manage Emails' page.
     *
     * @param dateBegin The start date for filtering email logs.
     * @param dateEnd The end date for filtering email logs.
     * @param demographicNo The demographic number for demographic lastname and firstname.
     * @param senderEmailAddress The sender email address for filtering email logs.
     * @param emailStatus The email status for filtering email logs.
     * @return A list of email logs that match the specified filters.
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getEmailStatusByDateDemographicSenderStatus(Date dateBegin, Date dateEnd, String demographicNo, String senderEmailAddress, String emailStatus) {
        StringBuilder sql = new StringBuilder("SELECT el.id, el.subject, el.fromEmail, el.toEmail, el.status, el.errorMessage, el.timestamp, el.password, el.isEncrypted, ec.senderFirstName, ec.senderLastName, d.FirstName as recipientFirstName, d.LastName as recipientLastName, p.FirstName as providerFirstName, p.LastName as providerLastName FROM EmailLog el, EmailConfig ec, Demographic d, Provider p WHERE el.emailConfig.id = ec.id AND el.demographicNo = d.DemographicNo AND el.providerNo = p.ProviderNo");
    	
        Map<String, Object> parameters = new HashMap<>();
        if (demographicNo != null) { appendToSqlAndParameters(sql, "el.demographicNo = :demo", "demo", Integer.parseInt(demographicNo), parameters); }
    	if (emailStatus != null) { appendToSqlAndParameters(sql, "el.status = :emailStatus", "emailStatus", EmailLog.EmailStatus.valueOf(emailStatus), parameters); }
        if (senderEmailAddress != null) { appendToSqlAndParameters(sql, "el.fromEmail = :senderEmailAddress", "senderEmailAddress", senderEmailAddress, parameters); }
        if (dateBegin != null) { appendToSqlAndParameters(sql, "el.timestamp >= :beginDate", "beginDate", dateBegin, parameters); }
        if (dateEnd != null) { appendToSqlAndParameters(sql, "el.timestamp <= :endDate", "endDate", dateEnd, parameters); }
    	
    	Query query = entityManager.createQuery(sql.toString());
    	for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        return query.getResultList();
    }

    private void appendToSqlAndParameters(StringBuilder sql, String clause, String paramName, Object paramValue, Map<String, Object> parameters) {
        sql.append(" AND ").append(clause);
        parameters.put(paramName, paramValue);
    }

}
