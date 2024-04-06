package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.LookupListItem;

public interface LookupListItemDao extends AbstractDao<LookupListItem> {
    List<LookupListItem> findActiveByLookupListId(int lookupListId);
    List<LookupListItem> findByLookupListId(int lookupListId, boolean active);
    LookupListItem findByLookupListIdAndValue(int lookupListId, String value);
}
