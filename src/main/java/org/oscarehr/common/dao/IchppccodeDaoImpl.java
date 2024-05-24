package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.AbstractCodeSystemModel;
import org.oscarehr.common.model.Ichppccode;
import org.springframework.stereotype.Repository;

@Repository
public class IchppccodeDaoImpl extends AbstractDaoImpl<Ichppccode> implements IchppccodeDao {

    public IchppccodeDaoImpl() {
        super(Ichppccode.class);
    }

    @Override
    public List<Ichppccode> findAll() {
        Query query = createQuery("x", null);
        return query.getResultList();
    }

    @Override
    public List<Ichppccode> getIchppccodeCode(String term){
        Query query = entityManager.createQuery("select i from Ichppccode i where i.id=?");
        query.setParameter(1, term);

        @SuppressWarnings("unchecked")
        List<Ichppccode> results = query.getResultList();

        return results;
    }

    @Override
    public List<Ichppccode> getIchppccode(String query) {
        Query q = entityManager.createQuery("select i from Ichppccode i where i.id like ? or i.description like ? order by i.description");
        q.setParameter(1, "%"+query+"%");
        q.setParameter(2, "%"+query+"%");

        @SuppressWarnings("unchecked")
        List<Ichppccode> results = q.getResultList();

        return results;
    }

    @Override
    public List<Ichppccode> searchCode(String term) {
        return getIchppccode(term);
    }

    @Override
    public Ichppccode findByCode(String code) {
        List<Ichppccode> results = getIchppccodeCode(code);
        if(results.isEmpty())
            return null;
        return results.get(0);
    }

    @Override
    public AbstractCodeSystemModel<?> findByCodingSystem(String codingSystem) {
        Query query = entityManager.createQuery("FROM Ichppccode i WHERE i.id like :cs");
        query.setParameter("cs", codingSystem);
        query.setMaxResults(1);

        return find(codingSystem);
    }

    @Override
    public List<Ichppccode> search_research_code(String code, String code1, String code2, String desc, String desc1, String desc2) {
        Query query = entityManager.createQuery("select i from Ichppccode i where i.id like ? or i.id like ? or i.id like ? or i.description like ? or i.description like ? or i.description like ?");
        query.setParameter(1, code);
        query.setParameter(2, code1);
        query.setParameter(3, code2);
        query.setParameter(4, desc);
        query.setParameter(5, desc1);
        query.setParameter(6, desc2);

        return query.getResultList();
    }
}
