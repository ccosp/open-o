package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.CountryCode;
import org.springframework.stereotype.Repository;

@Repository
public class CountryCodeDaoImpl extends AbstractDaoImpl<CountryCode> implements CountryCodeDao {

    public CountryCodeDaoImpl() {
    	super(CountryCode.class);
    }
    
    public List<CountryCode> findAll(){
    	Query query = entityManager.createQuery("SELECT cc from CountryCode cc");
        @SuppressWarnings("unchecked")
        List<CountryCode> codeList = query.getResultList();
        return codeList;
    }

    public List<CountryCode> getAllCountryCodes(){
    	return findAll();
    }

    public List<CountryCode> getAllCountryCodes(String locale){
    	Query query = entityManager.createQuery("SELECT cc from CountryCode cc where cc.clocale = ?");
    	query.setParameter(1, locale);
        @SuppressWarnings("unchecked")
        List<CountryCode> codeList = query.getResultList();
        return codeList;
    }

    public CountryCode getCountryCode(String countryCode){
    	Query query = entityManager.createQuery("SELECT cc from CountryCode  cc where cc.countryId = ?");
    	query.setParameter(1, countryCode);
        @SuppressWarnings("unchecked")
        List<CountryCode> codeList = query.getResultList();
        if (codeList.size() >0){
            return  codeList.get(0);
        }
        return null;
    }

    public CountryCode getCountryCode(String countryCode,String locale){
    	Query query = entityManager.createQuery("SELECT cc from CountryCode cc where cc.countryId = ? and cc.clocale=?");
    	query.setParameter(1, countryCode);
        query.setParameter(2, locale);
        @SuppressWarnings("unchecked")
        List<CountryCode> codeList = query.getResultList();

    	CountryCode code = null;
        if (codeList != null && codeList.size() >0){
            code = codeList.get(0);
        }
        return code;
    }
}
