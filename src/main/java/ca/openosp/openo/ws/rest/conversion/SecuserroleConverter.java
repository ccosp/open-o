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

import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ws.rest.to.model.UserRoleTo1;
import org.springframework.stereotype.Component;

import com.quatro.model.security.Secuserrole;

@Component
public class SecuserroleConverter extends AbstractConverter<Secuserrole, UserRoleTo1> {

    @Override
    public Secuserrole getAsDomainObject(LoggedInInfo loggedInInfo, UserRoleTo1 t) throws ConversionException {
        return null;
    }

    @Override
    public UserRoleTo1 getAsTransferObject(LoggedInInfo loggedInInfo, Secuserrole d) throws ConversionException {
        UserRoleTo1 t = new UserRoleTo1();
        t.setRoleName(d.getRoleName());
        return t;
    }
}
