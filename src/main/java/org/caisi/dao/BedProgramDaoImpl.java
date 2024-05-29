/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 *
 * Modifications made by Magenta Health in 2024.
 */

package org.caisi.dao;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
public class BedProgramDaoImpl extends HibernateDaoSupport implements BedProgramDao{
    private String bedType = "Geographical";


	@Autowired
    public void setSessionFactoryOverride(SessionFactory sessionFactory) {
        super.setSessionFactory(sessionFactory);
    }

    private List getProgramResultList(String q) {
        return getHibernateTemplate().find(q);
    }

    private List getProgramResultList(String q1, Object obj) {
        return getHibernateTemplate().find(q1, obj);
    }

    public List getAllBedProgram() {
        String qr = "FROM Program p where p.type = ?";
        List rs = getProgramResultList(qr, bedType);
        return rs;
    }

    public List getAllProgram() {
        String qr = "FROM Program p order by p.name";
        List rs = getProgramResultList(qr);
        return rs;
    }

    public List getAllProgramNameID() {
        String qr = "select p.name, p.id from Program p";
        List rs = getProgramResultList(qr);
        return rs;
    }

    public List getAllProgramName() {
        String qr = "select p.name from Program p";
        List rs = getProgramResultList(qr);
        return rs;
    }

    public List getProgramIdByName(String name) {
        String q = "SELECT p.id FROM Program p WHERE p.name = ?";
        List rs = getProgramResultList(q, name);
        return rs;
    }

    public String[] getProgramInfo(int programId) {
        String[] result = new String[3];
        Session session = currentSession();
        SQLQuery query = session.createSQLQuery("SELECT name,address,phone,fax from program where id=" + programId);
        query.addScalar("name", StandardBasicTypes.STRING);
        query.addScalar("address", StandardBasicTypes.STRING);
        query.addScalar("phone", StandardBasicTypes.STRING);
        query.addScalar("fax", StandardBasicTypes.STRING);
        Object[] o = (Object[])query.uniqueResult();
        if (o != null) {
            result[0] = new String(o[0] + "\n" + o[1]);
            result[1] = (String)o[2];
            result[2] = (String)o[3];
        }
        return result;
    }

}
