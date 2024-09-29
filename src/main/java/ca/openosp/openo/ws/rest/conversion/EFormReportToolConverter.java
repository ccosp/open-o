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
package ca.openosp.openo.ws.rest.conversion;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import ca.openosp.openo.common.dao.EFormDao;
import ca.openosp.openo.common.model.EForm;
import ca.openosp.openo.common.model.EFormReportTool;
import ca.openosp.openo.managers.EFormReportToolManager;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.MiscUtils;
import ca.openosp.openo.ehrutil.SpringUtils;
import ca.openosp.openo.ws.rest.to.model.EFormReportToolTo1;
import org.springframework.beans.BeanUtils;

public class EFormReportToolConverter extends AbstractConverter<EFormReportTool, EFormReportToolTo1> {

    private boolean eFormName = false;
    private boolean numRecordsInTable = false;

    private EFormDao eformDao = SpringUtils.getBean(EFormDao.class);
    private EFormReportToolManager eformReportToolManager = SpringUtils.getBean(EFormReportToolManager.class);

    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    public EFormReportToolConverter() {

    }

    public EFormReportToolConverter(boolean eFormName, boolean numRecordsInTable) {
        this.eFormName = eFormName;
        this.numRecordsInTable = numRecordsInTable;
    }

    @Override
    public EFormReportTool getAsDomainObject(LoggedInInfo loggedInInfo, EFormReportToolTo1 t) throws ConversionException {
        EFormReportTool d = new EFormReportTool();
        BeanUtils.copyProperties(t, d);

        if (t.getExpiryDateString() != null && t.getExpiryDateString().length() > 0) {
            try {
                d.setExpiryDate(dateFormatter.parse(t.getExpiryDateString()));
            } catch (ParseException e) {
                MiscUtils.getLogger().error("Error", e);
            }
        }
        return d;
    }

    @Override
    public EFormReportToolTo1 getAsTransferObject(LoggedInInfo loggedInInfo, EFormReportTool d) throws ConversionException {
        EFormReportToolTo1 t = new EFormReportToolTo1();
        BeanUtils.copyProperties(d, t);

        if (eFormName) {
            EForm eform = eformDao.find(d.getEformId());
            if (eform != null) {
                t.setEformName(eform.getFormName());
            }
        }
        if (numRecordsInTable) {
            Integer i = eformReportToolManager.getNumRecords(loggedInInfo, d);
            if (i != null) {
                t.setNumRecordsInTable(i);
            }
        }

        return t;
    }


}
