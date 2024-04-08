package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.MeasurementGroupStyle;

public interface MeasurementGroupStyleDao extends AbstractDao<MeasurementGroupStyle>{
    List<MeasurementGroupStyle> findAll();
    List<MeasurementGroupStyle> findByGroupName(String groupName);
    List<MeasurementGroupStyle> findByCssId(Integer cssId);
}
