package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;
import org.oscarehr.common.model.MdsMSH;

public interface MdsMSHDao extends AbstractDao<MdsMSH> {
    List<Object[]> findLabsByAccessionNumAndId(Integer id, String controlId);
    List<Object[]> findMdsSementDataById(Integer id);
    List<Integer> getLabResultsSince(Integer demographicNo, Date updateDate);
}
