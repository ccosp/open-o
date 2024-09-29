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

import ca.openosp.openo.common.model.TicklerLink;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ws.rest.to.model.TicklerLinkTo1;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class TicklerLinkConverter extends AbstractConverter<TicklerLink, TicklerLinkTo1> {

    @Override
    public TicklerLink getAsDomainObject(LoggedInInfo loggedInInfo, TicklerLinkTo1 t) throws ConversionException {
        TicklerLink d = new TicklerLink();
        BeanUtils.copyProperties(t, d);
        return d;
    }

    @Override
    public TicklerLinkTo1 getAsTransferObject(LoggedInInfo loggedInInfo, TicklerLink d) throws ConversionException {
        TicklerLinkTo1 t = new TicklerLinkTo1();
        BeanUtils.copyProperties(d, t);
        return t;
    }

}
