package org.oscarehr.common.dao;

import org.oscarehr.common.model.RemoteIntegratedDataCopy;

import java.util.List;

public interface RemoteIntegratedDataCopyDao extends AbstractDao<RemoteIntegratedDataCopy> {
    void archiveDataCopyExceptThisOne(RemoteIntegratedDataCopy remoteIntegratedDataCopy);
    RemoteIntegratedDataCopy findByDemoType(Integer facilityId, Integer demographicNo,String dataType);
    RemoteIntegratedDataCopy findByType(Integer facilityId,String dataType);
    RemoteIntegratedDataCopy findByDemoTypeSignature(Integer facilityId, Integer demographicNo,String dataType,String signature);
    RemoteIntegratedDataCopy save(Integer demographicNo, Object obj,String providerNo, Integer facilityId) throws Exception;
    RemoteIntegratedDataCopy save(Integer demographicNo, Object obj,String providerNo, Integer facilityId,String type) throws Exception;
    <T> T getObjectFrom (Class<T> clazz, RemoteIntegratedDataCopy rid) throws Exception;
}
