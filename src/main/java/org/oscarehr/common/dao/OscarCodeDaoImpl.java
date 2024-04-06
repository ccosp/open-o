package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.AbstractCodeSystemModel;
import org.oscarehr.common.model.OscarCode;
import org.springframework.stereotype.Repository;

@Repository
public class OscarCodeDaoImpl extends AbstractDaoImpl<OscarCode> implements OscarCodeDao {

    public OscarCodeDaoImpl() {
        super(OscarCode.class);
    }

    public List<OscarCode> getIcd9Code(String icdCode){
        Query query = entityManager.createQuery("select i from OscarCode i where i.oscarCode=?");
        query.setParameter(1, icdCode);

        @SuppressWarnings("unchecked")
        List<OscarCode> results = query.getResultList();

        return results;
    }

    public List<OscarCode> getOscarCode(String query) {
        Query q = entityManager.createQuery("select i from OscarCode i where i.oscarCode like ? or i.description like ? order by i.description");
        q.setParameter(1, "%"+query+"%");
        q.setParameter(2, "%"+query+"%");

        @SuppressWarnings("unchecked")
        List<OscarCode> results = q.getResultList();

        return results;
    }

    public OscarCode findByCode(String code) {
        List<OscarCode> results =  getOscarCode(code);
        if(results.isEmpty())
            return null;
        return results.get(0);
    }

    public AbstractCodeSystemModel<?> findByCodingSystem(String codingSystem) {
        Query query = entityManager.createQuery("FROM OscarCode i WHERE i.oscarCode like :cs");
        query.setParameter("cs", codingSystem);
        query.setMaxResults(1);

        return getSingleResultOrNull(query);
    }
}
