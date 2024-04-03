package org.oscarehr.common.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.oscarehr.common.model.UserProperty;

public interface UserPropertyDAO extends AbstractDao<UserProperty>{
    void delete(UserProperty prop);
    void saveProp(String provider, String userPropertyName, String value);
    void saveProp(UserProperty prop);
    void saveProp(String name, String val);
    String getStringValue(String provider, String propertyName);
    List<UserProperty> getAllProperties(String name, List<String> list);
    List<UserProperty> getPropValues(String name, String value);
    UserProperty getProp(String prov, String name);
    UserProperty getProp(String name);
    List<UserProperty> getDemographicProperties(String providerNo);
    Map<String, String> getProviderPropertiesAsMap(String providerNo);
    void saveProperties(String providerNo, Map<String, String> props);
}
