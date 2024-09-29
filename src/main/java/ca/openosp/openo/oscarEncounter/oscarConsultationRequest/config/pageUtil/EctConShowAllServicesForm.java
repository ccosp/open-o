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


package ca.openosp.openo.oscarEncounter.oscarConsultationRequest.config.pageUtil;

import org.apache.struts.action.ActionForm;
import ca.openosp.openo.ehrutil.MiscUtils;

public final class EctConShowAllServicesForm extends ActionForm {

    public String getServiceId() {
        MiscUtils.getLogger().debug("getter showall.Service");
        if (service == null)
            service = new String();
        return service;
    }

    public void setServiceId(String str) {
        MiscUtils.getLogger().debug("setter showall.Service");
        service = str;
    }

    public String getServiceDesc() {
        if (serviceDesc == null)
            serviceDesc = new String();
        return serviceDesc;
    }

    public void setServiceDesc(String str) {
        serviceDesc = str;
    }

    String service;
    String serviceDesc;
}
