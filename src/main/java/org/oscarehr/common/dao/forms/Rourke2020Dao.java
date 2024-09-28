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


package org.oscarehr.common.dao.forms;

import org.oscarehr.common.dao.AbstractDaoImpl;
import org.oscarehr.util.SpringUtils;
import org.springframework.stereotype.Repository;
import ca.openosp.openo.form.model.FormBooleanValue;
import ca.openosp.openo.form.model.FormRourke2020;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class Rourke2020Dao extends AbstractDaoImpl<FormRourke2020> {

    public Rourke2020Dao() {
        super(FormRourke2020.class);
    }

    @SuppressWarnings("unchecked")
    public List<FormRourke2020> findAllDistinctForms(Integer demographicNo) {
        String sql = "select frm from FormRourke2020 frm where frm.demographicNo = :demo and frm.id "
                + "= (select max(frm2.id) from FormRourke2020 frm2 where frm2.formCreated "
                + "= frm.formCreated and frm2.demographicNo = frm.demographicNo)";
        Query query = entityManager.createQuery(sql);
        query = query.setParameter("demo", demographicNo);
        return query.getResultList();
    }

    @Override
    public FormRourke2020 find(Object id) {
        // get form
        FormRourke2020 form = super.find(id);

        // set boolean fields from FormBooleanValues
        FormBooleanValueDao formBooleanValueDao = SpringUtils.getBean(FormBooleanValueDao.class);
        HashMap<String, FormBooleanValue> booleanValueMap = formBooleanValueDao.findAllForForm(form);
        form.setBooleanValueMap(booleanValueMap);

        return form;
    }

    @Override
    public FormRourke2020 saveEntity(FormRourke2020 form) {
        // save FormRourke2020 object
        form = super.saveEntity(form);

        // get boolean fields and persist them:
        for (FormBooleanValue value : form.getBooleanValueMap().values()) {
            value.getId().setFormId(form.getId());
            value.getId().setFormName(FormRourke2020.FORM_TABLE);
        }
        FormBooleanValueDao formBooleanValueDao = SpringUtils.getBean(FormBooleanValueDao.class);
        formBooleanValueDao.batchPersist(new ArrayList(form.getBooleanValueMap().values()));
        return form;
    }
}
