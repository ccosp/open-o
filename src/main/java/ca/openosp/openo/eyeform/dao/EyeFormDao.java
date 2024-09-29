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


package ca.openosp.openo.eyeform.dao;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import ca.openosp.openo.common.dao.AbstractDaoImpl;
import ca.openosp.openo.eyeform.model.EyeForm;
import ca.openosp.openo.ehrutil.MiscUtils;
import org.springframework.stereotype.Repository;

@Repository
public class EyeFormDao extends AbstractDaoImpl<EyeForm> {

    public EyeFormDao() {
        super(EyeForm.class);
    }

    public void save(EyeForm obj) {
        if (obj.getId() != null && obj.getId().intValue() > 0) {
            entityManager.merge(obj);
        } else {
            entityManager.persist(obj);
        }
    }

    public EyeForm getByAppointmentNo(int appointmentNo) {
        Query query = entityManager.createQuery("select x from " + modelClass.getSimpleName() + " x where x.appointmentNo=?1");
        query.setParameter(1, appointmentNo);

        EyeForm eyeform = null;

        try {
            eyeform = (EyeForm) query.getSingleResult();
        } catch (NoResultException e) {
            MiscUtils.getLogger().warn("warning", e);
        }

        return eyeform;

    }
}
