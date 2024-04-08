package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.MdsOBX;

public interface MdsOBXDao extends AbstractDao<MdsOBX> {
    List<MdsOBX> findByIdObrAndCodes(Integer id, String associatedOBR, List<String> codes);
}
