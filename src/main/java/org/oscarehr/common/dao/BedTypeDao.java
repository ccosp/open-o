package org.oscarehr.common.dao;

import org.oscarehr.common.model.BedType;

public interface BedTypeDao extends AbstractDao<BedType> {
    boolean bedTypeExists(Integer bedTypeId);
    BedType getBedType(Integer bedTypeId);
    BedType[] getBedTypes();
}
