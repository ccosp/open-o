package org.oscarehr.common.dao;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import org.apache.commons.lang.StringEscapeUtils;
import org.oscarehr.common.model.ProfessionalContact;
import org.springframework.stereotype.Repository;

@Repository
public class ProfessionalContactDaoImpl extends AbstractDaoImpl<ProfessionalContact> implements ProfessionalContactDao {

    public ProfessionalContactDaoImpl() {
        super(ProfessionalContact.class);
    }
    
    @Deprecated
    @SuppressWarnings("unchecked")
    @Override
    public List<ProfessionalContact> findAll() {
        Query query = createQuery("x", null);
        return query.getResultList();
    }
    
    @Override
    public List<ProfessionalContact> search(String searchMode, String orderBy, String keyword) {
        StringBuilder where = new StringBuilder();
        List<String> paramList = new ArrayList<String>();
        
        if(searchMode.equals("search_name")) {
            String[] temp = keyword.split("\\,\\p{Space}*");
            if(temp.length>1) {
              where.append("c.lastName like ?1 and c.firstName like ?2");
              paramList.add(temp[0]+"%");
              paramList.add(temp[1]+"%");
            } else {
              where.append("c.lastName like ?1");
              paramList.add(temp[0]+"%");
            }
        }else {        
            where.append("c." + StringEscapeUtils.escapeSql(searchMode) + " like ?1");
            paramList.add(keyword+"%");
        }            
        String sql = "SELECT c from ProfessionalContact c where " + where.toString() + " order by " + orderBy;
        
        Query query = entityManager.createQuery(sql);
        for(int x=0;x<paramList.size();x++) {
            query.setParameter(x+1,paramList.get(x));
        }        
        
        @SuppressWarnings("unchecked")
        List<ProfessionalContact> contacts = query.getResultList();
        return contacts;
    }
}
