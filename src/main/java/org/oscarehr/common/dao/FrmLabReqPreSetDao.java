package org.oscarehr.common.dao;

import java.util.Properties;

public interface FrmLabReqPreSetDao extends AbstractDao<FrmLabReqPreSet> {
    Properties fillPropertiesByLabType(String labType, Properties prop);
}
