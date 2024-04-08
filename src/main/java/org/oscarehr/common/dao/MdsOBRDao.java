package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.MdsOBR;

public interface MdsOBRDao extends AbstractDao<MdsOBR> {
    List<Object[]> findByIdAndResultCodes(Integer id, List<String> resultCodes);
}
