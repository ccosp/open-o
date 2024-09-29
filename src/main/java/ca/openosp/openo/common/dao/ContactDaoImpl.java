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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang.StringEscapeUtils;
import ca.openosp.openo.common.model.Contact;
import ca.openosp.openo.ehrutil.MiscUtils;
import org.springframework.stereotype.Repository;

@Repository
public class ContactDaoImpl extends AbstractDaoImpl<Contact> implements ContactDao {

    public ContactDaoImpl() {
        super(Contact.class);
    }

    @Override
    public List<Contact> search(String searchMode, String orderBy, String keyword) {
        StringBuilder where = new StringBuilder();
        List<String> paramList = new ArrayList<String>();

        if (searchMode.equals("search_name")) {
            String[] temp = keyword.split("\\,\\p{Space}*");
            if (temp.length > 1) {
                where.append("c.lastName like ?1 and c.firstName like ?2");
                paramList.add(temp[0] + "%");
                paramList.add(temp[1] + "%");
            } else {
                where.append("c.lastName like ?1");
                paramList.add(temp[0] + "%");
            }
        } else {
            where.append("c." + StringEscapeUtils.escapeSql(searchMode) + " like ?1");
            paramList.add(keyword + "%");
        }
        String sql = "SELECT c from Contact c where " + where.toString() + " order by " + orderBy;
        MiscUtils.getLogger().debug(sql);
        Query query = entityManager.createQuery(sql);
        for (int x = 0; x < paramList.size(); x++) {
            query.setParameter(x + 1, paramList.get(x));
        }

        @SuppressWarnings("unchecked")
        List<Contact> contacts = query.getResultList();
        return contacts;
    }

}
