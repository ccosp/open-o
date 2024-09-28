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

package ca.openosp.openo.oscarReport.oscarMeasurements.pageUtil;

import java.util.Vector;

import org.oscarehr.common.dao.MeasurementTypeDao;
import org.oscarehr.common.model.MeasurementType;
import org.oscarehr.util.SpringUtils;

public class RptMeasuringInstructionBeanHandler {

    Vector<RptMeasuringInstructionBean> measuringInstrcVector = new Vector<RptMeasuringInstructionBean>();

    public RptMeasuringInstructionBeanHandler(String measurementType) {
        init(measurementType);
    }

    public boolean init(String measurementType) {
        MeasurementTypeDao dao = SpringUtils.getBean(MeasurementTypeDao.class);
        for (MeasurementType mt : dao.findByTypeDisplayName(measurementType)) {
            RptMeasuringInstructionBean measuringInstrc = new RptMeasuringInstructionBean(mt.getMeasuringInstruction());
            measuringInstrcVector.add(measuringInstrc);
        }
        return true;
    }

    public Vector<RptMeasuringInstructionBean> getMeasuringInstrcVector() {
        return measuringInstrcVector;
    }
}
