package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.FlowSheetCustomization;
import org.springframework.stereotype.Repository;

@Repository
public class FlowSheetCustomizationDaoImpl extends AbstractDaoImpl<FlowSheetCustomization> implements FlowSheetCustomizationDao {

    public FlowSheetCustomizationDaoImpl() {
        super(FlowSheetCustomization.class);
    }

    @Override
    public FlowSheetCustomization getFlowSheetCustomization(Integer id){
        return this.find(id);
    }

    @Override
    public List<FlowSheetCustomization> getFlowSheetCustomizations(String flowsheet, String provider, Integer demographic){
        Query query = entityManager.createQuery("SELECT fd FROM FlowSheetCustomization fd WHERE fd.flowsheet=? and fd.archived=0 and ( fd.providerNo='' or (fd.providerNo=? and fd.demographicNo=0) or (fd.providerNo=? and fd.demographicNo=?) ) order by fd.providerNo, fd.demographicNo");
        query.setParameter(1, flowsheet);
        query.setParameter(2, provider);
        query.setParameter(3, provider);
        query.setParameter(4, String.valueOf(demographic));

        @SuppressWarnings("unchecked")
        List<FlowSheetCustomization> list = query.getResultList();
        return list;
    }

    @Override
    public List<FlowSheetCustomization> getFlowSheetCustomizations(String flowsheet, String provider){
        Query query = entityManager.createQuery("SELECT fd FROM FlowSheetCustomization fd WHERE fd.flowsheet=? and fd.archived=0 and fd.providerNo = ?  and fd.demographicNo = 0");
        query.setParameter(1, flowsheet);
        query.setParameter(2, provider);

        @SuppressWarnings("unchecked")
        List<FlowSheetCustomization> list = query.getResultList();
        return list;
    }

    @Override
    public List<FlowSheetCustomization> getFlowSheetCustomizations(String flowsheet){
        Query query = entityManager.createQuery("SELECT fd FROM FlowSheetCustomization fd WHERE fd.flowsheet=? and fd.archived=0 and fd.providerNo = ''  and fd.demographicNo = 0");
        query.setParameter(1, flowsheet);

        @SuppressWarnings("unchecked")
        List<FlowSheetCustomization> list = query.getResultList();
        return list;
    }

    @Override
    public List<FlowSheetCustomization> getFlowSheetCustomizationsForPatient(String flowsheet, String demographicNo){
        Query query = entityManager.createQuery("SELECT fd FROM FlowSheetCustomization fd WHERE fd.flowsheet=? and fd.archived=0 and fd.demographicNo = ?");
        query.setParameter(1, flowsheet);
        query.setParameter(2, demographicNo);

        @SuppressWarnings("unchecked")
        List<FlowSheetCustomization> list = query.getResultList();
        return list;
    }
}
