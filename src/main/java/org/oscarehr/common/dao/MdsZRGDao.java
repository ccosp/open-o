package org.oscarehr.common.dao;

import java.util.List;

public interface MdsZRGDao extends AbstractDao<MdsZRG> {
    List<Object[]> findById(Integer id);
    List<Object> findReportGroupHeadingsById(Integer id, String reportGroupId);
}
