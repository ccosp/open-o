package org.oscarehr.common.dao;

import java.util.List;

public interface MdsOBRDao extends AbstractDao<MdsOBR> {
    List<Object[]> findByIdAndResultCodes(Integer id, List<String> resultCodes);
}
