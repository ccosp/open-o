package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.CdsFormOption;

public interface CdsFormOptionDao extends AbstractDao<CdsFormOption> {
    List<CdsFormOption> findByVersionAndCategory(String formVersion, String mainCatgeory);
    List<CdsFormOption> findByVersion(String formVersion);
}
