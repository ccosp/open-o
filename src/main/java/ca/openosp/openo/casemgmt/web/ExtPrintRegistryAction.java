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


package ca.openosp.openo.casemgmt.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import ca.openosp.openo.casemgmt.util.ExtPrintRegistry;
import ca.openosp.openo.ehrutil.MiscUtils;

public class ExtPrintRegistryAction extends DispatchAction {

    private static Logger logger = MiscUtils.getLogger();

    public ActionForward register(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        String bean = request.getParameter("bean");

        ExtPrintRegistry.addEntry(name, bean);

        logger.info("ext print registry added " + name + ":" + bean);
        return null;
    }

}
