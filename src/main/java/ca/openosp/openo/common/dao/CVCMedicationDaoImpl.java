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

import ca.openosp.openo.common.model.CVCMedication;
import org.springframework.stereotype.Repository;

@Repository
public class CVCMedicationDaoImpl extends AbstractDaoImpl<CVCMedication> implements CVCMedicationDao {

    public CVCMedicationDaoImpl() {
        super(CVCMedication.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CVCMedication> findByDIN(String din) {
        Query query = entityManager.createQuery("SELECT x FROM CVCMedication x WHERE x.din = :din");
        query.setParameter("din", din);
        List<CVCMedication> result = query.getResultList();
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CVCMedication findBySNOMED(String conceptId) {
        Query query = entityManager.createQuery("SELECT x FROM CVCMedication x WHERE x.snomedCode = :code");
        query.setParameter("code", conceptId);
        query.setMaxResults(1);
        List<CVCMedication> result = query.getResultList();
        if (!result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }

    @Override
    public void removeAll() {
        Query query = entityManager.createQuery("DELETE FROM CVCMedication");
        query.executeUpdate();
    }
}
