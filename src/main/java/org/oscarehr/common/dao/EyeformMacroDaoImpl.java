//CHECKSTYLE:OFF
/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
 * Copyright (c) 2008-2012 Indivica Inc.
 * <p>
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 * <p>
 * Modifications made by Magenta Health in 2024.
 */

package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;

import org.oscarehr.common.model.EyeformMacro;
import org.springframework.stereotype.Repository;

@Repository
public class EyeformMacroDaoImpl extends AbstractDaoImpl<EyeformMacro> implements EyeformMacroDao {

    public EyeformMacroDaoImpl() {
        super(EyeformMacro.class);
    }

    @SuppressWarnings("unchecked")
    public List<EyeformMacro> getMacros() {

        Query query = entityManager.createQuery("select x from " + modelClass.getName() + " x order by x.macroName");
        return query.getResultList();
    }

}
