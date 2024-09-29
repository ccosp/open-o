//CHECKSTYLE:OFF
/**
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
 */
package ca.openosp.openo.ws.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import ca.openosp.openo.casemgmt.dao.IssueDAO;
import ca.openosp.openo.casemgmt.model.Issue;
import ca.openosp.openo.common.dao.DxresearchDAO;
import ca.openosp.openo.common.dao.QuickListDao;
import ca.openosp.openo.common.model.Dxresearch;
import ca.openosp.openo.common.model.QuickList;
import ca.openosp.openo.ws.rest.to.model.DiagnosisTo1;
import ca.openosp.openo.ws.rest.to.model.DxQuickList;
import ca.openosp.openo.ws.rest.to.model.IssueTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ca.openosp.openo.log.LogAction;

@Path("/dxRegisty")
public class DiseaseRegistryService extends AbstractServiceImpl {

    @Autowired
    QuickListDao quickListDao;

    @Autowired
    @Qualifier("DxresearchDAO")
    protected DxresearchDAO dxresearchDao;

    @Autowired
    @Qualifier("IssueDAO")
    private IssueDAO issueDao;

    @GET
    @Path("/quickLists")
    @Produces("application/json")
    public List<DxQuickList> getQuickLists() {

        Map<String, DxQuickList> quickListMap = new HashMap<String, DxQuickList>();

        List<QuickList> quicklists = quickListDao.findAll();

        for (QuickList item : quicklists) {
            DxQuickList dxList = quickListMap.get(item.getQuickListName());
            if (dxList == null) {
                dxList = new DxQuickList();
                dxList.setLabel(item.getQuickListName());
                quickListMap.put(item.getQuickListName(), dxList);
            }

            String desc = dxresearchDao.getDescription(item.getCodingSystem(), item.getDxResearchCode());
            if (desc != null) {
                DiagnosisTo1 dx = new DiagnosisTo1();
                dx.setCode(item.getDxResearchCode());
                dx.setCodingSystem(item.getCodingSystem());
                dx.setDescription(desc);
                dxList.getDxList().add(dx);
            }
        }


        List<DxQuickList> returnQuickLists = new ArrayList<DxQuickList>(quickListMap.values());
        return returnQuickLists;
    }

    @POST
    @Path("/findLikeIssue")
    @Produces("application/json")
    @Consumes("application/json")
    public Response findLikeIssues(DiagnosisTo1 dx) {
        Issue issue = issueDao.findIssueByTypeAndCode(dx.getCodingSystem(), dx.getCode());
        IssueTo1 returnIssue = new IssueTo1();
        returnIssue.setCode(issue.getCode());
        returnIssue.setDescription(issue.getDescription());
        returnIssue.setId(issue.getId());
        returnIssue.setType(issue.getType());
        returnIssue.setPriority(issue.getPriority());
        returnIssue.setRole(issue.getRole());
        returnIssue.setUpdate_date(issue.getUpdate_date());
        returnIssue.setSortOrderId(issue.getSortOrderId());
        return Response.ok(returnIssue).build();
    }

    @POST
    @Path("/{demographicNo}/add")
    @Produces("application/json")
    @Consumes("application/json")
    public Response addToDiseaseRegistry(@PathParam("demographicNo") Integer demographicNo, IssueTo1 issue) {
        boolean activeEntryExists = dxresearchDao.activeEntryExists(demographicNo, issue.getType(), issue.getCode());

        if (!activeEntryExists) {
            Dxresearch dx = new Dxresearch();
            dx.setStartDate(new Date());
            dx.setCodingSystem(issue.getType());
            dx.setDemographicNo(demographicNo);
            dx.setDxresearchCode(issue.getCode());
            dx.setStatus('A');
            dx.setProviderNo(getCurrentProvider().getProviderNo());
            dxresearchDao.persist(dx);
            LogAction.addLog(getLoggedInInfo(), "Dxresearch.add", "dxresearch", "" + dx.getId(), "" + demographicNo, dx.toString());
        }

        return Response.ok().build();
    }

    @GET
    @Path("/getDiseaseRegistry")
    @Produces("application/json")
    @Consumes("application/json")
    public Response getDiseaseRegistry(@QueryParam("demographicNo") Integer demographicNo) {
        List<Dxresearch> dxresearchList = dxresearchDao.getByDemographicNo(demographicNo);
        return Response.ok(dxresearchList).build();
    }

}
