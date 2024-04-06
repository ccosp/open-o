package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.MeasurementType;

public interface MeasurementTypeDao extends AbstractDao<MeasurementType> {
    List<MeasurementType> findAll();
    List<MeasurementType> findAllOrderByName();
    List<MeasurementType> findAllOrderById();
    List<MeasurementType> findByType(String type);
    List<MeasurementType> findByMeasuringInstructionAndTypeDisplayName(String measuringInstruction, String typeDisplayName);
    List<MeasurementType> findByTypeDisplayName(String typeDisplayName);
    List<MeasurementType> findByTypeAndMeasuringInstruction(String type, String measuringInstruction);
    List<Object> findUniqueTypeDisplayNames();
}
