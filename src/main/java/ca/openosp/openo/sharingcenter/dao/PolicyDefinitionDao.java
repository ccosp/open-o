//CHECKSTYLE:OFF
/**
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
 */
package ca.openosp.openo.sharingcenter.dao;

import java.util.List;

import ca.openosp.openo.common.dao.AbstractDaoImpl;

import javax.persistence.Query;

import ca.openosp.openo.sharingcenter.model.AffinityDomainDataObject;
import ca.openosp.openo.sharingcenter.model.PolicyDefinitionDataObject;
import org.springframework.stereotype.Repository;

@Repository
public class PolicyDefinitionDao extends AbstractDaoImpl<PolicyDefinitionDataObject> {

    public PolicyDefinitionDao() {
        super(PolicyDefinitionDataObject.class);
    }

    /**
     * Finds PolicyDefinition object for a specific id
     *
     * @param id PolicyDefinition Id
     * @return PolicyDefinition
     */
    public PolicyDefinitionDataObject getPolicyDefinition(int id) {
        String sql = "FROM PolicyDefinitionDataObject a where a.id = ?";
        Query query = entityManager.createQuery(sql);
        query.setParameter(0, id);

        PolicyDefinitionDataObject retVal = getSingleResultOrNull(query);
        return retVal;
    }

    /**
     * Finds PolicyDefinition object by code, code_system and domain
     *
     * @param code       Policy Code
     * @param codeSystem Policy Code System
     * @param domain     Affinity Domain
     * @return PolicyDefinition
     */
    public PolicyDefinitionDataObject getPolicyDefinitionByCode(String code, String codeSystem, AffinityDomainDataObject domain) {
        String sql = "FROM PolicyDefinitionDataObject a where a.code = ? and a.codeSystem = ? and a.affinityDomain = ?";
        Query query = entityManager.createQuery(sql);
        query.setParameter(0, code);
        query.setParameter(1, codeSystem);
        query.setParameter(2, domain);

        query.setMaxResults(1);
        PolicyDefinitionDataObject retVal = getSingleResultOrNull(query);
        return retVal;
    }

    /**
     * Finds PolicyDefinition object for a specific domain
     *
     * @param domain AffinityDomain
     * @return PolicyDefinition
     */
    public List<PolicyDefinitionDataObject> getPolicyDefinitionByDomain(AffinityDomainDataObject domain) {
        String sql = "FROM PolicyDefinitionDataObject a where a.affinityDomain = ?";
        Query query = entityManager.createQuery(sql);
        query.setParameter(0, domain);

        @SuppressWarnings("unchecked")
        List<PolicyDefinitionDataObject> retVal = query.getResultList();
        return retVal;
    }

    public List<PolicyDefinitionDataObject> getAllPolicyDefinitions() {
        String sql = "FROM PolicyDefinitionDataObject a";
        Query query = entityManager.createQuery(sql);

        @SuppressWarnings("unchecked")
        List<PolicyDefinitionDataObject> retVal = query.getResultList();
        return retVal;
    }

}
