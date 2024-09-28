//CHECKSTYLE:OFF
/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
 * <p>
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * This software was written for
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 * <p>
 * Modifications made by Magenta Health in 2024.
 */

package org.oscarehr.PMmodule.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.oscarehr.common.NativeSql;
import org.oscarehr.common.dao.ProviderFacilityDao;
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.ProviderFacility;
import org.oscarehr.common.model.ProviderFacilityPK;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.hibernate.type.StandardBasicTypes;
import ca.openosp.openo.OscarProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import com.quatro.model.security.SecProvider;

@SuppressWarnings("unchecked")
@Transactional
public class ProviderDaoImpl extends HibernateDaoSupport implements ProviderDao {


    private static Logger log = MiscUtils.getLogger();

    @Autowired
    public void setSessionFactoryOverride(SessionFactory sessionFactory) {
        log.info("Setting session factory in ProviderDaoImpl");
        if (sessionFactory == null) {
            log.error("SessionFactory is null!");
        } else {
            log.info("SessionFactory is successfully set.");
        }
        super.setSessionFactory(sessionFactory);
    }

    public boolean providerExists(String providerNo) {
        return getHibernateTemplate().get(Provider.class, providerNo) != null;
    }

    @Override
    public Provider getProvider(String providerNo) {
        if (providerNo == null || providerNo.length() <= 0) {
            return null;
        }

        Provider provider = getHibernateTemplate().get(Provider.class, providerNo);

        if (log.isDebugEnabled()) {
            log.debug("getProvider: providerNo=" + providerNo + ",found=" + (provider != null));
        }

        return provider;
    }

    @Override
    public String getProviderName(String providerNo) {

        String providerName = "";
        Provider provider = getProvider(providerNo);

        if (provider != null) {
            if (provider.getFirstName() != null) {
                providerName = provider.getFirstName() + " ";
            }

            if (provider.getLastName() != null) {
                providerName += provider.getLastName();
            }

            if (log.isDebugEnabled()) {
                log.debug("getProviderName: providerNo=" + providerNo + ",result=" + providerName);
            }
        }

        return providerName;
    }

    @Override
    public String getProviderNameLastFirst(String providerNo) {
        if (providerNo == null || providerNo.length() <= 0) {
            throw new IllegalArgumentException();
        }

        String providerName = "";
        Provider provider = getProvider(providerNo);

        if (provider != null) {
            if (provider.getLastName() != null) {
                providerName = provider.getLastName() + ", ";
            }

            if (provider.getFirstName() != null) {
                providerName += provider.getFirstName();
            }

            if (log.isDebugEnabled()) {
                log.debug("getProviderNameLastFirst: providerNo=" + providerNo + ",result=" + providerName);
            }
        }

        return providerName;
    }

    @Override
    public List<Provider> getProviders() {

        List<Provider> rs = (List<Provider>) getHibernateTemplate().find(
                "FROM  Provider p ORDER BY p.LastName");

        if (log.isDebugEnabled()) {
            log.debug("getProviders: # of results=" + rs.size());
        }
        return rs;
    }

    @Override
    public List<Provider> getProviders(String[] providers) {
        List<Provider> rs = (List<Provider>) getHibernateTemplate().find(
                "FROM Provider p WHERE p.providerNumber IN (?)", (Object[]) providers);
        return rs;
    }

    @Override
    public List<Provider> getProviderFromFirstLastName(String firstname, String lastname) {
        firstname = firstname.trim();
        lastname = lastname.trim();
        String s = "From Provider p where p.FirstName=? and p.LastName=?";
        ArrayList<Object> paramList = new ArrayList<Object>();
        paramList.add(firstname);
        paramList.add(lastname);
        Object params[] = paramList.toArray(new Object[paramList.size()]);
        return (List<Provider>) getHibernateTemplate().find(s, params);
    }

    @Override
    public List<Provider> getProviderLikeFirstLastName(String firstname, String lastname) {
        firstname = firstname.trim();
        lastname = lastname.trim();
        String s = "From Provider p where p.FirstName like ? and p.LastName like ?";
        ArrayList<Object> paramList = new ArrayList<Object>();
        paramList.add(firstname);
        paramList.add(lastname);
        Object params[] = paramList.toArray(new Object[paramList.size()]);
        return (List<Provider>) getHibernateTemplate().find(s, params);
    }

