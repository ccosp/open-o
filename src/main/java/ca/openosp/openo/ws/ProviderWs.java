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

package ca.openosp.openo.ws;

import java.util.List;

import javax.jws.WebService;

import org.apache.cxf.annotations.GZIP;
import ca.openosp.openo.common.model.Property;
import ca.openosp.openo.common.model.Provider;
import ca.openosp.openo.managers.ProviderManager2;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ws.transfer_objects.ProviderPropertyTransfer;
import ca.openosp.openo.ws.transfer_objects.ProviderTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@WebService
@Component
@GZIP(threshold = AbstractWs.GZIP_THRESHOLD)
public class ProviderWs extends AbstractWs {
    @Autowired
    private ProviderManager2 providerManager;

    public ProviderTransfer getLoggedInProviderTransfer() {
        LoggedInInfo loggedInInfo = getLoggedInInfo();
        return (ProviderTransfer.toTransfer(loggedInInfo.getLoggedInProvider()));
    }

    /**
     * @deprecated 2013-03-27 parameter should have been an object to allow nulls
     */
    public ProviderTransfer[] getProviders(boolean active) {
        return (getProviders2(active));
    }

    public ProviderTransfer[] getProviders2(Boolean active) {
        List<Provider> tempResults = providerManager.getProviders(getLoggedInInfo(), active);
        ProviderTransfer[] results = ProviderTransfer.toTransfers(tempResults);
        return (results);
    }

    public ProviderPropertyTransfer[] getProviderProperties(String providerNo, String propertyName) {
        List<Property> tempResults = providerManager.getProviderProperties(getLoggedInInfo(), providerNo, propertyName);
        ProviderPropertyTransfer[] results = ProviderPropertyTransfer.toTransfers(tempResults);
        return (results);
    }
}
