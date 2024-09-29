//CHECKSTYLE:OFF
/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
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
 * <p>
 * Modifications made by Magenta Health in 2024.
 */
package ca.openosp.openo.common.dao;

import java.util.List;
import javax.persistence.Query;

import ca.openosp.openo.common.model.Security;
import ca.openosp.openo.common.model.SecurityArchive;
import org.springframework.stereotype.Repository;

@Repository
public class SecurityArchiveDaoImpl extends AbstractDaoImpl<SecurityArchive> implements SecurityArchiveDao {

    public SecurityArchiveDaoImpl() {
        super(SecurityArchive.class);
    }

    @Override
    public List<SecurityArchive> findBySecurityNo(Integer securityNo) {

        String sqlCommand = "select x from SecurityArchive x where x.securityNo=?1";

        Query query = entityManager.createQuery(sqlCommand);
        query.setParameter(1, securityNo);

        @SuppressWarnings("unchecked")
        List<SecurityArchive> results = query.getResultList();

        return results;
    }

    @Override
    public List<String> findPreviousPasswordsByProviderNo(String providerNo, int maxResult) {

        String sqlCommand = "select distinct x.password from SecurityArchive x where x.providerNo=?1 order by x.passwordUpdateDate DESC";

        Query query = entityManager.createQuery(sqlCommand);
        query.setParameter(1, providerNo);
        query.setMaxResults(maxResult);

        @SuppressWarnings("unchecked")
        List<String> results = query.getResultList();

        return results;
    }

    @Override
    public Integer archiveRecord(Security s) {
        SecurityArchive sa = new SecurityArchive(s);
        persist(sa);
        return sa.getId();
    }

}
