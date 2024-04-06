package org.oscarehr.common.dao;

import java.util.List;
import java.util.UUID;

import javax.persistence.Query;

import org.oscarehr.common.model.OscarAnnotation;
import org.springframework.stereotype.Repository;

@Repository
public class OscarAnnotationDaoImpl extends AbstractDaoImpl<OscarAnnotation> implements OscarAnnotationDao {

	public OscarAnnotationDaoImpl() {
		super(OscarAnnotation.class);
	}

    public OscarAnnotation getAnnotations(String demoNo, String tableName,Long tableId){
    	Query query = entityManager.createQuery("select a from OscarAnnotation a where a.demographicNo=? and a.tableName=? and a.tableId=?");
    	query.setParameter(1, demoNo);
    	query.setParameter(2, tableName);
    	query.setParameter(3, tableId);
        @SuppressWarnings("unchecked")
        List<OscarAnnotation> codeList = query.getResultList();
        if(codeList.size()>0) {
        	return codeList.get(0);
        }
        return null;
    }

    public void save(OscarAnnotation anno) {
        if (anno.isUuidSet()){
            UUID uuid = UUID.randomUUID();
            anno.setUuid(uuid.toString());
        }
        persist(anno);
    }

   public int getNumberOfNotes(String demoNo, String tableName,Long tableId){
   	Query query = entityManager.createQuery("select a from OscarAnnotation a where a.demographicNo=? and a.tableName=? and a.tableId=?");
   	query.setParameter(1, demoNo);
   	query.setParameter(2, tableName);
   	query.setParameter(3,tableId);

   	@SuppressWarnings("unchecked")
    List<OscarAnnotation> codeList = query.getResultList();

   	return codeList.size();
   }
}
