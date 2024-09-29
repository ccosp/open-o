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

import ca.openosp.openo.common.model.CtlBillingServicePremium;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class CtlBillingServicePremiumDaoImpl extends AbstractDaoImpl<CtlBillingServicePremium> implements CtlBillingServicePremiumDao {

    public CtlBillingServicePremiumDaoImpl() {
        super(CtlBillingServicePremium.class);
    }

    public List<CtlBillingServicePremium> findByServiceCode(String serviceCode) {
        Query q = entityManager.createQuery("select x from CtlBillingServicePremium x where x.serviceCode=?");
        q.setParameter(0, serviceCode);


        List<CtlBillingServicePremium> results = q.getResultList();

        return results;
    }

    public List<CtlBillingServicePremium> findByStatus(String status) {
        Query q = entityManager.createQuery("select x from CtlBillingServicePremium x where x.status=?");
        q.setParameter(0, status);


        List<CtlBillingServicePremium> results = q.getResultList();

        return results;
    }

    public List<Object[]> search_ctlpremium(String status) {
        Query q = entityManager.createQuery("select b.serviceCode, c.description from CtlBillingServicePremium b, BillingService c where b.serviceCode=c.serviceCode and b.status=?");
        q.setParameter(0, status);

        List<Object[]> results = q.getResultList();

        return results;
    }

    public List<CtlBillingServicePremium> findByServceCodes(List<String> serviceCodes) {
        Query query = createQuery("p", "p.serviceCode in (:serviceCodes)");
        query.setParameter("serviceCodes", serviceCodes);
        return query.getResultList();
    }
}
