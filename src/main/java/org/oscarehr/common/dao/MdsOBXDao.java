package org.oscarehr.common.dao;

import java.util.List;

public interface MdsOBXDao extends AbstractDao<MdsOBX> {
    List<MdsOBX> findByIdObrAndCodes(Integer id, String associatedOBR, List<String> codes);
}
