package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.CssStyle;
import org.springframework.stereotype.Repository;

@Repository
public class CSSStylesDaoImpl extends AbstractDaoImpl<CssStyle> implements CSSStylesDAO {
    
    public CSSStylesDaoImpl() {
    	super(CssStyle.class);
    }
   
    @SuppressWarnings("unchecked")
    public List<CssStyle> findAll() {
    	String sql = "select css from CssStyle css where css.status = (:status) order by css.name";
    	Query q = entityManager.createQuery(sql);
    	q.setParameter("status", CssStyle.ACTIVE);
    	
    	return q.getResultList();
    }            
}
