package org.oscarehr.common.dao;

import java.util.Date;

public interface BornTransmissionLogDao {
    Long getSeqNoToday(String filenameStart, Integer id);
}
