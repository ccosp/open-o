package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.OcanConnexOption;

public interface OcanConnexOptionDao extends AbstractDao<OcanConnexOption> {
    List<OcanConnexOption> findByLHINCode(String LHIN_code);
    List<OcanConnexOption> findByLHINCodeOrgName(String LHIN_code, String orgName);
    List<OcanConnexOption> findByLHINCodeOrgNameProgramName(String LHIN_code, String orgName, String programName);
    OcanConnexOption findByID(Integer connexOptionId);
}
