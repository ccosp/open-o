package org.oscarehr.common.dao;

import java.util.Properties;
import org.oscarehr.common.model.FrmLabReqPreSet;

public interface FrmLabReqPreSetDao extends AbstractDao<FrmLabReqPreSet> {
    Properties fillPropertiesByLabType(String labType, Properties prop);
}
