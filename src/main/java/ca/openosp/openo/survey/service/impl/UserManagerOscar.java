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

package ca.openosp.openo.survey.service.impl;

import javax.servlet.http.HttpServletRequest;

import ca.openosp.openo.survey.service.UserManager;
import org.springframework.stereotype.Component;

@Component(value = "surveyUserManager")
public class UserManagerOscar implements UserManager {

    public long getUserId(HttpServletRequest request) {
        String value = (String) request.getSession().getAttribute("user");
        return Long.parseLong(value);
    }

    public String getUsername(HttpServletRequest request) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isLoggedIn(HttpServletRequest request) {
        if (request.getSession().getAttribute("user") == null) {
            return false;
        }
        return true;
    }

    public boolean isAdmin(HttpServletRequest request) {
        if (isLoggedIn(request) && ((String) request.getSession().getAttribute("userrole")).indexOf("admin") != -1) {
            return true;
        }
        return false;
    }

}
