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


package ca.openosp.openo.form;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;

import ca.openosp.openo.Misc;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.MiscUtils;

import ca.openosp.openo.oscarDB.DBHandler;
import ca.openosp.openo.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBeanHandler;
import ca.openosp.openo.util.UtilDateUtilities;

public class FrmRhImmuneGlobulinRecord extends FrmRecord {
    private String _dateFormat = "yyyy-MM-dd";

    public Properties getFormRecord(LoggedInInfo loggedInInfo, int demographicNo, int existingID)
            throws SQLException {
        Properties props = new Properties();


        ResultSet rs;
        String sql;

        if (existingID <= 0) {
            sql = "SELECT * FROM demographic WHERE demographic_no = "
                    + demographicNo;
            rs = DBHandler.GetSQL(sql);
            if (rs.next()) {
                java.util.Date dob = UtilDateUtilities.calcDate(Misc.getString(rs, "year_of_birth"), Misc.getString(rs, "month_of_birth"), Misc.getString(rs, "date_of_birth"));
                props.setProperty("demographic_no", Misc.getString(rs, "demographic_no"));
                props.setProperty("formCreated", UtilDateUtilities.DateToString(new Date(), _dateFormat));
                props.setProperty("dob", UtilDateUtilities.DateToString(dob, "yyyy-MM-dd"));
                props.setProperty("sex", Misc.getString(rs, "sex"));
                props.setProperty("phone", Misc.getString(rs, "phone"));

                String lastname = Misc.getString(rs, "last_name");
                MiscUtils.getLogger().debug("last name " + lastname);
                props.setProperty("motherSurname", lastname);
                props.setProperty("motherFirstname", Misc.getString(rs, "first_name"));
                props.setProperty("motherHIN", Misc.getString(rs, "hin"));
                props.setProperty("motherVC", Misc.getString(rs, "ver"));
                props.setProperty("motherCity", Misc.getString(rs, "city"));
                props.setProperty("motherProvince", Misc.getString(rs, "province"));
                props.setProperty("motherPostalCode", Misc.getString(rs, "postal"));


                props.setProperty("motherAddress", Misc.getString(rs, "address"));

                Hashtable measurementHash = EctMeasurementsDataBeanHandler.getLast("" + demographicNo, "BLDT");


                if (measurementHash != null && measurementHash.get("value") != null) {
                    props.setProperty("motherABO", (String) measurementHash.get("value"));
                }

                measurementHash = EctMeasurementsDataBeanHandler.getLast("" + demographicNo, "RHT");
                if (measurementHash != null && measurementHash.get("value") != null) {
                    props.setProperty("motherRHtype", (String) measurementHash.get("value"));
                }
            }
            rs.close();
        } else {
            sql =
                    "SELECT * FROM formRhImmuneGlobulin WHERE demographic_no = "
                            + demographicNo
                            + " AND ID = "
                            + existingID;
            rs = DBHandler.GetSQL(sql);

            if (rs.next()) {
                MiscUtils.getLogger().debug("getting metaData");
                ResultSetMetaData md = rs.getMetaData();

                for (int i = 1; i <= md.getColumnCount(); i++) {
                    String name = md.getColumnName(i);

                    String value;
                    MiscUtils.getLogger().debug(" name = " + name + " type = " + md.getColumnTypeName(i) + " scale = " + md.getScale(i));
                    if (md.getColumnTypeName(i).equalsIgnoreCase("TINY")) {

                        if (rs.getInt(i) == 1) {
                            value = "checked='checked'";
                            MiscUtils.getLogger().debug("checking " + name);
                        } else {
                            value = "";
                            MiscUtils.getLogger().debug("not checking " + name);
                        }
                    } else {
                        if (md.getColumnTypeName(i).equalsIgnoreCase("date")) {
                            value = UtilDateUtilities.DateToString(rs.getDate(i), _dateFormat);
                        } else {
                            value = Misc.getString(rs, i);
                        }
                    }

                    if (value != null) {
                        props.setProperty(name, value);
                    }
                }
            }
            rs.close();
        }
        return props;
    }

    public int saveFormRecord(Properties props) throws SQLException {
        String demographic_no = props.getProperty("demographic_no");
        String sql =
                "SELECT * FROM formRhImmuneGlobulin WHERE demographic_no="
                        + demographic_no
                        + " AND ID=0";

        FrmRecordHelp frh = new FrmRecordHelp();
        frh.setDateFormat(_dateFormat);
        return ((frh).saveFormRecord(props, sql));
    }

    public Properties getPrintRecord(int demographicNo, int existingID)
            throws SQLException {
        String sql =
                "SELECT * FROM formRhImmuneGlobulin WHERE demographic_no = "
                        + demographicNo
                        + " AND ID = "
                        + existingID;
        FrmRecordHelp frh = new FrmRecordHelp();
        frh.setDateFormat(_dateFormat);
        return ((frh).getPrintRecord(sql));
    }

    public String findActionValue(String submit) throws SQLException {
        FrmRecordHelp frh = new FrmRecordHelp();
        frh.setDateFormat(_dateFormat);
        return ((frh).findActionValue(submit));
    }

    public String createActionURL(
            String where,
            String action,
            String demoId,
            String formId)
            throws SQLException {
        FrmRecordHelp frh = new FrmRecordHelp();
        frh.setDateFormat(_dateFormat);
        return ((frh).createActionURL(where, action, demoId, formId));
    }

}
