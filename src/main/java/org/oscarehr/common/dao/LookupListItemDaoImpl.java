package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.LookupListItem;
import org.springframework.stereotype.Repository;
import javax.persistence.Query;

@Repository
public class LookupListItemDaoImpl extends AbstractDaoImpl<LookupListItem> implements LookupListItemDao {

    public LookupListItemDaoImpl() {
        super(LookupListItem.class);
    }
    
    @Override
    public List<LookupListItem> findActiveByLookupListId(int lookupListId) {
        return findByLookupListId(lookupListId, Boolean.TRUE);
    }

    @Override
    public List<LookupListItem> findByLookupListId(int lookupListId, boolean active) {
        Query q = entityManager.createQuery("select l from LookupListItem l where l.lookupListId=? and l.active=? order by l.displayOrder");

        q.setParameter(1,lookupListId);
        q.setParameter(2,active);

        @SuppressWarnings("unchecked")
        List<LookupListItem> result = q.getResultList();

        return result;
    }
    
    @Override
    public LookupListItem findByLookupListIdAndValue(int lookupListId, String value) {
        Query q = entityManager.createQuery("select l from LookupListItem l where l.lookupListId=? and l.value=?");

        q.setParameter(1,lookupListId);
        q.setParameter(2,value);

        LookupListItem item = this.getSingleResultOrNull(q);
        
        return item;
    }
}
