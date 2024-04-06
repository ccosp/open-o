package org.oscarehr.common.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Query;
import org.oscarehr.common.model.View;
import org.springframework.stereotype.Repository;

@Repository
public class ViewDaoImpl extends AbstractDaoImpl<View> implements ViewDao {

    public ViewDaoImpl() {
        super(View.class);
    }

    @Override
    public Map<String, View> getView(String view, String role) {
        Query query = entityManager.createQuery("select v from View v where v.view_name=? and v.role=? and v.providerNo is null");
        query.setParameter(1, view);
        query.setParameter(2, role);
        return getView(query);
    }

    @Override
    public Map<String, View> getView(String view, String role, String providerNo) {
        Query query = entityManager.createQuery("select v from View v where v.view_name=? and v.role=? and v.providerNo = ?");
        query.setParameter(1, view);
        query.setParameter(2, role);
        query.setParameter(3, providerNo);
        return getView(query);
    }

    private Map<String, View> getView(Query query) {
        List<View> list = query.getResultList();
        Map<String,View>map = new HashMap<String,View>();
        for( View v : list ) {
            map.put(v.getName(),v);
        }
        return map;
    }

    @Override
    public void saveView(View v) {
        if(v != null && v.getId() != null && v.getId() > 0) {
            merge(v);
        } else {
            persist(v);
        }
    }

    @Override
    public void delete(View v) {
        remove(v.getId());
    }
}
