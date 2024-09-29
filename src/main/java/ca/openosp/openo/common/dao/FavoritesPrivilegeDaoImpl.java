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

import ca.openosp.openo.common.model.FavoritesPrivilege;
import org.springframework.stereotype.Repository;

@Repository
public class FavoritesPrivilegeDaoImpl extends AbstractDaoImpl<FavoritesPrivilege> implements FavoritesPrivilegeDao {

    public FavoritesPrivilegeDaoImpl() {
        super(FavoritesPrivilege.class);
    }

    public List<String> getProviders() {
        Query query = entityManager.createQuery("select x.providerNo from FavoritesPrivilege x where x.openToPublic=?");
        query.setParameter(0, true);

        @SuppressWarnings("unchecked")
        List<String> results = query.getResultList();

        return results;
    }

    public FavoritesPrivilege findByProviderNo(String providerNo) {
        Query query = entityManager.createQuery("select x from FavoritesPrivilege x where x.providerNo=?");
        query.setParameter(0, providerNo);

        FavoritesPrivilege result = this.getSingleResultOrNull(query);

        return result;
    }

    public void setFavoritesPrivilege(String providerNo, boolean openpublic, boolean writeable) {

        FavoritesPrivilege fp = findByProviderNo(providerNo);
        if (fp != null) {
            fp.setOpenToPublic(openpublic);
            fp.setWriteable(writeable);
        } else {
            fp = new FavoritesPrivilege();
            fp.setProviderNo(providerNo);
            fp.setOpenToPublic(openpublic);
            fp.setWriteable(writeable);
            persist(fp);
        }
    }
}
