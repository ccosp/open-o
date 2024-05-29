/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * This software was written for
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 *
 * Modifications made by Magenta Health in 2024.
 */

package org.oscarehr.PMmodule.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.oscarehr.PMmodule.model.FunctionalUserType;
import org.oscarehr.PMmodule.model.ProgramFunctionalUser;
import org.oscarehr.util.MiscUtils;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.hibernate.SessionFactory;

public interface ProgramFunctionalUserDAO {

    public List<FunctionalUserType> getFunctionalUserTypes();

    public FunctionalUserType getFunctionalUserType(Long id);

    public void saveFunctionalUserType(FunctionalUserType fut);

    public void deleteFunctionalUserType(Long id);

    public List<FunctionalUserType> getFunctionalUsers(Long programId);

    public ProgramFunctionalUser getFunctionalUser(Long id);

    public void saveFunctionalUser(ProgramFunctionalUser pfu);

    public void deleteFunctionalUser(Long id);

    public Long getFunctionalUserByUserType(Long programId, Long userTypeId);
}
