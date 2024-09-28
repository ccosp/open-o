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


package ca.openosp.openo.eyeform.web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.itextpdf.text.*;
import org.apache.logging.log4j.Logger;
import ca.openosp.openo.PMmodule.dao.ProviderDao;
import ca.openosp.openo.casemgmt.service.CaseManagementPrintPdf;
import ca.openosp.openo.casemgmt.util.ExtPrint;
import ca.openosp.openo.eyeform.dao.EyeformSpecsHistoryDao;
import ca.openosp.openo.eyeform.model.EyeformSpecsHistory;
import ca.openosp.openo.ehrutil.MiscUtils;
import ca.openosp.openo.ehrutil.SpringUtils;


public class SpecsHistoryPrint implements ExtPrint {

    private static Logger logger = MiscUtils.getLogger();

    @Override
    public void printExt(CaseManagementPrintPdf engine, HttpServletRequest request) throws IOException, DocumentException {
        logger.debug("specs history print!!!!");
        String startDate = request.getParameter("pStartDate");
        String endDate = request.getParameter("pEndDate");
        String demographicNo = request.getParameter("demographicNo");

        logger.debug("startDate = " + startDate);
        logger.debug("endDate = " + endDate);
        logger.debug("demographicNo = " + demographicNo);

        ProviderDao providerDao = (ProviderDao) SpringUtils.getBean(ProviderDao.class);
        EyeformSpecsHistoryDao dao = (EyeformSpecsHistoryDao) SpringUtils.getBean(EyeformSpecsHistoryDao.class);


        List<EyeformSpecsHistory> specs = null;

        if (startDate.equals("") && endDate.equals("")) {
            specs = dao.getByDemographicNo(Integer.parseInt(demographicNo));
        } else {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
                Date dStartDate = formatter.parse(startDate);
                Date dEndDate = formatter.parse(endDate);
                specs = dao.getByDateRange(Integer.parseInt(demographicNo), dStartDate, dEndDate);
            } catch (Exception e) {
                logger.error("Error", e);
            }

        }

        if (engine.getNewPage())
            engine.getDocument().newPage();
        else
            engine.setNewPage(true);

        Font obsfont = new Font(engine.getBaseFont(), engine.FONTSIZE, Font.UNDERLINE);


        Paragraph p = new Paragraph();
        p.setAlignment(Paragraph.ALIGN_CENTER);
        Phrase phrase = new Phrase(engine.LEADING, "\n\n", engine.getFont());
        p.add(phrase);
        phrase = new Phrase(engine.LEADING, "Specs History", obsfont);
        p.add(phrase);
        engine.getDocument().add(p);

        for (EyeformSpecsHistory spec : specs) {
            p = new Paragraph();
            phrase = new Phrase(engine.LEADING, "", engine.getFont());
            Chunk chunk = new Chunk("Documentation Date: " + engine.getFormatter().format(spec.getDate()) + "\n", obsfont);
            phrase.add(chunk);
            p.add(phrase);
            p.add("Type:" + spec.getType() + "\n");
            p.add("Details:" + spec.toString().replaceAll("<br/>", "    ") + "\n");
            p.add("Doctor:" + providerDao.getProviderName(spec.getDoctor()) + "\n");

            engine.getDocument().add(p);
        }

    }


}
