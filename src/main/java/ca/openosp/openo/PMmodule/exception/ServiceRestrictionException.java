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

package ca.openosp.openo.PMmodule.exception;

import ca.openosp.openo.PMmodule.model.ProgramClientRestriction;

/**
 *
 */
public class ServiceRestrictionException extends Exception {

    private ProgramClientRestriction restriction;

    public ServiceRestrictionException(ProgramClientRestriction restriction) {
        this.restriction = restriction;
    }

    public ServiceRestrictionException(String s, ProgramClientRestriction restriction) {
        super(s);
        this.restriction = restriction;
    }

    public ServiceRestrictionException(String s, Throwable throwable, ProgramClientRestriction restriction) {
        super(s, throwable);
        this.restriction = restriction;
    }

    public ServiceRestrictionException(Throwable throwable, ProgramClientRestriction restriction) {
        super(throwable);
        this.restriction = restriction;
    }

    public ProgramClientRestriction getRestriction() {
        return restriction;
    }
}
