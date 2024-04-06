package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.Site;

public interface SiteDao extends AbstractDao<Site> {
    void save(Site s);
    List<Site> getAllSites();
    List<Site> getAllActiveSites();
    List<Site> getActiveSitesByProviderNo(String provider_no);
    Site getById(Integer id);
    Site getByLocation(String location);
    List<String> getGroupBySiteLocation(String location);
    List<String> getProviderNoBySiteLocation(String location);
    List<String> getProviderNoBySiteManagerProviderNo(String providerNo);
    List<String> getGroupBySiteManagerProviderNo(String providerNo);
    Long site_searchmygroupcount(String myGroupNo, String siteName);
    String getSiteNameByAppointmentNo(String appointmentNo);
    List<String> getGroupsBySiteProviderNo(String groupNo);
    List<String> getGroupsForAllSites();
    Site findByName(String name);
}
