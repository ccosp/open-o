package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.OcanConnexOption;
import org.springframework.stereotype.Repository;

@Repository
public class OcanConnexOptionDaoImpl extends AbstractDaoImpl<OcanConnexOption> implements OcanConnexOptionDao {

    public OcanConnexOptionDaoImpl() {
        super(OcanConnexOption.class);
    }

    public List<OcanConnexOption> findByLHINCode(String LHIN_code) {
        String sqlCommand = "select x from OcanConnexOption x where x.LHINCode=?1 order by x.orgName";
        Query query = entityManager.createQuery(sqlCommand);
        query.setParameter(1, LHIN_code);
        @SuppressWarnings("unchecked")
        List<OcanConnexOption> results = query.getResultList();
        return (results);
    }

    public List<OcanConnexOption> findByLHINCodeOrgName(String LHIN_code, String orgName) {
        String sqlCommand = "select x from OcanConnexOption x where x.LHINCode=?1 and x.orgName=?2 order by x.programName";
        Query query = entityManager.createQuery(sqlCommand);
        query.setParameter(1, LHIN_code);
        query.setParameter(2, orgName);
        @SuppressWarnings("unchecked")
        List<OcanConnexOption> results = query.getResultList();
        return (results);
    }

    public List<OcanConnexOption> findByLHINCodeOrgNameProgramName(String LHIN_code, String orgName, String programName) {
        String sqlCommand = "select x from OcanConnexOption x where x.LHINCode=?1 and x.orgName=?2 and x.programName=?3 order by x.programName";
        Query query = entityManager.createQuery(sqlCommand);
        query.setParameter(1, LHIN_code);
        query.setParameter(2, orgName);
        query.setParameter(3, programName);
        @SuppressWarnings("unchecked")
        List<OcanConnexOption> results = query.getResultList();
        return (results);
    }

    public OcanConnexOption findByID(Integer connexOptionId) {
        String sqlCommand = "select x from OcanConnexOption x where x.id=?1";
        Query query = entityManager.createQuery(sqlCommand);
        query.setParameter(1, connexOptionId);
        return this.getSingleResultOrNull(query);
    }
}
