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

import java.util.List;

import javax.persistence.Query;

import ca.openosp.openo.common.dao.AbstractDaoImpl;
import ca.openosp.openo.hospitalReportManager.model.HRMSubClass;
import org.springframework.stereotype.Repository;

@Repository
public class HRMSubClassDao extends AbstractDaoImpl<HRMSubClass> {

    public HRMSubClassDao() {
        super(HRMSubClass.class);
    }

    public List<HRMSubClass> findById(int id) {
        String sql = "select x from " + this.modelClass.getName() + " x where x.id=?";
        Query query = entityManager.createQuery(sql);
        query.setParameter(0, id);
        @SuppressWarnings("unchecked")
        List<HRMSubClass> documents = query.getResultList();
        return documents;
    }

    public List<HRMSubClass> listAll() {
        String sql = "select x from " + this.modelClass.getName() + " x ";
        Query query = entityManager.createQuery(sql);

        @SuppressWarnings("unchecked")
        List<HRMSubClass> subclasses = query.getResultList();
        return subclasses;
    }

    public List<HRMSubClass> findBySendingFacilityId(String sendingFacilityId) {
        String sql = "select x from " + this.modelClass.getName() + " x  where x.sendingFacilityId = ?1";
        Query query = entityManager.createQuery(sql);
        query.setParameter(1, sendingFacilityId);

        @SuppressWarnings("unchecked")
        List<HRMSubClass> subclasses = query.getResultList();
        return subclasses;
    }

    public boolean subClassMappingExists(String className, String subClassName) {
        return subClassMappingExists(className, subClassName, "*");
    }

    public boolean subClassMappingExists(String className, String subClassName, String sendingFacilityId) {
        return subClassMappingExists(className, subClassName, null, sendingFacilityId);
    }

    public boolean subClassMappingExists(String className, String subClassName, String subClassMnemonic, String sendingFacilityId) {
        String sql = "select x from " + this.modelClass.getName() + " x where x.className=? and x.subClassName=?  and x.subClassMnemonic=? and x.sendingFacilityId=?";
        sendingFacilityId = (sendingFacilityId == null) ? "*" : sendingFacilityId;
        Query query = entityManager.createQuery(sql);
        query.setParameter(1, className);
        query.setParameter(2, subClassName);
        query.setParameter(3, subClassMnemonic);
        query.setParameter(4, sendingFacilityId);

        try {
            return (query.getSingleResult() != null);
        } catch (Exception e) {
            return false;
        }
    }

    public HRMSubClass findSubClassMapping(String className, String subClassName, String subClassMnemonic, String sendingFacilityId) {
        String sql = null;
        if (subClassMnemonic != null) {
            sql = "select x from " + this.modelClass.getName() + " x where x.className=? and x.subClassName=? and x.sendingFacilityId=? and x.subClassMnemonic=?";
        } else {
            sql = "select x from " + this.modelClass.getName() + " x where x.className=? and x.subClassName=? and x.sendingFacilityId=?";
        }
        Query query = entityManager.createQuery(sql);
        query.setParameter(1, className);
        query.setParameter(2, subClassName);
        query.setParameter(3, sendingFacilityId);

        if (subClassMnemonic != null) {
            query.setParameter(4, subClassMnemonic);
        }

        return getSingleResultOrNull(query);
    }

    public HRMSubClass findApplicableSubClassMapping(String className, String subClassName, String subClassMnemonic, String sendingFacilityId) {
        HRMSubClass mapping = null;
        try {
            mapping = findSubClassMapping(className, subClassName, subClassMnemonic, sendingFacilityId);
        } catch (Exception e) {
            // Didn't find one... try a wildcard search
            try {
                mapping = findSubClassMapping(className, subClassName, subClassMnemonic, "*");
            } catch (Exception e2) {
                // Didn't find one that way either
                mapping = null;
            }
        }
        return mapping;
    }


}
