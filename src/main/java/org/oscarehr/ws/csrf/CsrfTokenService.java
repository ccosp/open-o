/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */

package org.oscarehr.ws.csrf;

import net.sf.json.JSONObject;
import org.oscarehr.ws.oauth.AbstractServiceImpl;
import org.owasp.csrfguard.CsrfGuard;
import org.springframework.stereotype.Component;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/csrf")
@Component("csrfTokenService")
public class CsrfTokenService extends AbstractServiceImpl {
	
	@POST
	@Path("/getToken")
	@Produces("application/json")
	public String getToken() {
		CsrfGuard csrfGuard = CsrfGuard.getInstance();
		JSONObject token = new JSONObject();
		token.put("name", csrfGuard.getTokenName());
		token.put("value", csrfGuard.getTokenValue(getHttpServletRequest()));
		return token.toString();
	}
}
