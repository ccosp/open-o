package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.TicklerLink;
import org.springframework.stereotype.Repository;

@Repository
public class TicklerLinkDaoImpl extends AbstractDaoImpl<TicklerLink> implements TicklerLinkDao {

    public TicklerLinkDaoImpl() {
        super(TicklerLink.class);
    }

    @Override
    public TicklerLink getTicklerLink(Integer id) {
        return find(id);
    }

    @Override
    public List<TicklerLink> getLinkByTableId(String tableName, Long tableId) {
        Query query = entityManager.createQuery("SELECT tLink from TicklerLink tLink WHERE tLink.tableName = ? and tLink.tableId = ? order by tLink.id");
        query.setParameter(1, tableName);
        query.setParameter(2,tableId);

        @SuppressWarnings("unchecked")
        List<TicklerLink> results = query.getResultList();

        return results;
    }

    @Override
    public List<TicklerLink> getLinkByTickler(Integer ticklerNo) {
        Query query = entityManager.createQuery("SELECT tLink from TicklerLink tLink WHERE tLink.ticklerNo = ? order by tLink.id");
        query.setParameter(1, ticklerNo);

        @SuppressWarnings("unchecked")
        List<TicklerLink> results = query.getResultList();
        return results;
    }

    @Override
    public void save(TicklerLink cLink) {
        persist(cLink);
    }

    @Override
    public void update(TicklerLink cLink) {
        merge(cLink);
    }
}
