package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.CdsClientForm;
import org.springframework.stereotype.Repository;

@Repository
public class CdsClientFormDaoImpl extends AbstractDaoImpl<CdsClientForm> implements CdsClientFormDao {

    public CdsClientFormDaoImpl() {
        super(CdsClientForm.class);
    }

    public CdsClientForm findLatestByFacilityClient(Integer facilityId, Integer clientId) {
        String sqlCommand = "select * from CdsClientForm where facilityId=?1 and clientId=?2 order by created desc";
        Query query = entityManager.createNativeQuery(sqlCommand, modelClass);
        query.setMaxResults(1);
        query.setParameter(1, facilityId);
        query.setParameter(2, clientId);
        return getSingleResultOrNull(query);
    }

    public CdsClientForm findLatestByFacilityAdmissionId(Integer facilityId, Integer admissionId, Boolean signed) {
        String sqlCommand = "select x from CdsClientForm x where x.facilityId=?1 and x.admissionId=?2" + (signed != null ? " and signed=?3" : "") + " order by x.created desc";
        Query query = entityManager.createQuery(sqlCommand);
        query.setParameter(1, facilityId);
        query.setParameter(2, admissionId);
        if (signed != null) query.setParameter(3, signed);
        @SuppressWarnings("unchecked")
        List<CdsClientForm> results = query.getResultList();
        if (results.size() > 0) return (results.get(0));
        else return (null);
    }

    public List<CdsClientForm> findByFacilityClient(Integer facilityId, Integer clientId) {
        String sqlCommand = "select x from CdsClientForm x where x.facilityId=?1 and x.clientId=?2 order by x.created desc";
        Query query = entityManager.createQuery(sqlCommand);
        query.setParameter(1, facilityId);
        query.setParameter(2, clientId);
        @SuppressWarnings("unchecked")
        List<CdsClientForm> results=query.getResultList();
        return (results);
    }

    public List<CdsClientForm> findSignedCdsForms(Integer facilityId, String formVersion, Date startDate, Date endDate) {
        String sqlCommand="select x from CdsClientForm x where x.facilityId=?1 and x.signed=?2 and x.cdsFormVersion=?3 and x.created>=?4 and x.created<?5";
        Query query = entityManager.createQuery(sqlCommand);
        query.setParameter(1, facilityId);
        query.setParameter(2, true);
        query.setParameter(3, formVersion);
        query.setParameter(4, startDate);
        query.setParameter(5, endDate);
        @SuppressWarnings("unchecked")
        List<CdsClientForm> results=query.getResultList();
        return(results);
    }
}
