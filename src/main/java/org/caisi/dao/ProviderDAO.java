//CHECKSTYLE:OFF
/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
 * <p>
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
 * <p>
 * Modifications made by Magenta Health in 2024.
 */

package org.caisi.dao;

import java.util.List;

import ca.openosp.openo.common.model.Provider;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

/**
 * This couldn't possibly work, it's not a spring managed bean according to the xml files.
 * But oh well, some one imports this class and tries to have it injected so I'll
 * leave the code here so it compiles. what ever...
 */
public interface ProviderDAO {

    public List<Provider> getProviders();

    public Provider getProvider(String provider_no);

    public Provider getProviderByName(String lastName, String firstName);

}
 