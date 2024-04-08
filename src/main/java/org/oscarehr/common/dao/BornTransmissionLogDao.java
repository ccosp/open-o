package org.oscarehr.common.dao;

import java.util.Date;
import org.oscarehr.common.model.BornTransmissionLog;

public interface BornTransmissionLogDao extends AbstractDao<BornTransmissionLog>  {
    Long getSeqNoToday(String filenameStart, Integer id);
}
