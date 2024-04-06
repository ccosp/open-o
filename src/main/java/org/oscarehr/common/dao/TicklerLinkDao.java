package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.TicklerLink;

public interface TicklerLinkDao extends AbstractDao<TicklerLink> {
    TicklerLink getTicklerLink(Integer id);
    List<TicklerLink> getLinkByTableId(String tableName, Long tableId);
    List<TicklerLink> getLinkByTickler(Integer ticklerNo);
    void save(TicklerLink cLink);
    void update(TicklerLink cLink);
}