    @Override
    public List<Provider> getActiveProviderLikeFirstLastName(String firstname, String lastname) {
        firstname = firstname.trim();
        lastname = lastname.trim();
        String s = "From Provider p where p.FirstName like ? and p.LastName like ? and p.Status='1'";
        ArrayList<Object> paramList = new ArrayList<Object>();
        paramList.add(firstname);
        paramList.add(lastname);
        Object params[] = paramList.toArray(new Object[paramList.size()]);
        return (List<Provider>) getHibernateTemplate().find(s, params);
    }

    @Override
    public List<SecProvider> getActiveProviders(Integer programId) {
        ArrayList<Object> paramList = new ArrayList<Object>();

        String sSQL = "FROM  SecProvider p where p.status='1' and p.providerNo in " +
                "(select sr.providerNo from secUserRole sr, LstOrgcd o " +
                " where o.code = 'P' || ? " +
                " and o.codecsv  like '%' || sr.orgcd || ',%' " +
                " and not (sr.orgcd like 'R%' or sr.orgcd like 'O%'))" +
                " ORDER BY p.lastName";

        paramList.add(programId);
        Object params[] = paramList.toArray(new Object[paramList.size()]);

        return (List<SecProvider>) getHibernateTemplate().find(sSQL, params);
    }

    @Override
    public List<Provider> getActiveProviders(String facilityId, String programId) {
        ArrayList<Object> paramList = new ArrayList<Object>();

        String sSQL;
        List<Provider> rs;
        if (programId != null && "0".equals(programId) == false) {
            sSQL = "FROM  Provider p where p.Status='1' and p.ProviderNo in "
                    + "(select c.ProviderNo from ProgramProvider c where c.ProgramId =?) ORDER BY p.LastName";
            paramList.add(Long.valueOf(programId));
            Object params[] = paramList.toArray(new Object[paramList.size()]);
            rs = (List<Provider>) getHibernateTemplate().find(sSQL, params);
        } else if (facilityId != null && "0".equals(facilityId) == false) {
            sSQL = "FROM  Provider p where p.Status='1' and p.ProviderNo in "
                    + "(select c.ProviderNo from ProgramProvider c where c.ProgramId in "
                    + "(select a.id from Program a where a.facilityId=?)) ORDER BY p.LastName";
            // JS 2192700 - string facilityId seems to be throwing class cast
            // exception
            Integer intFacilityId = Integer.valueOf(facilityId);
            paramList.add(intFacilityId);
            Object params[] = paramList.toArray(new Object[paramList.size()]);
            rs = (List<Provider>) getHibernateTemplate().find(sSQL, params);
        } else {
            sSQL = "FROM  Provider p where p.Status='1' ORDER BY p.LastName";
            rs = (List<Provider>) getHibernateTemplate().find(sSQL);
        }
        // List<Provider> rs =
        // getHibernateTemplate().find("FROM Provider p ORDER BY p.LastName");

        return rs;
    }

    @Override
    public List<Provider> getActiveProviders() {

        List<Provider> rs = (List<Provider>) getHibernateTemplate().find(
                "FROM  Provider p where p.Status='1' AND p.ProviderNo NOT LIKE '-%'  ORDER BY p.LastName");

        if (log.isDebugEnabled()) {
            log.debug("getProviders: # of results=" + rs.size());
        }
        return rs;
    }

    @Override
    public List<Provider> getActiveProviders(boolean filterOutSystemAndImportedProviders) {

        List<Provider> rs = null;

        if (!filterOutSystemAndImportedProviders) {
            rs = (List<Provider>) getHibernateTemplate().find(
                    "FROM  Provider p where p.Status='1' ORDER BY p.LastName");
        } else {
            rs = (List<Provider>) getHibernateTemplate().find(
                    "FROM  Provider p where p.Status='1' AND p.ProviderNo > -1 ORDER BY p.LastName");
        }

        if (log.isDebugEnabled()) {
            log.debug("getProviders: # of results=" + rs.size());
        }
        return rs;
    }

    @Override
    public List<Provider> getActiveProvidersByRole(String role) {

        List<Provider> rs = (List<Provider>) getHibernateTemplate().find(
                "select p FROM Provider p, SecUserRole s where p.ProviderNo = s.ProviderNo and p.Status='1' and s.RoleName = ? order by p.LastName, p.FirstName",
                role);

        if (log.isDebugEnabled()) {
            log.debug("getActiveProvidersByRole: # of results=" + rs.size());
        }
        return rs;
    }

