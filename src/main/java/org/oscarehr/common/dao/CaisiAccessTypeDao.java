package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.CaisiAccessType;

public interface CaisiAccessTypeDao {
    List<CaisiAccessType> findAll();
    CaisiAccessType findByName(String name);
}
