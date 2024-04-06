package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.LabRequestReportLink;
import org.springframework.stereotype.Repository;

@Repository
public class LabRequestReportLinkDaoImpl extends AbstractDaoImpl<LabRequestReportLink> implements LabRequestReportLinkDao {

    public LabRequestReportLinkDaoImpl() {
        super(LabRequestReportLink.class);
    }
    
    public List<LabRequestReportLink> findByReportTableAndReportId(String reportTable, int reportId) {
        Query q = entityManager.createQuery("select l from LabRequestReportLink l WHERE l.reportTable = ?1 AND l.reportId=?2");
        q.setParameter(1, reportTable);
        q.setParameter(2, reportId);
        
        @SuppressWarnings("unchecked")
        List<LabRequestReportLink> results = q.getResultList();
        
        return results;
    }
    
    public List<LabRequestReportLink> findByRequestTableAndRequestId(String requestTable, int requestId) {
        Query q = entityManager.createQuery("select l from LabRequestReportLink l WHERE l.requestTable = ?1 AND l.requestId=?2");
        q.setParameter(1, requestTable);
        q.setParameter(2, requestId);
        
        @SuppressWarnings("unchecked")
        List<LabRequestReportLink> results = q.getResultList();
        
        return results;
    }
}
