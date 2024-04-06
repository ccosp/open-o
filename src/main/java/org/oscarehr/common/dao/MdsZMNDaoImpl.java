package org.oscarehr.common.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.model.MdsZMN;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class MdsZMNDaoImpl extends AbstractDaoImpl<MdsZMN> implements MdsZMNDao {

    public MdsZMNDaoImpl() {
        super(MdsZMN.class);
    }

    public MdsZMN findBySegmentIdAndReportName(Integer id, String reportName) {
        Query query = createQuery("z", "z.id = :id AND z.reportName = :reportName");
        query.setParameter("id", id);
        query.setParameter("reportName", reportName);
        return getSingleResultOrNull(query);
    }

    public MdsZMN findBySegmentIdAndResultMnemonic(Integer id, String rm) {
        Query query = createQuery("z", "z.id = :id and z.resultMnemonic = :rm");
        query.setParameter("id", id);
        query.setParameter("rm", rm);
        return getSingleResultOrNull(query);
    }

    public List<String> findResultCodes(Integer id, String reportSequence) {        
        String sql = "SELECT zmn.resultCode FROM MdsZMN zmn WHERE zmn.id = :id " +
                "AND zmn.reportGroup = :reportSequence ";
        Query query = entityManager.createQuery(sql);
        query.setParameter("id", id);
        query.setParameter("reportSequence", reportSequence);
        List<Object[]> resultCodes = query.getResultList();
        List<String> result = new ArrayList<String>(resultCodes.size());
        for(Object[] o : resultCodes) {
            result.add(String.valueOf(o[0]));
        }
        return result;
    }
}
