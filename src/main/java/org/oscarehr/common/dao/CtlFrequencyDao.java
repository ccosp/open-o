package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.CtlFrequency;

public interface CtlFrequencyDao extends AbstractDao<CtlFrequency> {
    List<CtlFrequency> findAll();
}
