package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.MdsZRG;

public interface MdsZRGDao extends AbstractDao<MdsZRG> {
    List<Object[]> findById(Integer id);
    List<Object> findReportGroupHeadingsById(Integer id, String reportGroupId);
}
