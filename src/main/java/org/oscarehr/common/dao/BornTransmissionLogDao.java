package org.oscarehr.common.dao;

import java.util.Date;

public interface BornTransmissionLogDao extends AbstractDao<BornTransmissionLog>  {
    Long getSeqNoToday(String filenameStart, Integer id);
}
