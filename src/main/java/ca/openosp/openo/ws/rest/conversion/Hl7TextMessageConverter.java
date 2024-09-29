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

import ca.openosp.openo.common.model.Hl7TextMessage;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ws.rest.to.model.Hl7TextMessageTo1;
import org.springframework.beans.BeanUtils;

public class Hl7TextMessageConverter extends AbstractConverter<Hl7TextMessage, Hl7TextMessageTo1> {

    @Override
    public Hl7TextMessage getAsDomainObject(LoggedInInfo loggedInInfo, Hl7TextMessageTo1 t) throws ConversionException {
        Hl7TextMessage d = new Hl7TextMessage();
        BeanUtils.copyProperties(t, d);
        return d;
    }

    @Override
    public Hl7TextMessageTo1 getAsTransferObject(LoggedInInfo loggedInInfo, Hl7TextMessage d) throws ConversionException {
        Hl7TextMessageTo1 t = new Hl7TextMessageTo1();
        BeanUtils.copyProperties(d, t);
        return t;
    }


}
