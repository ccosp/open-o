package org.oscarehr.common.dao;

import org.oscarehr.common.model.OtherId;

public interface OtherIdDao extends AbstractDao<OtherId> {
    OtherId getOtherId(Integer tableName, Integer tableId, String otherKey);
    OtherId getOtherId(Integer tableName, String tableId, String otherKey);
    OtherId searchTable(Integer tableName, String otherKey, String otherValue);
    void save(OtherId otherId);
}
