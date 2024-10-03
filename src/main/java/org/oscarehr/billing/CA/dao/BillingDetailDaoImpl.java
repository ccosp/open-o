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

package org.oscarehr.billing.CA.dao;

import java.util.List;

import javax.persistence.Query;

import org.oscarehr.billing.CA.model.BillingDetail;
import org.oscarehr.common.dao.AbstractDaoImpl;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class BillingDetailDaoImpl extends AbstractDaoImpl<BillingDetail> implements BillingDetailDao {

    public BillingDetailDaoImpl() {
        super(BillingDetail.class);
    }

    @Override
    public List<BillingDetail> findByBillingNo(int billingNo) {
        Query q = entityManager.createQuery("select x from BillingDetail x where x.billingNo=?1");
        q.setParameter(1, billingNo);
        List<BillingDetail> results = q.getResultList();
        return results;
    }

    @Override
    public List<BillingDetail> findByBillingNoAndStatus(Integer billingNo, String status) {
        Query query = createQuery("bd", "bd.billingNo = :billingNo AND bd.status = :status");
        query.setParameter("billingNo", billingNo);
        query.setParameter("status", status);
        return query.getResultList();
    }

    @Override
    public List<BillingDetail> findByBillingNo(Integer billingNo) {
        Query query = createQuery("bd", "bd.billingNo = :billingNo AND bd.status <> 'D' ORDER BY service_code");
        query.setParameter("billingNo", billingNo);
        return query.getResultList();
    }
}
