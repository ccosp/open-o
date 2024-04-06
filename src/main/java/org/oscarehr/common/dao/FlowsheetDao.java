package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.Flowsheet;

public interface FlowsheetDao extends AbstractDao<Flowsheet> {
    List<Flowsheet> findAll();
    Flowsheet findByName(String name);
}
