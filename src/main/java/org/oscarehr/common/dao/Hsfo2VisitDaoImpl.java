package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.Hsfo2Visit;
import org.springframework.stereotype.Repository;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.oscarehr.util.MiscUtils;
import org.apache.logging.log4j.Logger;

@Repository
public class Hsfo2VisitDaoImpl extends AbstractDaoImpl<Hsfo2Visit> implements Hsfo2VisitDao {
    private static Logger logger = MiscUtils.getLogger();

    public Hsfo2VisitDaoImpl() {
        super(Hsfo2Visit.class);
    }

    // ... rest of the methods implementation ...
}
