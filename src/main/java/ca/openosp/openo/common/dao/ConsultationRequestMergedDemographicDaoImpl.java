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

import ca.openosp.openo.common.merge.MergedDemographicTemplate;
import ca.openosp.openo.common.model.ConsultationRequest;
import org.springframework.stereotype.Repository;

@Repository("consultationRequestDao")
public class ConsultationRequestMergedDemographicDaoImpl extends ConsultationRequestDaoImpl implements ConsultationRequestMergedDemographicDao {

    @Override
    public List<ConsultationRequest> getConsults(Integer demoNo) {
        List<ConsultationRequest> result = super.getConsults(demoNo);
        MergedDemographicTemplate<ConsultationRequest> template = new MergedDemographicTemplate<ConsultationRequest>() {
            @Override
            protected List<ConsultationRequest> findById(Integer demographic_no) {
                return ConsultationRequestMergedDemographicDaoImpl.super.getConsults(demographic_no);
            }
        };
        return template.findMerged(demoNo, result);
    }

    @Override
    public List<ConsultationRequest> getConsultationsByStatus(Integer demographicNo, final String status) {
        List<ConsultationRequest> result = super.getConsultationsByStatus(demographicNo, status);
        MergedDemographicTemplate<ConsultationRequest> template = new MergedDemographicTemplate<ConsultationRequest>() {
            @Override
            protected List<ConsultationRequest> findById(Integer demographic_no) {
                return ConsultationRequestMergedDemographicDaoImpl.super.getConsultationsByStatus(demographic_no, status);
            }
        };
        return template.findMerged(demographicNo, result);
    }

}
