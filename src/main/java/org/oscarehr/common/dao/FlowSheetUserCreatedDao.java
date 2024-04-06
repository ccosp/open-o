package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.FlowSheetUserCreated;

public interface FlowSheetUserCreatedDao extends AbstractDao<FlowSheetUserCreated> {

    List<FlowSheetUserCreated> findActiveNoTemplate();

    List<FlowSheetUserCreated> getAllUserCreatedFlowSheets();

    List<FlowSheetUserCreated> findActiveByScope(String scope);

    FlowSheetUserCreated findByPatientScope(String template, Integer demographicNo);

    FlowSheetUserCreated findByProviderScope(String template, String providerNo);

    FlowSheetUserCreated findByClinicScope(String template);

    FlowSheetUserCreated findByPatientScopeName(String name, Integer demographicNo);

    FlowSheetUserCreated findByProviderScopeName(String name, String providerNo);

    FlowSheetUserCreated findByClinicScopeName(String name);
}
