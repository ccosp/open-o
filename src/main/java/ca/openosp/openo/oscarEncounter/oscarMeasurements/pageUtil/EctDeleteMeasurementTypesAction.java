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


package ca.openosp.openo.oscarEncounter.oscarMeasurements.pageUtil;

import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import ca.openosp.openo.common.dao.MeasurementGroupDao;
import ca.openosp.openo.common.dao.MeasurementTypeDao;
import ca.openosp.openo.common.dao.MeasurementTypeDeletedDao;
import ca.openosp.openo.common.model.MeasurementGroup;
import ca.openosp.openo.common.model.MeasurementType;
import ca.openosp.openo.common.model.MeasurementTypeDeleted;
import ca.openosp.openo.managers.SecurityInfoManager;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.MiscUtils;
import ca.openosp.openo.ehrutil.SpringUtils;

import ca.openosp.openo.oscarEncounter.oscarMeasurements.data.MeasurementTypes;


public class EctDeleteMeasurementTypesAction extends Action {

    private MeasurementTypeDao measurementTypeDao = SpringUtils.getBean(MeasurementTypeDao.class);
    private MeasurementGroupDao measurementGroupDao = SpringUtils.getBean(MeasurementGroupDao.class);
    private MeasurementTypeDeletedDao measurementTypeDeletedDao = SpringUtils.getBean(MeasurementTypeDeletedDao.class);
    private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_admin", "w", null) || securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_admin.measurements", "w", null)) {

            EctDeleteMeasurementTypesForm frm = (EctDeleteMeasurementTypesForm) form;
            request.getSession().setAttribute("EctDeleteMeasurementTypesForm", frm);
            String[] deleteCheckbox = frm.getDeleteCheckbox();
            GregorianCalendar now = new GregorianCalendar();


            if (deleteCheckbox != null) {
                for (int i = 0; i < deleteCheckbox.length; i++) {
                    MiscUtils.getLogger().debug(deleteCheckbox[i]);

                    MeasurementType mt = measurementTypeDao.find(Integer.parseInt(deleteCheckbox[i]));
                    if (mt != null) {

                        for (MeasurementGroup g : measurementGroupDao.findByTypeDisplayName(mt.getTypeDisplayName())) {
                            measurementGroupDao.remove(g.getId());
                        }

                        MeasurementTypeDeleted mtd = new MeasurementTypeDeleted();
                        mtd.setType(mt.getType());
                        mtd.setTypeDisplayName(mt.getTypeDisplayName());
                        mtd.setTypeDescription(mt.getTypeDescription());
                        mtd.setMeasuringInstruction(mt.getMeasuringInstruction());
                        mtd.setValidation(mt.getValidation());
                        mtd.setDateDeleted(new Date());
                        measurementTypeDeletedDao.persist(mtd);

                        measurementTypeDao.remove(mt.getId());


                    }


                }
            }


            MeasurementTypes mt = MeasurementTypes.getInstance();
            mt.reInit();
            return mapping.findForward("success");

        } else {
            throw new SecurityException("Access Denied!"); //missing required security object (_admin)
        }
    }

}
