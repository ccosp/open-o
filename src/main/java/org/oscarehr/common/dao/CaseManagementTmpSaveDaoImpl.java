package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.CaseManagementTmpSave;
import org.springframework.stereotype.Repository;

@Repository
public class CaseManagementTmpSaveDaoImpl extends AbstractDaoImpl<CaseManagementTmpSave> implements CaseManagementTmpSaveDao {

    private static final String NOTE_TAG_REGEXP = "^\\[[[:digit:]]{2}-[[:alpha:]]{3}-[[:digit:]]{4} \\.\\: [^]]*\\][[:space:]]+$";

    public CaseManagementTmpSaveDaoImpl() {
        super(CaseManagementTmpSave.class);
    }

    @Override
    public void remove(String providerNo, Integer demographicNo, Integer programId) {
        Query query = entityManager.createQuery("SELECT x FROM CaseManagementTmpSave x WHERE x.providerNo = ? and x.demographicNo=? and x.programId = ?");
        query.setParameter(1,providerNo);
        query.setParameter(2,demographicNo);
        query.setParameter(3,programId);

        @SuppressWarnings("unchecked")
        List<CaseManagementTmpSave> results = query.getResultList();

        for(CaseManagementTmpSave result : results) {
            remove(result);
        }
    }

    @Override
    public CaseManagementTmpSave find(String providerNo, Integer demographicNo, Integer programId) {
        Query query = entityManager.createQuery("SELECT x FROM CaseManagementTmpSave x WHERE x.providerNo = ? and x.demographicNo=? and x.programId = ? order by x.updateDate DESC");
        query.setParameter(1,providerNo);
        query.setParameter(2,demographicNo);
        query.setParameter(3,programId);

        return this.getSingleResultOrNull(query);
    }

    @Override
    public CaseManagementTmpSave find(String providerNo, Integer demographicNo, Integer programId, Date date) {
        Query query = entityManager.createQuery("SELECT x FROM CaseManagementTmpSave x WHERE x.providerNo = ? and x.demographicNo=? and x.programId = ? and x.updateDate >= ? order by x.updateDate DESC");
        query.setParameter(1,providerNo);
        query.setParameter(2,demographicNo);
        query.setParameter(3,programId);
        query.setParameter(4, date);

        return this.getSingleResultOrNull(query);
    }

    @Override
    public boolean noteHasContent(Integer id) {
        Query query = entityManager.createNativeQuery("SELECT * FROM casemgmt_tmpsave x WHERE x.id = ? and x.note  NOT REGEXP ? order by x.update_date DESC", CaseManagementTmpSave.class);

        query.setParameter(1, id);
        query.setParameter(2, NOTE_TAG_REGEXP);

        return (this.getSingleResultOrNull(query) != null);
    }
}
