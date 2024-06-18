package org.oscarehr.common.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import org.oscarehr.common.model.EmailLog;
import org.oscarehr.common.model.EmailLog.EmailStatus;

@Repository
public class EmailLogDao extends AbstractDao<EmailLog> {

    public EmailLogDao() {
    	super(EmailLog.class);
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
    public List<EmailLog> getEmailStatusByDateDemographicSenderStatus(Date dateBegin, Date dateEnd, String demographicNo, String senderEmailAddress, String emailStatus) {
        StringBuilder hql = new StringBuilder("SELECT el FROM EmailLog el " +
                                                "JOIN el.emailConfig ec " +
                                                "JOIN el.demographic d " +
                                                "JOIN el.provider p WHERE 1=1");
    	
        Map<String, Object> parameters = new HashMap<>();
        if (demographicNo != null) { 
            appendToSqlAndParameters(hql, " AND el.demographic.DemographicNo = :demo", "demo", Integer.parseInt(demographicNo), parameters); 
        }
    	if (emailStatus != null) { 
            appendToSqlAndParameters(hql, " AND el.status = :emailStatus", "emailStatus", EmailStatus.valueOf(emailStatus), parameters); 
        }
        if (senderEmailAddress != null) { 
            appendToSqlAndParameters(hql, " AND el.fromEmail = :senderEmailAddress", "senderEmailAddress", senderEmailAddress, parameters); 
        }
        if (dateBegin != null) { 
            appendToSqlAndParameters(hql, " AND el.timestamp >= :beginDate", "beginDate", dateBegin, parameters); 
        }
        if (dateEnd != null) { 
            appendToSqlAndParameters(hql, " AND el.timestamp <= :endDate", "endDate", dateEnd, parameters); 
        }
    	
    	Query query = entityManager.createQuery(hql.toString());
    	for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        return query.getResultList();
    }

    private void appendToSqlAndParameters(StringBuilder sql, String clause, String paramName, Object paramValue, Map<String, Object> parameters) {
        sql.append(clause);
        parameters.put(paramName, paramValue);
    }

}
