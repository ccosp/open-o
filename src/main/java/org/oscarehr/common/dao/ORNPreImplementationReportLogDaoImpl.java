package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.ORNPreImplementationReportLog;
import org.springframework.stereotype.Repository;

@Repository
public class ORNPreImplementationReportLogDaoImpl extends AbstractDaoImpl<ORNPreImplementationReportLog> implements ORNPreImplementationReportLogDao {

    public ORNPreImplementationReportLogDaoImpl() {
        super(ORNPreImplementationReportLog.class);
    }
    
    @Override
    public List<ORNPreImplementationReportLog> getAllReports() {
        Query query = entityManager.createQuery("SELECT x FROM ORNPreImplementationReportLog x ORDER BY x.lastUpdateDate DESC");
        @SuppressWarnings("unchecked")
        List<ORNPreImplementationReportLog> results = query.getResultList();
        return results;
    }
}
