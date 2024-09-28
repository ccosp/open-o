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


package ca.openosp.openo.oscarEncounter.oscarMeasurements.bean;

import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import ca.openosp.openo.PMmodule.dao.ProviderDao;
import ca.openosp.openo.common.dao.MeasurementDao;
import ca.openosp.openo.common.dao.MeasurementGroupDao;
import ca.openosp.openo.common.dao.MeasurementTypeDao;
import ca.openosp.openo.common.dao.ValidationsDao;
import ca.openosp.openo.common.model.Measurement;
import ca.openosp.openo.common.model.MeasurementGroup;
import ca.openosp.openo.common.model.MeasurementType;
import ca.openosp.openo.common.model.Provider;
import ca.openosp.openo.common.model.Validations;
import ca.openosp.openo.ehrutil.SpringUtils;

import ca.openosp.openo.OscarProperties;
import ca.openosp.openo.util.ConversionUtils;

public class EctMeasurementTypesBeanHandler {

    Vector measurementTypeVector = new Vector();
    Vector measuringInstrcVector = new Vector();
    Vector measuringInstrcHdVector = new Vector();
    Vector measuringInstrcVectorVector = new Vector();
    Vector typeVector = new Vector();
    Vector measurementsDataVector = new Vector();

    public EctMeasurementTypesBeanHandler() {
        init();
    }

    public EctMeasurementTypesBeanHandler(String groupName, String demo) {
        init(groupName, demo);
    }

    public boolean init() {
        MeasurementTypeDao dao = SpringUtils.getBean(MeasurementTypeDao.class);
        ValidationsDao valDao = SpringUtils.getBean(ValidationsDao.class);

        boolean orderById = "true".equals(OscarProperties.getInstance().getProperty("oscarMeasurements.orderGroupById", "false"));

        List<MeasurementType> types = (orderById) ? dao.findAllOrderById() : dao.findAllOrderByName();

        for (MeasurementType mt : types) {
            String validation = mt.getValidation();
            if (validation == null || validation.isEmpty()) {
                continue;
            }

            Validations val = valDao.find(ConversionUtils.fromIntString(validation));
            if (val != null) {
                EctMeasurementTypesBean measurementTypes = new EctMeasurementTypesBean(
                        mt.getId(), mt.getType(), mt.getTypeDisplayName(),
                        mt.getTypeDescription(), mt.getMeasuringInstruction(),
                        val.getName());
                measurementTypeVector.add(measurementTypes);
            }
        }
        return true;
    }

    public boolean init(String groupName, String demo) {
        MeasurementGroupDao mgDao = SpringUtils.getBean(MeasurementGroupDao.class);
        MeasurementTypeDao mtDao = SpringUtils.getBean(MeasurementTypeDao.class);
        MeasurementDao msDao = SpringUtils.getBean(MeasurementDao.class);
        ProviderDao prDao = SpringUtils.getBean(ProviderDao.class);

        for (MeasurementGroup mg : mgDao.findByName(groupName)) {
            String typeDisplayName = mg.getTypeDisplayName();

            MeasurementType mt = null;
            for (MeasurementType m : mtDao.findByTypeDisplayName(typeDisplayName)) {
                EctMeasuringInstructionBean mInstrc = new EctMeasuringInstructionBean(m.getMeasuringInstruction());
                measuringInstrcVector.add(mInstrc);
                mt = m;
            }

            Measurement ms = null;
            if (mt != null) {
                ms = msDao.findLastEntered(ConversionUtils.fromIntString(demo), mt.getType());
            }

            boolean hasPreviousData = false;
            if (ms != null) {
                String providerNo = ms.getProviderNo();

                Provider provider = prDao.getProvider(providerNo);

                String pFname = "";
                String pLname = "";
                if (provider != null) {
                    pFname = provider.getFirstName();
                    pLname = provider.getLastName();

                } else if (providerNo.equals("0")) {
                    ResourceBundle props = ResourceBundle.getBundle("oscarResources");
                    pFname = props.getString("oscarLab.System");
                    pLname = props.getString("oscarLab.System");
                }

                EctMeasurementTypesBean measurementTypes = new EctMeasurementTypesBean(mt.getId(),
                        mt.getType(),
                        mt.getTypeDisplayName(),
                        mt.getTypeDescription(),
                        mt.getMeasuringInstruction(),
                        mt.getValidation(),
                        pFname,
                        pLname,
                        ms.getDataField(),
                        ms.getMeasuringInstruction(),
                        ms.getComments(),
                        ConversionUtils.toDateString(ms.getDateObserved()),
                        ConversionUtils.toDateString(ms.getCreateDate()));

                measurementTypeVector.add(measurementTypes);
                EctMeasuringInstructionBeanHandler hd = new EctMeasuringInstructionBeanHandler(measuringInstrcVector);
                measuringInstrcHdVector.add(hd);
                measuringInstrcVectorVector.add(measuringInstrcVector);
                measuringInstrcVector = new Vector();
                hasPreviousData = true;

            }

            if (!hasPreviousData && mt != null) {
                EctMeasurementTypesBean measurementTypes = new EctMeasurementTypesBean(mt.getId(),
                        mt.getType(),
                        mt.getTypeDisplayName(),
                        mt.getTypeDescription(),
                        mt.getMeasuringInstruction(),
                        mt.getValidation());

                measurementTypeVector.add(measurementTypes);
                EctMeasuringInstructionBeanHandler hd = new EctMeasuringInstructionBeanHandler(measuringInstrcVector);
                measuringInstrcHdVector.add(hd);
                measuringInstrcVectorVector.add(measuringInstrcVector);
                measuringInstrcVector = new Vector();
            }
        }

        return true;
    }

    public Vector getMeasurementTypeVector() {
        return measurementTypeVector;
    }

    public Vector getMeasuringInstrcVector() {
        return measuringInstrcVector;
    }

    public Vector getMeasuringInstrcHdVector() {
        return measuringInstrcHdVector;
    }

    public Vector getMeasuringInstrcVectorVector() {
        return measuringInstrcVectorVector;
    }

    public Vector getMeasurementsDataVector() {
        return measurementsDataVector;
    }
}
