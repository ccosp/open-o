package org.oscarehr.common.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import org.oscarehr.common.model.EmailLog;
import org.oscarehr.common.model.EmailLog.EmailStatus;
import org.oscarehr.email.core.EmailStatusResult;

@Transactional
@Repository
public class EmailLogDao {

    @PersistenceContext
    private EntityManager entityManager;

    public void persist(EmailLog emailLog) {
        entityManager.persist(emailLog);
    }

    public void merge(EmailLog emailLog) {
        entityManager.merge(emailLog);
    }

    public EmailLog getEmailLogById(Integer id) {
        return entityManager.find(EmailLog.class, id);
    }

    @SuppressWarnings("unchecked")
    public List<EmailLog> getAllEmailLogs() {
        Query query = entityManager.createQuery("SELECT el FROM EmailLog el");
        List<EmailLog> emailLogs = query.getResultList();
        Collections.sort(emailLogs);
        return emailLogs;
    }

    @SuppressWarnings("unchecked")
    public List<EmailStatusResult> getEmailStatusByDateDemographicSenderStatus(Date dateBegin, Date dateEnd, String demographicNo, String senderEmailAddress, String emailStatus) {
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

        List<Object[]> resultList = query.getResultList();
        return retriveEmailStatusResultList(resultList);
    }

    private List<EmailStatusResult> retriveEmailStatusResultList(List<Object[]> resultList) {
        List<EmailStatusResult> emailStatusResults = new ArrayList<>();
        for (Object[] result : resultList) {
            EmailStatusResult emailStatusResult = new EmailStatusResult();
            emailStatusResult.setLogId((Integer) result[0]);
            emailStatusResult.setSubject((String) result[1]);
            emailStatusResult.setSenderEmail((String) result[2]);
            emailStatusResult.setRecipientEmail((String) result[3]);
            emailStatusResult.setStatus((EmailStatus) result[4]);
            emailStatusResult.setErrorMessage((String) result[5]);
            emailStatusResult.setCreated((Date) result[6]);
            emailStatusResult.setPassword((String) result[7]);
            emailStatusResult.setIsEncrypted((boolean) result[8]);
            emailStatusResult.setSenderFullName((String) result[9], (String) result[10]);
            emailStatusResult.setRecipientFullName((String) result[11], (String) result[12]);
            emailStatusResult.setProviderFullName((String) result[13], (String) result[14]);
            emailStatusResults.add(emailStatusResult);
        }
        Collections.sort(emailStatusResults);
        return emailStatusResults;
    }

    private void appendToSqlAndParameters(StringBuilder sql, String clause, String paramName, Object paramValue, Map<String, Object> parameters) {
        sql.append(" AND ").append(clause);
        parameters.put(paramName, paramValue);
    }

}
