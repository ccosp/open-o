//CHECKSTYLE:OFF
/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * <p>
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 * <p>
 * Modifications made by Magenta Health in 2024.
 */
package ca.openosp.openo.common.dao;

import java.util.List;
import javax.persistence.Query;

import ca.openosp.openo.common.model.FlowSheetCustomization;
import org.springframework.stereotype.Repository;

@Repository
public class FlowSheetCustomizationDaoImpl extends AbstractDaoImpl<FlowSheetCustomization> implements FlowSheetCustomizationDao {

    public FlowSheetCustomizationDaoImpl() {
        super(FlowSheetCustomization.class);
    }

    @Override
    public FlowSheetCustomization getFlowSheetCustomization(Integer id) {
        return this.find(id);
    }

    @Override
    public List<FlowSheetCustomization> getFlowSheetCustomizations(String flowsheet, String provider, Integer demographic) {
        Query query = entityManager.createQuery("SELECT fd FROM FlowSheetCustomization fd WHERE fd.flowsheet=? and fd.archived=0 and ( fd.providerNo='' or (fd.providerNo=? and fd.demographicNo=0) or (fd.providerNo=? and fd.demographicNo=?) ) order by fd.providerNo, fd.demographicNo");
        query.setParameter(0, flowsheet);
        query.setParameter(1, provider);
        query.setParameter(2, provider);
        query.setParameter(3, String.valueOf(demographic));

        @SuppressWarnings("unchecked")
        List<FlowSheetCustomization> list = query.getResultList();
        return list;
    }

    @Override
    public List<FlowSheetCustomization> getFlowSheetCustomizations(String flowsheet, String provider) {
        Query query = entityManager.createQuery("SELECT fd FROM FlowSheetCustomization fd WHERE fd.flowsheet=? and fd.archived=0 and fd.providerNo = ?  and fd.demographicNo = 0");
        query.setParameter(0, flowsheet);
        query.setParameter(1, provider);

        @SuppressWarnings("unchecked")
        List<FlowSheetCustomization> list = query.getResultList();
        return list;
    }

    @Override
    public List<FlowSheetCustomization> getFlowSheetCustomizations(String flowsheet) {
        Query query = entityManager.createQuery("SELECT fd FROM FlowSheetCustomization fd WHERE fd.flowsheet=? and fd.archived=0 and fd.providerNo = ''  and fd.demographicNo = 0");
        query.setParameter(0, flowsheet);

        @SuppressWarnings("unchecked")
        List<FlowSheetCustomization> list = query.getResultList();
        return list;
    }

    @Override
    public List<FlowSheetCustomization> getFlowSheetCustomizationsForPatient(String flowsheet, String demographicNo) {
        Query query = entityManager.createQuery("SELECT fd FROM FlowSheetCustomization fd WHERE fd.flowsheet=? and fd.archived=0 and fd.demographicNo = ?");
        query.setParameter(0, flowsheet);
        query.setParameter(1, demographicNo);

        @SuppressWarnings("unchecked")
        List<FlowSheetCustomization> list = query.getResultList();
        return list;
    }
}
