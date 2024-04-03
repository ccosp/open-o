package org.oscarehr.common.dao;

import org.oscarehr.common.model.FaxConfig;

public interface FaxConfigDao extends AbstractDao<FaxConfig>{
    FaxConfig getConfigByNumber(String number);
    FaxConfig getActiveConfigByNumber(String number);
}