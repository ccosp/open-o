package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.NativeSql;
import org.oscarehr.common.model.CtlDiagCode;
import org.springframework.stereotype.Repository;

@Repository
public class CtlDiagCodeDaoImpl extends AbstractDaoImpl<CtlDiagCode> implements CtlDiagCodeDao {

    public CtlDiagCodeDaoImpl() {
        super(CtlDiagCode.class);
    }

    @SuppressWarnings("unchecked")
    @NativeSql({"ctl_diagcode", "diagnosticcode"})
    public List<Object[]> getDiagnostics(String billRegion, String serviceType) {
        Query query = entityManager.createNativeQuery("SELECT d.diagnostic_code, d.description FROM diagnosticcode d, " 
                + "ctl_diagcode c WHERE d.diagnostic_code=c.diagnostic_code and d.region = ? and c.servicetype = ?");
        query.setParameter(1, billRegion);
        query.setParameter(2, serviceType);
        return query.getResultList();
    }

    public List<CtlDiagCode> findByServiceType(String serviceType) {
        Query q = entityManager.createQuery("select x from CtlDiagCode x where x.serviceType = ?");
        q.setParameter(1, serviceType);
        
        @SuppressWarnings("unchecked")
        List<CtlDiagCode> results = q.getResultList();
        
        return results;
    }
}
