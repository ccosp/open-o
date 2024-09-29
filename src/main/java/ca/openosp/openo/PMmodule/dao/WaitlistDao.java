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
package ca.openosp.openo.PMmodule.dao;

import java.util.Collection;
import java.util.List;

import ca.openosp.openo.PMmodule.wlmatch.CriteriasBO;
import ca.openosp.openo.PMmodule.wlmatch.MatchBO;
import ca.openosp.openo.PMmodule.wlmatch.VacancyDisplayBO;
import ca.openosp.openo.common.model.EFormData;
import ca.openosp.openo.match.client.ClientData;
import ca.openosp.openo.match.vacancy.VacancyData;

public interface WaitlistDao {

    public List<MatchBO> getClientMatches(int vacancyId);

    public List<MatchBO> getClientMatchesWithMinPercentage(int vacancyId, double percentage);

    public Collection<EFormData> searchForMatchingEforms(CriteriasBO crits);

    public List<VacancyDisplayBO> listDisplayVacanciesForWaitListProgram(int programID);

    public List<VacancyDisplayBO> listDisplayVacanciesForAllWaitListPrograms();

    public List<VacancyDisplayBO> getDisplayVacanciesForAgencyProgram(int programID);

    public VacancyDisplayBO getDisplayVacancy(int vacancyID);

    public void loadStats(VacancyDisplayBO bo);

    public Integer getProgramIdByVacancyId(int vacancyId);

    public List<VacancyDisplayBO> listNoOfVacanciesForWaitListProgram();

    public List<VacancyDisplayBO> listVacanciesForWaitListProgram();

    public List<ClientData> getAllClientsData();

    public List<ClientData> getAllClientsDataByProgramId(int wlProgramId);

    public ClientData getClientData(int clientId);

    // private static final String field_type_one = "select_one";
    // private static final String field_type_number = "number";

    public VacancyData loadVacancyData(final int vacancyId);

    public VacancyData loadVacancyData(final int vacancyId, final int wlProgramId);

}