    @Override
    public List<Provider> getDoctorsWithOhip() {
        return (List<Provider>) getHibernateTemplate().find(
                "FROM Provider p " +
                        "WHERE p.ProviderType = 'doctor' " +
                        "AND p.Status = '1' " +
                        "AND p.OhipNo IS NOT NULL " +
                        "ORDER BY p.LastName, p.FirstName");
    }

    @Override
    public List<Provider> getBillableProviders() {
        List<Provider> rs = (List<Provider>) getHibernateTemplate()
                .find("FROM Provider p where p.OhipNo != '' and p.Status = '1' order by p.LastName");
        return rs;
    }

    /**
     * Add loggedininfo to excluded logged in provider.
     * Usefull when setting personal preferrences.
     *
     * @param loggedInInfo
     * @return
     */
    @Override
    public List<Provider> getBillableProvidersInBC(LoggedInInfo loggedInInfo) {
        return (List<Provider>) getHibernateTemplate().find(
                "FROM Provider p where (p.OhipNo <> '' or p.RmaNo <> ''  or p.BillingNo <> '' or p.HsoNo <> '') and p.Status = '1' and p.ProviderNo not like ? order by p.LastName",
                loggedInInfo.getLoggedInProviderNo());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Provider> getBillableProvidersInBC() {
        List<Provider> rs = (List<Provider>) getHibernateTemplate().find(
                "FROM Provider p where (p.OhipNo <> '' or p.RmaNo <> ''  or p.BillingNo <> '' or p.HsoNo <> '')and p.Status = '1' order by p.LastName");
        return rs;
    }

    @Override
    public List<Provider> getProviders(boolean active) {

        List<Provider> rs = (List<Provider>) getHibernateTemplate().find(
                "FROM  Provider p where p.Status='" + (active ? 1 : 0) + "' order by p.LastName");
        return rs;
    }

    @Override
    public List<Provider> getActiveProviders(String providerNo, Integer shelterId) {
        //
        String sql;
        if (shelterId == null || shelterId.intValue() == 0)
            sql = "FROM  Provider p where p.Status='1'" +
                    " and p.ProviderNo in (select sr.providerNo from Secuserrole sr " +
                    " where sr.orgcd in (select o.code from LstOrgcd o, Secuserrole srb " +
                    " where o.codecsv  like '%' || srb.orgcd || ',%' and srb.providerNo =?))" +
                    " ORDER BY p.LastName";
        else
            sql = "FROM  Provider p where p.Status='1'" +
                    " and p.ProviderNo in (select sr.providerNo from Secuserrole sr " +
                    " where sr.orgcd in (select o.code from LstOrgcd o, Secuserrole srb " +
                    " where o.codecsv like '%S" + shelterId.toString()
                    + ",%' and o.codecsv like '%' || srb.orgcd || ',%' and srb.providerNo =?))" +
                    " ORDER BY p.LastName";

        ArrayList<Object> paramList = new ArrayList<Object>();
        paramList.add(providerNo);

        Object params[] = paramList.toArray(new Object[paramList.size()]);

        List<Provider> rs = (List<Provider>) getHibernateTemplate().find(sql, params);

        if (log.isDebugEnabled()) {
            log.debug("getProviders: # of results=" + rs.size());
        }
        return rs;
    }

    @Override
    public List<Provider> getActiveProvider(String providerNo) {

        String sql = "FROM Provider p where p.Status='1' and p.ProviderNo =?";

        ArrayList<Object> paramList = new ArrayList<Object>();
        paramList.add(providerNo);

        Object params[] = paramList.toArray(new Object[paramList.size()]);

        List<Provider> rs = (List<Provider>) getHibernateTemplate().find(sql, params);

        if (log.isDebugEnabled()) {
            log.debug("getProvider: # of results=" + rs.size());
        }
        return rs;
    }

    @Override
    public List<Provider> search(String name) {
        boolean isOracle = OscarProperties.getInstance().getDbType().equals(
                "oracle");
        // Session session = getSession();
        Session session = currentSession();

        Criteria c = session.createCriteria(Provider.class);
        if (isOracle) {
            c.add(Restrictions.or(Expression.ilike("FirstName", name + "%"),
                    Expression.ilike("LastName", name + "%")));
        } else {
            c.add(Restrictions.or(Expression.like("FirstName", name + "%"),
                    Expression.like("LastName", name + "%")));
        }
        c.addOrder(Order.asc("ProviderNo"));

        List<Provider> results = new ArrayList<Provider>();

        try {
            results = c.list();
        } finally {
            // this.releaseSession(session);
            //session.close();
        }

        if (log.isDebugEnabled()) {
            log.debug("search: # of results=" + results.size());
        }
        return results;
    }

    @Override
    public List<Provider> getProvidersByTypeWithNonEmptyOhipNo(String type) {
        List<Provider> results = (List<Provider>) this.getHibernateTemplate().find(
                "from Provider p where p.ProviderType = ? and p.OhipNo <> ''", type);
        return results;
    }

    @Override
    public List<Provider> getProvidersByType(String type) {

        List<Provider> results = (List<Provider>) this.getHibernateTemplate().find(
                "from Provider p where p.ProviderType = ?", type);

        if (log.isDebugEnabled()) {
            log.debug("getProvidersByType: type=" + type + ",# of results="
                    + results.size());
        }

        return results;
    }

    @Override
    public List<Provider> getProvidersByTypePattern(String typePattern) {

        List<Provider> results = (List<Provider>) this.getHibernateTemplate().find(
                "from Provider p where p.ProviderType like ?", typePattern);
        return results;
    }

    @Override
    public List getShelterIds(String provider_no) {

        String sql = "select distinct c.id as shelter_id from lst_shelter c, lst_orgcd a, secUserRole b  where instr('RO',substr(b.orgcd,1,1)) = 0 and a.codecsv like '%' || b.orgcd || ',%'"
                +
                " and b.provider_no=? and a.codecsv like '%S' || c.id  || ',%'";
        // Session session = getSession();
        Session session = currentSession();

        Query query = session.createSQLQuery(sql);
        ((SQLQuery) query).addScalar("shelter_id", StandardBasicTypes.INTEGER);
        query.setString(1, provider_no);
        List lst = new ArrayList();
        try {
            lst = query.list();
        } finally {
            // this.releaseSession(session);
            //session.close();
        }
        return lst;

    }

    @Override
    public void addProviderToFacility(String provider_no, int facilityId) {
        try {
            ProviderFacility pf = new ProviderFacility();
            pf.setId(new ProviderFacilityPK());
            pf.getId().setProviderNo(provider_no);
            pf.getId().setFacilityId(facilityId);
            ProviderFacilityDao pfDao = SpringUtils.getBean(ProviderFacilityDao.class);
            pfDao.persist(pf);
        } catch (RuntimeException e) {
            // chances are it's a duplicate unique entry exception so it's safe
            // to ignore.
            // this is still unexpected because duplicate calls shouldn't be
            // made
            log.warn("Unexpected exception occurred.", e);
        }
    }

    @Override
    public void removeProviderFromFacility(String provider_no,
                                           int facilityId) {
        ProviderFacilityDao dao = SpringUtils.getBean(ProviderFacilityDao.class);
        for (ProviderFacility p : dao.findByProviderNoAndFacilityId(provider_no, facilityId)) {
            dao.remove(p.getId());
        }
    }

    @Override
    public List<Integer> getFacilityIds(String provider_no) {
        // Session session = getSession();
        Session session = currentSession();
        try {
            SQLQuery query = session.createSQLQuery(
                    "select facility_id from provider_facility,Facility where Facility.id=provider_facility.facility_id and Facility.disabled=0 and provider_no=\'"
                            + provider_no + "\'");
            List<Integer> results = query.list();
            return results;
        } finally {
            // this.releaseSession(session);
            //session.close();
        }
    }

    @Override
    public List<String> getProviderIds(int facilityId) {
        // Session session = getSession();
        Session session = currentSession();
        try {
            SQLQuery query = session
                    .createSQLQuery("select provider_no from provider_facility where facility_id=" + facilityId);
            List<String> results = query.list();
            return results;
        } finally {
            // this.releaseSession(session);
            //session.close();
        }

    }

    @Override
    public void updateProvider(Provider provider) {
        this.getHibernateTemplate().update(provider);
    }

    @Override
    public void saveProvider(Provider provider) {
        this.getHibernateTemplate().save(provider);
    }

    @Override
    public Provider getProviderByPractitionerNo(String practitionerNo) {
        if (practitionerNo == null || practitionerNo.length() <= 0) {
            return null;
        }

        List<Provider> providerList = (List<Provider>) getHibernateTemplate()
                .find("From Provider p where p.practitionerNo=?", new Object[]{practitionerNo});

        if (providerList.size() > 1) {
            logger.warn("Found more than 1 provider with practitionerNo=" + practitionerNo);
        }
        if (providerList.size() > 0)
            return providerList.get(0);

        return null;
    }

    @Override
    public Provider getProviderByPractitionerNo(String practitionerNoType, String practitionerNo) {
        return getProviderByPractitionerNo(new String[]{practitionerNoType}, practitionerNo);
    }

    @Override
    public Provider getProviderByPractitionerNo(String[] practitionerNoTypes, String practitionerNo) {
        if (practitionerNoTypes == null || practitionerNoTypes.length <= 0) {
            throw new IllegalArgumentException();
        }
        if (practitionerNo == null || practitionerNo.length() <= 0) {
            throw new IllegalArgumentException();
        }

        List<Provider> providerList = (List<Provider>) getHibernateTemplate().findByNamedParam(
                "From Provider p where p.practitionerNoType IN (:types) AND p.practitionerNo=:pId",
                new String[]{"types", "pId"}, new Object[]{practitionerNoTypes, practitionerNo});
        // List<Provider> providerList = getHibernateTemplate().find("From Provider p
        // where p.practitionerNoType IN (:types) AND p.practitionerNo=?",new
        // Object[]{practitionerNo});

        if (providerList.size() > 1) {
            logger.warn("Found more than 1 provider with practitionerNo=" + practitionerNo);
        }
        if (providerList.size() > 0)
            return providerList.get(0);

        return null;
    }

    @Override
    public List<String> getUniqueTeams() {

        List<String> providerList = (List<String>) getHibernateTemplate()
                .find("select distinct p.Team From Provider p");

        return providerList;
    }

    @Override
    public List<Provider> getBillableProvidersOnTeam(Provider p) {

        List<Provider> providers = (List<Provider>) this.getHibernateTemplate().find(
                "from Provider p where status='1' and ohip_no!='' and p.Team=? order by last_name, first_name",
                p.getTeam());

        return providers;
    }

    @Override
    public List<Provider> getBillableProvidersByOHIPNo(String ohipNo) {
        if (ohipNo == null || ohipNo.length() <= 0) {
            throw new IllegalArgumentException();
        }

        List<Provider> providers = (List<Provider>) this.getHibernateTemplate()
                .find("from Provider p where ohip_no like ? order by last_name, first_name", ohipNo);

        if (providers.size() > 1) {
            logger.warn("Found more than 1 provider with ohipNo=" + ohipNo);
        }
        if (providers.isEmpty())
            return null;
        else
            return providers;
    }

    @Override
    public List<Provider> getProvidersWithNonEmptyOhip(LoggedInInfo loggedInInfo) {
        return (List<Provider>) getHibernateTemplate().find(
                "FROM Provider WHERE ohip_no != '' and ProviderNo not like ? order by last_name, first_name",
                loggedInInfo.getLoggedInProviderNo());
    }

    /**
     * Gets all providers with non-empty OHIP number ordered by last,then first name
     *
     * @return Returns the all found providers
     */
    @Override
    public List<Provider> getProvidersWithNonEmptyOhip() {
        return (List<Provider>) getHibernateTemplate()
                .find("FROM Provider WHERE ohip_no != '' order by last_name, first_name");
    }

    @Override
    public List<Provider> getCurrentTeamProviders(String providerNo) {
        String hql = "SELECT p FROM Provider p "
                + "WHERE p.Status='1' and p.OhipNo != '' "
                + "AND (p.ProviderNo='" + providerNo
                + "' or team=(SELECT p2.Team FROM Provider p2 where p2.ProviderNo='" + providerNo + "')) "
                + "ORDER BY p.LastName, p.FirstName";

        return (List<Provider>) this.getHibernateTemplate().find(hql);
    }

    @Override
    public List<String> getActiveTeams() {
        List<String> providerList = (List<String>) getHibernateTemplate()
                .find("select distinct p.Team From Provider p where p.Status = '1' and p.Team != '' order by p.Team");
        return providerList;
    }

    @NativeSql({"provider", "providersite"})
    @Override
    public List<String> getActiveTeamsViaSites(String providerNo) {
        // Session session = getSession();
        Session session = currentSession();
        try {
            // providersite is not mapped in hibernate - this can be rewritten w.o.
            // subselect with a cross product IHMO
            SQLQuery query = session.createSQLQuery(
                    "select distinct team from provider p inner join providersite s on s.provider_no = p.provider_no " +
                            " where s.site_id in (select site_id from providersite where provider_no = '" + providerNo
                            + "') order by team ");
            return query.list();
        } finally {
            // this.releaseSession(session);
            //session.close();
        }
    }

    @Override
    public List<Provider> getProviderByPatientId(Integer patientId) {
        String hql = "SELECT p FROM Provider p, Demographic d "
                + "WHERE d.ProviderNo = p.ProviderNo "
                + "AND d.DemographicNo = ?";
        return (List<Provider>) this.getHibernateTemplate().find(hql, patientId);
    }

    @Override
    public List<Provider> getDoctorsWithNonEmptyCredentials() {
        String sql = "FROM Provider p WHERE p.ProviderType = 'doctor' " +
                "AND p.Status='1' " +
                "AND p.OhipNo IS NOT NULL " +
                "AND p.OhipNo != '' " +
                "ORDER BY p.LastName, p.FirstName";
        return (List<Provider>) getHibernateTemplate().find(sql);
    }

    @Override
    public List<Provider> getProvidersWithNonEmptyCredentials() {
        String sql = "FROM Provider p WHERE p.Status='1' " +
                "AND p.OhipNo IS NOT NULL " +
                "AND p.OhipNo != '' " +
                "ORDER BY p.LastName, p.FirstName";
        return (List<Provider>) getHibernateTemplate().find(sql);
    }

    @Override
    public List<String> getProvidersInTeam(String teamName) {
        List<String> providerList = (List<String>) getHibernateTemplate()
                .find("select distinct p.ProviderNo from Provider p  where p.Team = ?", new Object[]{teamName});
        return providerList;
    }

    @Override
    public List<Object[]> getDistinctProviders() {
        List<Object[]> providerList = (List<Object[]>) getHibernateTemplate()
                .find("select distinct p.ProviderNo, p.ProviderType from Provider p ORDER BY p.LastName");
        return providerList;
    }

    @Override
    public List<String> getRecordsAddedAndUpdatedSinceTime(Date date) {
        @SuppressWarnings("unchecked")
        List<String> providers = (List<String>) getHibernateTemplate()
                .find("select distinct p.ProviderNo From Provider p where p.lastUpdateDate > ? ", date);

        return providers;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Provider> searchProviderByNamesString(String searchString, int startIndex, int itemsToReturn) {
        String sqlCommand = "select x from Provider x";
        if (searchString != null) {
            if (searchString.indexOf(",") != -1 && searchString.split(",").length > 1
                    && searchString.split(",")[1].length() > 0) {
                sqlCommand = sqlCommand + " where x.LastName like :ln AND x.FirstName like :fn";
            } else {
                sqlCommand = sqlCommand + " where x.LastName like :ln";
            }

        }

        // Session session = this.getSession();
        Session session = currentSession();
        try {
            Query q = session.createQuery(sqlCommand);
            if (searchString != null) {
                q.setParameter("ln", "%" + searchString.split(",")[0] + "%");
                if (searchString.indexOf(",") != -1 && searchString.split(",").length > 1
                        && searchString.split(",")[1].length() > 0) {
                    q.setParameter("fn", "%" + searchString.split(",")[1] + "%");

                }
            }
            q.setFirstResult(startIndex);
            q.setMaxResults(itemsToReturn);
            return (q.list());
        } finally {
            // this.releaseSession(session);
            //session.close();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Provider> search(String term, boolean active, int startIndex, int itemsToReturn) {
        String sqlCommand = "select x from Provider x WHERE x.Status = :status ";

        if (term != null && term.length() > 0) {
            sqlCommand += "AND (x.LastName like :term  OR x.FirstName like :term) ";
        }

        sqlCommand += " ORDER BY x.LastName,x.FirstName";

        // Session session = this.getSession();
        Session session = currentSession();
        try {
            Query q = session.createQuery(sqlCommand);

            q.setString("status", active ? "1" : "0");
            if (term != null && term.length() > 0) {
                q.setString("term", term + "%");
            }

            q.setFirstResult(startIndex);
            q.setMaxResults(itemsToReturn);
            return (q.list());
        } finally {
            // this.releaseSession(session);
            //session.close();
        }
    }

    @NativeSql({"provider", "appointment"})
    @Override
    public List<String> getProviderNosWithAppointmentsOnDate(Date appointmentDate) {
        // Session session = getSession();
        Session session = currentSession();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String sql = "SELECT p.provider_no FROM provider p WHERE p.provider_no IN (SELECT DISTINCT a.provider_no FROM appointment a WHERE a.appointment_date = '"
                    + sdf.format(appointmentDate) + "') " +
                    "AND p.Status='1'";
            SQLQuery query = session.createSQLQuery(sql);

            return query.list();
        } finally {
            // this.releaseSession(session);
            //session.close();
        }
    }

    @Override
    public List<Provider> getOlisHicProviders() {
        UserPropertyDAO userPropertyDAO = SpringUtils.getBean(UserPropertyDAO.class);
        // Session session = getSession();
        Session session = currentSession();
        String sql = "FROM Provider p WHERE p.practitionerNo IS NOT NULL AND p.practitionerNo != ''";
        Query query = session.createQuery(sql);
        List<Provider> practitionerNoProviders = query.list();

        List<Provider> results = new ArrayList<Provider>();
        for (Provider practitionerNoProvider : practitionerNoProviders) {
            String olisType = userPropertyDAO.getStringValue(practitionerNoProvider.getProviderNo(),
                    UserProperty.OFFICIAL_OLIS_IDTYPE);
            if (olisType != null && !olisType.isEmpty()) {
                results.add(practitionerNoProvider);
            }
        }
        //session.close();
        return results;
    }

    @Override
    public Provider getProviderByPractitionerNoAndOlisType(String practitionerNo, String olisIdentifierType) {
        UserPropertyDAO userPropertyDAO = SpringUtils.getBean(UserPropertyDAO.class);
        String sql = "FROM Provider p WHERE p.practitionerNo=?";

        List<Provider> providers = (List<Provider>) getHibernateTemplate().find(sql, practitionerNo);

        if (!providers.isEmpty()) {
            Provider provider = providers.get(0);
            String olisType = userPropertyDAO.getStringValue(provider.getProviderNo(),
                    UserProperty.OFFICIAL_OLIS_IDTYPE);
            if (olisIdentifierType.equals(olisType)) {
                return providers.get(0);
            }
        }
        return null;
    }

    @Override
    public List<Provider> getOlisProvidersByPractitionerNo(List<String> practitionerNumbers) {
        // Session session = getSession();
        Session session = currentSession();
        String sql = "FROM Provider p WHERE p.practitionerNo IN (:practitionerNumbers)";
        Query query = session.createQuery(sql);
        query.setParameterList("practitionerNumbers", practitionerNumbers);
        List<Provider> providers = query.list();
        //session.close();
        return providers;
    }

    /**
     * Gets a list of provider numbers based on the provided list of provider
     * numbers
     *
     * @param providerNumbers The list of provider numbers to get the related
     *                        objects for
     * @return A list of providers
     */
    @Override
    public List<Provider> getProvidersByIds(List<String> providerNumbers) {
        // Session session = getSession();
        Session session = currentSession();
        String sql = "FROM Provider p WHERE p.ProviderNo IN (:providerNumbers)";
        Query query = session.createQuery(sql);
        query.setParameterList("providerNumbers", providerNumbers);

        List<Provider> providers = query.list();
        //session.close();
        return providers;
    }

    /**
     * Gets a map of provider names with the provider number as the map key based on
     * the provided list of provider numbers
     *
     * @param providerNumbers A list of provider numbers to get the name map for
     * @return A map of provider names with their related provider number as the key
     */
    @Override
    public Map<String, String> getProviderNamesByIdsAsMap(List<String> providerNumbers) {
        Map<String, String> providerNameMap = new HashMap<>();
        List<Provider> providers = getProvidersByIds(providerNumbers);

        for (Provider provider : providers) {
            providerNameMap.put(provider.getProviderNo(), provider.getFullName());
        }

        return providerNameMap;
    }
}
