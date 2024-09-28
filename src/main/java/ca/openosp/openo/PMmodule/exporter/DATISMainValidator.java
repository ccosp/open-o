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

package ca.openosp.openo.PMmodule.exporter;

import java.util.ArrayList;
import java.util.List;


public class DATISMainValidator implements IValidator {

    private List<String> errorsList = new ArrayList<String>();

    /*
     * @see exporter.PMmodule.IValidator#validate(exporter.PMmodule.DATISField, java.lang.String)
     */
//	@Override
    public String validate(DATISField field, String value) {
        if (DATISType.isNumeric(field.getType())) {
            if (value.charAt(field.getMaxSize() - 1) == '-') {
                value = value.substring(0, field.getMaxSize() - 1);
                errorsList.add(field.getName() + ": Removed trailing '-' from value");
            }
        }

        return value;
    }

    //	@Override
    public List<String> getErrorList() {
        return errorsList;
    }

}
