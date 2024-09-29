//CHECKSTYLE:OFF
/**
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * This software was written for
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */


package ca.openosp.openo.oscarReport.reportByTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import ca.openosp.openo.ehrutil.MiscUtils;

import ca.openosp.openo.oscarDB.DBHandler;
import ca.openosp.openo.oscarReport.data.RptResultStruct;
import ca.openosp.openo.util.UtilMisc;

import com.Ostermiller.util.CSVPrinter;


/**
 * @author rjonasz
 */
public class SQLReporter implements Reporter {

    /**
     * Creates a new instance of SQLReporter
     */
    public SQLReporter() {
    }

    public boolean generateReport(HttpServletRequest request) {
        String templateId = request.getParameter("templateId");
        ReportObject curReport = (new ReportManager()).getReportTemplateNoParam(templateId);
        Map parameterMap = request.getParameterMap();

        if (curReport.isSequence()) {
            return generateSequencedReport(request);
        }

        String sql = curReport.getPreparedSQL(parameterMap);
        if (sql == "" || sql == null) {
            request.setAttribute("errormsg", "Error: Cannot find all parameters for the query.  Check the template.");
            request.setAttribute("templateid", templateId);
            return false;
        }
        ResultSet rs = null;
        String rsHtml = "An SQL query error has occured ";
        String csv = "";
        try {
            rs = DBHandler.GetSQL(sql);
            rsHtml = RptResultStruct.getStructure2(rs);  //makes html from the result set
            StringWriter swr = new StringWriter();
            CSVPrinter csvp = new CSVPrinter(swr);
            csvp.writeln(UtilMisc.getArrayFromResultSet(rs));
            csv = swr.toString();
        } catch (SQLException sqe) {
            rsHtml += sqe.getCause() != null ? sqe.getCause() : sqe.getMessage();
            MiscUtils.getLogger().error("Error", sqe);
        } catch (IOException e) {
            rsHtml = "Error: during creation of CSV : " + (e.getCause() != null ? e.getCause() : e.getMessage());
            MiscUtils.getLogger().error("Error", e);
        }

        request.getSession().setAttribute("csv", csv);
        request.setAttribute("csv", csv);
        request.setAttribute("sql", sql);
        request.setAttribute("reportobject", curReport);
        request.setAttribute("resultsethtml", rsHtml);

        return true;
    }

    public boolean generateSequencedReport(HttpServletRequest request) {
        String templateId = request.getParameter("templateId");
        ReportObject curReport = (new ReportManager()).getReportTemplateNoParam(templateId);
        Map parameterMap = request.getParameterMap();

        int x = 0;
        String sql = null;
        while ((sql = curReport.getPreparedSQL(x, parameterMap)) != null) {
            if (sql == "") {
                request.setAttribute("errormsg", "Error: Cannot find all parameters for the query.  Check the template.");
                request.setAttribute("templateid", templateId);
                return false;
            }

            ResultSet rs = null;
            String rsHtml = "An SQL query error has occured ";
            String csv = "";
            try {

                rs = DBHandler.GetSQL(sql);
                rsHtml = RptResultStruct.getStructure2(rs);  //makes html from the result set
                StringWriter swr = new StringWriter();
                CSVPrinter csvp = new CSVPrinter(swr);
                csvp.writeln(UtilMisc.getArrayFromResultSet(rs));
                csv = swr.toString();

            } catch (SQLException sqe) {
                rsHtml += sqe.getCause() != null ? sqe.getCause() : sqe.getMessage();
                MiscUtils.getLogger().error("Error", sqe);
            } catch (IOException e) {
                rsHtml = "Error: during creation of CSV : " + (e.getCause() != null ? e.getCause() : e.getMessage());
                MiscUtils.getLogger().error("Error", e);
            }

            request.setAttribute("csv-" + x, csv);
            request.setAttribute("sql-" + x, sql);
            request.setAttribute("resultsethtml-" + x, rsHtml);
            x++;
        }

        request.setAttribute("sequenceLength", x);
        request.setAttribute("reportobject", curReport);

        return true;
    }

}
