//CHECKSTYLE:OFF
/**
 * Copyright (c) 2008-2012 Indivica Inc.
 * <p>
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */
package ca.openosp.openo.olis.dao;

import java.util.List;

import javax.persistence.Query;

import ca.openosp.openo.common.dao.AbstractDaoImpl;
import ca.openosp.openo.olis.model.OLISRequestNomenclature;
import org.springframework.stereotype.Repository;

@Repository
public class OLISRequestNomenclatureDao extends AbstractDaoImpl<OLISRequestNomenclature> {


    public OLISRequestNomenclatureDao() {
        super(OLISRequestNomenclature.class);
    }

    public OLISRequestNomenclature findByNameId(String id) {
        String sql = "select x from " + this.modelClass.getName() + " x where x.nameId=?";
        Query query = entityManager.createQuery(sql);
        query.setParameter(0, id);
        return this.getSingleResultOrNull(query);
    }

    @SuppressWarnings("unchecked")
    public List<OLISRequestNomenclature> findAll() {
        String sql = "select x from " + this.modelClass.getName() + " x";
        Query query = entityManager.createQuery(sql);
        return query.getResultList();
    }

}
