package org.oscarehr.common.dao;

import org.apache.logging.log4j.Logger;
import org.oscarehr.common.model.IntegratorControl;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class IntegratorControlDaoImpl extends AbstractDaoImpl<IntegratorControl> implements IntegratorControlDao {

    public static final String REMOVE_DEMO_ID_CTRL = "RemoveDemographicIdentity";
    public static final String UPDATE_INTERVAL_CTRL = "UpdateInterval";
    public static final String INTERVAL_HR = "h";
    private static final Logger log=MiscUtils.getLogger();

    public IntegratorControlDaoImpl() {
        super(IntegratorControl.class);
    }

    // ... rest of the methods implementation from the original IntegratorControlDao class
}
