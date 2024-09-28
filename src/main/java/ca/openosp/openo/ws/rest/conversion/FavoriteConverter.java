//CHECKSTYLE:OFF
/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
 * Copyright (c) 2013-2015. Department of Computer Science, University of Victoria. All Rights Reserved.
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
 * Department of Computer Science
 * LeadLab
 * University of Victoria
 * Victoria, Canada
 * <p>
 * Modifications made by Magenta Health in 2024.
 */

package ca.openosp.openo.ws.rest.conversion;

import ca.openosp.openo.common.model.Favorite;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ws.rest.to.model.FavoriteTo1;

import java.util.List;

/**
 * Converts between domain Favorite and FavoriteTo1 objects.
 * <p>
 * This class represents the transformation between a the SQL schema
 * and the data model that is presented to a client.
 */

public interface FavoriteConverter {

    public Favorite getAsDomainObject(LoggedInInfo loggedInInfo, FavoriteTo1 t) throws ConversionException;

    public FavoriteTo1 getAsTransferObject(LoggedInInfo loggedInInfo, Favorite f) throws ConversionException;

    public List<FavoriteTo1> getAllAsTransferObjects(LoggedInInfo loggedInInfo, List<Favorite> ds) throws ConversionException;
}
