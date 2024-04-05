package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.Contact;

public interface ContactDao extends AbstractDao<Contact> {
    public List<Contact> search(String searchMode, String orderBy, String keyword);
}
