//CHECKSTYLE:OFF

package ca.openosp.openo.form;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

import ca.openosp.openo.Misc;
import org.oscarehr.util.LoggedInInfo;

import ca.openosp.openo.oscarDB.DBHandler;
import ca.openosp.openo.util.UtilDateUtilities;

public class FrmGrowthChartRecord extends FrmRecord {
    private String _dateFormat = "yyyy/MM/dd";

    public Properties getFormRecord(LoggedInInfo loggedInInfo, int demographicNo, int existingID)
            throws SQLException {
        Properties props = new Properties();

        if (existingID <= 0) {

            String sql = "SELECT demographic_no, last_name, first_name, sex, address, city, province, postal, phone, phone2, year_of_birth, month_of_birth, date_of_birth, hin FROM demographic WHERE demographic_no = " + demographicNo;
            ResultSet rs = DBHandler.GetSQL(sql);
            if (rs.next()) {
                java.util.Date date = UtilDateUtilities.calcDate(Misc.getString(rs, "year_of_birth"), Misc.getString(rs, "month_of_birth"), Misc.getString(rs, "date_of_birth"));
                props.setProperty("demographic_no", Misc.getString(rs, "demographic_no"));
                props.setProperty("formCreated", UtilDateUtilities.DateToString(new Date(), _dateFormat));
                props.setProperty("formEdited", UtilDateUtilities.DateToString(new Date(), _dateFormat));
                props.setProperty("patientName", Misc.getString(rs, "first_name") + " " + Misc.getString(rs, "last_name"));
                props.setProperty("patientSex", Misc.getString(rs, "sex"));
                props.setProperty("dateOfBirth", UtilDateUtilities.DateToString(date, _dateFormat));
                //props.setProperty("c_surname", ca.openosp.openo.Misc.getString(rs,"last_name"));
                //props.setProperty("c_address", ca.openosp.openo.Misc.getString(rs,"address"));
                //props.setProperty("c_city", ca.openosp.openo.Misc.getString(rs,"city"));
                //props.setProperty("c_province", ca.openosp.openo.Misc.getString(rs,"province"));
                //props.setProperty("c_postal", ca.openosp.openo.Misc.getString(rs,"postal"));
                //props.setProperty("c_phn", ca.openosp.openo.Misc.getString(rs,"hin"));
                //props.setProperty("c_phone", ca.openosp.openo.Misc.getString(rs,"phone") +"  "+ ca.openosp.openo.Misc.getString(rs,"phone2"));
            }
            rs.close();
        } else {
            String sql = "SELECT * FROM formGrowthChart WHERE demographic_no = " + demographicNo + " AND ID = " + existingID;
            FrmRecordHelp frh = new FrmRecordHelp();
            frh.setDateFormat(_dateFormat);
            props = (frh).getFormRecord(sql);

            sql = "SELECT sex, year_of_birth, month_of_birth, date_of_birth FROM demographic WHERE demographic_no = " + demographicNo;
            ResultSet rs = DBHandler.GetSQL(sql);
            if (rs.next()) {
                java.util.Date date = UtilDateUtilities.calcDate(Misc.getString(rs, "year_of_birth"), Misc.getString(rs, "month_of_birth"), Misc.getString(rs, "date_of_birth"));
                props.setProperty("patientSex", Misc.getString(rs, "sex"));
                props.setProperty("dateOfBirth", UtilDateUtilities.DateToString(date, _dateFormat));
            }
        }

        return props;
    }

    public int saveFormRecord(Properties props) throws SQLException {
        String demographic_no = props.getProperty("demographic_no");
        String sql = "SELECT * FROM formGrowthChart WHERE demographic_no=" + demographic_no + " AND ID=0";

        FrmRecordHelp frh = new FrmRecordHelp();
        frh.setDateFormat(_dateFormat);
        return ((frh).saveFormRecord(props, sql));
    }

    public Properties getPrintRecord(int demographicNo, int existingID) throws SQLException {
        String sql = "SELECT * FROM formGrowthChart WHERE demographic_no = " + demographicNo + " AND ID = " + existingID;
        FrmRecordHelp frh = new FrmRecordHelp();
        frh.setDateFormat(_dateFormat);
        return ((frh).getPrintRecord(sql));
    }

    public String findActionValue(String submit) throws SQLException {
        FrmRecordHelp frh = new FrmRecordHelp();
        frh.setDateFormat(_dateFormat);
        return ((frh).findActionValue(submit));
    }

    public String createActionURL(String where, String action, String demoId, String formId) throws SQLException {
        FrmRecordHelp frh = new FrmRecordHelp();
        frh.setDateFormat(_dateFormat);
        return ((frh).createActionURL(where, action, demoId, formId));
    }

}
