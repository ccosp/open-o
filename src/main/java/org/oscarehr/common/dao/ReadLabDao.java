package org.oscarehr.common.dao;

import org.oscarehr.common.model.ReadLab;

public interface ReadLabDao extends AbstractDao<ReadLab> {
    void markAsRead(String providerNo, String labType, Integer labId);
    boolean isRead(String providerNo, String labType, Integer labId);
    ReadLab getByProviderNoAndLabTypeAndLabId(String providerNo, String labType, Integer labId);
}
