package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.MdsZMN;

public interface MdsZMNDao extends AbstractDao<MdsZMN> {
    MdsZMN findBySegmentIdAndReportName(Integer id, String reportName);
    MdsZMN findBySegmentIdAndResultMnemonic(Integer id, String rm);
    List<String> findResultCodes(Integer id, String reportSequence);
}
