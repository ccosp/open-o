package org.oscarehr.common.dao;

import org.oscarehr.common.model.ServiceClient;
import java.util.List;

public interface ServiceClientDao extends AbstractDao<ServiceClient> {
    List<ServiceClient> findAll();
    ServiceClient findByName(String name);
    ServiceClient findByKey(String key);
    ServiceClient find(Integer id);
}
