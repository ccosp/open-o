package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.ScheduleDate;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class ScheduleDateDaoImpl extends AbstractDaoImpl<ScheduleDate> implements ScheduleDateDao {

    public ScheduleDateDaoImpl() {
        super(ScheduleDate.class);
    }

    // ... rest of the methods from the original ScheduleDateDao class ...
}
