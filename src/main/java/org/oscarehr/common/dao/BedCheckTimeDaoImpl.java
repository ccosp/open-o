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

    private static final Logger logger = MiscUtils.getLogger();

    @Override
    public boolean bedCheckTimeExists(Integer programId, Date time) {
        // Implement the method here
    }

    @Override
    public BedCheckTime getBedCheckTime(Integer id) {
        // Implement the method here
    }

    @Override
    public BedCheckTime[] getBedCheckTimes(Integer programId) {
        // Implement the method here
    }

    @Override
    public void saveBedCheckTime(BedCheckTime bedCheckTime) {
        // Implement the method here
    }

    @Override
    public void deleteBedCheckTime(BedCheckTime bedCheckTime) {
        // Implement the method here
    }

    @Override
    public String getBedCheckTimesQuery(Integer programId) {
        // Implement the method here
    }

    @Override
    public Object[] getBedCheckTimesValues(Integer programId) {
        // Implement the method here
    }

    @Override
    public List<BedCheckTime> getBedCheckTimes(String queryStr, Object[] values) {
        // Implement the method here
    }
}
