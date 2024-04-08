package org.oscarehr.common.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.Logger;
import org.oscarehr.common.model.BedCheckTime;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Repository;

@Repository
public class BedCheckTimeDaoImpl extends AbstractDaoImpl<BedCheckTime> implements BedCheckTimeDao {

    private Logger log = MiscUtils.getLogger();

    public BedCheckTimeDaoImpl() {
		super(BedCheckTime.class);
	}

    @Override
    public boolean bedCheckTimeExists(Integer programId, Date time) {
        Query query = entityManager.createQuery("select b from BedCheckTime b where b.programId = ? and b.time = ?");
    	query.setParameter(1, programId);
    	query.setParameter(2, time);
    	
    	@SuppressWarnings("unchecked")
    	List<BedCheckTime> bedCheckTimes = query.getResultList();
    	
        log.debug("bedCheckTimeExists: " + (bedCheckTimes.size() > 0));

        return bedCheckTimes.size() > 0;
    }

    @Deprecated
    @Override
    public BedCheckTime getBedCheckTime(Integer id) {
        return find(id);
    }

    @Override
    public BedCheckTime[] getBedCheckTimes(Integer programId) {
        String query = getBedCheckTimesQuery(programId);
        Object[] values = getBedCheckTimesValues(programId);
        List<BedCheckTime> bedCheckTimes = getBedCheckTimes(query, values);
        log.debug("getBedCheckTimes: size " + bedCheckTimes.size());

        return bedCheckTimes.toArray(new BedCheckTime[bedCheckTimes.size()]);
    }

    @Deprecated
    @Override
    public void saveBedCheckTime(BedCheckTime bedCheckTime) {
        if(bedCheckTime == null)
    		return;
    	if(bedCheckTime.getId() == null || bedCheckTime.getId().intValue() == 0)
    		persist(bedCheckTime);
    	else
    		merge(bedCheckTime);
       
        log.debug("saveBedCheckTime: id " + bedCheckTime.getId());
    }

    @Deprecated
    @Override
    public void deleteBedCheckTime(BedCheckTime bedCheckTime) {
        remove(bedCheckTime.getId());
 
        log.debug("deleteBedCheckTime: " + bedCheckTime);
    }

    @Override
    public String getBedCheckTimesQuery(Integer programId) {
        StringBuilder queryBuilder = new StringBuilder("select bct from BedCheckTime bct");

        if (programId != null) {
            queryBuilder.append(" where ");
        }

        if (programId != null) {
            queryBuilder.append("bct.programId = ?");
        }

        queryBuilder.append(" order by bct.time asc");

        return queryBuilder.toString();
    }

    @Override
    public Object[] getBedCheckTimesValues(Integer programId) {
        List<Object> values = new ArrayList<Object>();

        if (programId != null) {
            values.add(programId);
        }

        return values.toArray(new Object[values.size()]);
    }

    @Override
    public List<BedCheckTime> getBedCheckTimes(String queryStr, Object[] values) {
        Query query = entityManager.createQuery(queryStr);
    	if(values != null) {
	    	for(int x=0;x<values.length;x++) {
	    		query.setParameter(x+1, values[x]);
	    	}
    	}
    	
    	@SuppressWarnings("unchecked")
    	List<BedCheckTime> results = query.getResultList();
    	
    	return results;
    }
}
