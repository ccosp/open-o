package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;

public interface MdsMSHDao extends AbstractDao<MdsMSH> {
    List<Object[]> findLabsByAccessionNumAndId(Integer id, String controlId);
    List<Object[]> findMdsSementDataById(Integer id);
    List<Integer> getLabResultsSince(Integer demographicNo, Date updateDate);
}
