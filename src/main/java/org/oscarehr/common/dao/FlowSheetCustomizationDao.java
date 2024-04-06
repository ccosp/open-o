package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.FlowSheetCustomization;

public interface FlowSheetCustomizationDao extends AbstractDao<FlowSheetCustomization> {
    FlowSheetCustomization getFlowSheetCustomization(Integer id);
    List<FlowSheetCustomization> getFlowSheetCustomizations(String flowsheet, String provider, Integer demographic);
    List<FlowSheetCustomization> getFlowSheetCustomizations(String flowsheet, String provider);
    List<FlowSheetCustomization> getFlowSheetCustomizations(String flowsheet);
    List<FlowSheetCustomization> getFlowSheetCustomizationsForPatient(String flowsheet, String demographicNo);
}
