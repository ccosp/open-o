package org.oscarehr.common.dao;

import java.security.MessageDigest;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.net.util.Base64;
import org.oscarehr.common.model.RemoteIntegratedDataCopy;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.ObjectMarshalUtil;
import org.springframework.stereotype.Repository;

@Repository
public class RemoteIntegratedDataCopyDaoImpl extends AbstractDaoImpl<RemoteIntegratedDataCopy> implements RemoteIntegratedDataCopyDao {

    public RemoteIntegratedDataCopyDaoImpl() {
        super(RemoteIntegratedDataCopy.class);
    }

    // ... rest of the methods implementation goes here ...

}
