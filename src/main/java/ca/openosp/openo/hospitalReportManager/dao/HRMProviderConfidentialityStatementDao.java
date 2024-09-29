//CHECKSTYLE:OFF
/**
 * Copyright (c) 2008-2012 Indivica Inc.
 * <p>
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */
package ca.openosp.openo.hospitalReportManager.dao;


import javax.persistence.Query;

import ca.openosp.openo.common.dao.AbstractDaoImpl;
import ca.openosp.openo.hospitalReportManager.model.HRMProviderConfidentialityStatement;
import org.springframework.stereotype.Repository;

@Repository
public class HRMProviderConfidentialityStatementDao extends AbstractDaoImpl<HRMProviderConfidentialityStatement> {

    public HRMProviderConfidentialityStatementDao() {
        super(HRMProviderConfidentialityStatement.class);
    }

    public String getConfidentialityStatementForProvider(String providerNo) {
        String sql = "select x.statement from " + this.modelClass.getName() + " x where x.providerNo=?";
        Query query = entityManager.createQuery(sql);
        query.setParameter(0, providerNo);
        try {
            return (String) query.getSingleResult();
        } catch (Exception e) {
            // No statement for this provider
            return "";
        }
    }

    public HRMProviderConfidentialityStatement findByProvider(String providerNo) {
        String sql = "select x from " + this.modelClass.getName() + " x where x.providerNo=?";
        Query query = entityManager.createQuery(sql);
        query.setParameter(0, providerNo);
        return this.getSingleResultOrNull(query);
    }


}
