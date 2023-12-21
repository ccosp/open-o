package org.oscarehr.common.dao;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import org.oscarehr.common.model.EmailLog;

@Transactional
@Repository
public class EmailLogDao {

    @PersistenceContext
    private EntityManager entityManager;

    public void saveEmailLog(EmailLog emailLog) {
        if (emailLog.getId() == null) {
            entityManager.persist(emailLog);
        } else {
            entityManager.merge(emailLog);
        }
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
}
