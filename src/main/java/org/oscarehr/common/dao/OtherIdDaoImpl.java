package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.OtherId;
import org.springframework.stereotype.Repository;

@Repository
public class OtherIdDaoImpl extends AbstractDaoImpl<OtherId> implements OtherIdDao {

    public OtherIdDaoImpl() {
        super(OtherId.class);
    }

    public OtherId getOtherId(Integer tableName, Integer tableId, String otherKey){
        return getOtherId(tableName,String.valueOf(tableId),otherKey);
    }

    public OtherId getOtherId(Integer tableName, String tableId, String otherKey){
        Query query = entityManager.createQuery("select o from OtherId o where o.tableName=? and o.tableId=? and o.otherKey=? and o.deleted=? order by o.id desc");
        query.setParameter(1, tableName);
        query.setParameter(2, tableId);
        query.setParameter(3, otherKey);
        query.setParameter(4, false);

        @SuppressWarnings("unchecked")
        List<OtherId> otherIdList = query.getResultList();

        return otherIdList.size()>0 ? otherIdList.get(0) : null;
    }

    public OtherId searchTable(Integer tableName, String otherKey, String otherValue){
        Query query = entityManager.createQuery("select o from OtherId o where o.tableName=? and o.otherKey=? and o.otherId=? and o.deleted=? order by o.id desc");
        query.setParameter(1, tableName);
        query.setParameter(2, otherKey);
        query.setParameter(3, otherValue);
        query.setParameter(4, false);

        @SuppressWarnings("unchecked")
        List<OtherId> otherIdList = query.getResultList();

        return otherIdList.size()>0 ? otherIdList.get(0) : null;
    }

    public void save(OtherId otherId) {
        if(otherId.getId() != null && otherId.getId().intValue() > 0 ) {
            merge(otherId);
        } else {
            persist(otherId);
        }
    }
}
