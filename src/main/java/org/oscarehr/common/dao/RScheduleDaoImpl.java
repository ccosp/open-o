package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.RSchedule;
import org.springframework.stereotype.Repository;

@Repository(value="rScheduleDao")
@SuppressWarnings("unchecked")
public class RScheduleDaoImpl extends AbstractDaoImpl<RSchedule> implements RScheduleDao {

    public RScheduleDaoImpl() {
        super(RSchedule.class);
    }

    // ... rest of the methods implementation ...

}
