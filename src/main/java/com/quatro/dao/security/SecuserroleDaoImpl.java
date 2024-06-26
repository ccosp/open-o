/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
 * Copyright (c) 2005, 2009 IBM Corporation and others.
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
 * Contributors:
 *     <Quatro Group Software Systems inc.>  <OSCAR Team>
 *
 * Modifications made by Magenta Health in 2024.
 */

 package com.quatro.dao.security;

 import java.util.Date;
 import java.util.List;
 
 import org.apache.commons.lang.StringEscapeUtils;
 import org.apache.logging.log4j.Logger;
 import org.hibernate.LockMode;
 import org.hibernate.Query;
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.criterion.Example;
 import org.oscarehr.PMmodule.web.formbean.StaffForm;
 import org.oscarehr.util.MiscUtils;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
 import org.springframework.stereotype.Repository;
 import org.springframework.transaction.annotation.Transactional;
 
 import com.quatro.model.security.Secuserrole;
 
 @Repository
 @Transactional
 public class SecuserroleDaoImpl extends HibernateDaoSupport implements SecuserroleDao {
        private static final Logger logger = MiscUtils.getLogger();
    
        @Autowired
        public void setSessionFactoryOverride(SessionFactory sessionFactory) {
            super.setSessionFactory(sessionFactory);
        }
    
        @Override
        @Transactional
        public void saveAll(List<Secuserrole> list) {
            logger.debug("saving ALL Secuserrole instances");
            Session session = currentSession();
            try {
                for (Secuserrole obj : list) {
                    obj.setLastUpdateDate(new Date());
                    int rowcount = update(obj);
                    if (rowcount <= 0) {
                        session.save(obj);
                    }
                }
                logger.debug("save ALL successful");
            } catch (RuntimeException re) {
                logger.error("save ALL failed", re);
                throw re;
            }
        }
    
        @Override
        @Transactional
        public void save(Secuserrole transientInstance) {
            logger.debug("saving Secuserrole instance");
            Session session = currentSession();
            try {
                transientInstance.setLastUpdateDate(new Date());
                session.saveOrUpdate(transientInstance);
                logger.debug("save successful");
            } catch (RuntimeException re) {
                logger.error("save failed", re);
                throw re;
            }
        }
    
        @Override
        @Transactional
        public void updateRoleName(Integer id, String roleName) {
            Secuserrole sur = this.getHibernateTemplate().get(Secuserrole.class, id);
            if (sur != null) {
                sur.setRoleName(roleName);
                sur.setLastUpdateDate(new Date());
                this.getHibernateTemplate().update(sur);
            }
        }
    
        @Override
        @Transactional
        public void delete(Secuserrole persistentInstance) {
            logger.debug("deleting Secuserrole instance");
            Session session = currentSession();
            try {
                session.delete(persistentInstance);
                logger.debug("delete successful");
            } catch (RuntimeException re) {
                logger.error("delete failed", re);
                throw re;
            }
        }
    
        @Override
        @Transactional
        public int deleteByOrgcd(String orgcd) {
            logger.debug("deleting Secuserrole by orgcd");
            try {
                return getHibernateTemplate().bulkUpdate("delete Secuserrole as model where model.orgcd =?", orgcd);
            } catch (RuntimeException re) {
                logger.error("delete failed", re);
                throw re;
            }
        }
    
        @Override
        @Transactional
        public int deleteByProviderNo(String providerNo) {
            logger.debug("deleting Secuserrole by providerNo");
            try {
                return getHibernateTemplate().bulkUpdate("delete Secuserrole as model where model.providerNo =?", providerNo);
            } catch (RuntimeException re) {
                logger.error("delete failed", re);
                throw re;
            }
        }
    
        @Override
        @Transactional
        public int deleteById(Integer id) {
            logger.debug("deleting Secuserrole by ID");
            try {
                Session session = currentSession();
                String hql = "delete from Secuserrole where id = :id";
                Query<?> query = session.createQuery(hql);
                query.setParameter("id", id);
                return query.executeUpdate();
            } catch (RuntimeException re) {
                logger.error("delete failed", re);
                throw re;
            }
        }
    
        @Override
        @Transactional
        public int update(Secuserrole instance) {
            logger.debug("Update Secuserrole instance");
            Session session = currentSession();
            try {
                String queryString = "update Secuserrole as model set model.activeyn ='" + instance.getActiveyn()
                        + "' , lastUpdateDate=now() "
                        + " where model.providerNo ='" + instance.getProviderNo() + "'"
                        + " and model.roleName ='" + instance.getRoleName() + "'"
                        + " and model.orgcd ='" + instance.getOrgcd() + "'";
                Query queryObject = session.createQuery(queryString);
                return queryObject.executeUpdate();
            } catch (RuntimeException re) {
                logger.error("Update failed", re);
                throw re;
            }
        }
    
        @Override
        @Transactional(readOnly = true)
        public Secuserrole findById(Integer id) {
            logger.debug("getting Secuserrole instance with id: " + id);
            Session session = currentSession();
            try {
                Secuserrole instance = (Secuserrole) session.get("com.quatro.model.security.Secuserrole", id);
                return instance;
            } catch (RuntimeException re) {
                logger.error("get failed", re);
                throw re;
            }
        }
    
        @Override
        @Transactional(readOnly = true)
        public List<Secuserrole> findByExample(Secuserrole instance) {
            Session session = currentSession();
            logger.debug("finding Secuserrole instance by example");
            try {
                List<Secuserrole> results = session.createCriteria("com.quatro.model.security.Secuserrole")
                        .add(Example.create(instance)).list();
                logger.debug("find by example successful, result size: " + results.size());
                return results;
            } catch (RuntimeException re) {
                logger.error("find by example failed", re);
                throw re;
            }
        }
    
        @Override
        @Transactional(readOnly = true)
        public List<Secuserrole> findByProperty(String propertyName, Object value) {
            logger.debug("finding Secuserrole instance with property: " + propertyName + ", value: " + value);
            Session session = currentSession();
            try {
                String queryString = "from Secuserrole as model where model." + propertyName + "= ?";
                Query queryObject = session.createQuery(queryString);
                queryObject.setParameter(0, value);
                return queryObject.list();
            } catch (RuntimeException re) {
                logger.error("find by property name failed", re);
                throw re;
            }
        }
    
        @Override
        @Transactional(readOnly = true)
        public List<Secuserrole> findByProviderNo(Object providerNo) {
            return findByProperty(PROVIDER_NO, providerNo);
        }
    
        @Override
        @Transactional(readOnly = true)
        public List<Secuserrole> findByRoleName(Object roleName) {
            return findByProperty(ROLE_NAME, roleName);
        }
    
        @Override
        @Transactional(readOnly = true)
        public List<Secuserrole> findByOrgcd(Object orgcd, boolean activeOnly) {
            logger.debug("Find staff instance.");
            try {
                String queryString = "select a from Secuserrole a, LstOrgcd b, SecProvider p"
                        + " where a.providerNo=p.providerNo and b.code ='" + orgcd + "'";
                if (activeOnly) {
                    queryString += " and p.status='1'";
                }
                queryString = queryString + " and b.codecsv like '%' || a.orgcd || ',%'"
                        + " and not (a.orgcd like 'R%' or a.orgcd like 'O%')";
                return (List<Secuserrole>) this.getHibernateTemplate().find(queryString);
            } catch (RuntimeException re) {
                logger.error("Find staff failed", re);
                throw re;
            }
        }
    
        @Override
        @Transactional(readOnly = true)
        public List<Secuserrole> searchByCriteria(StaffForm staffForm) {
            logger.debug("Search staff instance.");
            try {
                String AND = " and ";
                String orgcd = staffForm.getOrgcd();
                String queryString = "select a from Secuserrole a, LstOrgcd b"
                        + " where b.code ='" + orgcd + "'"
                        + " and b.codecsv like '%' || a.orgcd || ',%'"
                        + " and not (a.orgcd like 'R%' or a.orgcd like 'O%')";
                String fname = staffForm.getFirstName();
                String lname = staffForm.getLastName();
                if (fname != null && fname.length() > 0) {
                    fname = StringEscapeUtils.escapeSql(fname);
                    fname = fname.toLowerCase();
                    queryString = queryString + AND + "lower(a.providerFName) like '%" + fname + "%'";
                }
                if (lname != null && lname.length() > 0) {
                    lname = StringEscapeUtils.escapeSql(lname);
                    lname = lname.toLowerCase();
                    queryString = queryString + AND + "lower(a.providerLName) like '%" + lname + "%'";
                }
                return (List<Secuserrole>) this.getHibernateTemplate().find(queryString);
            } catch (RuntimeException re) {
                logger.error("Search staff failed", re);
                throw re;
            }
        }
    
        @Override
        @Transactional(readOnly = true)
        public List<Secuserrole> findByActiveyn(Object activeyn) {
            return findByProperty(ACTIVEYN, activeyn);
        }
    
        @Override
        @Transactional(readOnly = true)
        public List<Secuserrole> findAll() {
            logger.debug("finding all Secuserrole instances");
            Session session = currentSession();
            try {
                String queryString = "from Secuserrole";
                Query queryObject = session.createQuery(queryString);
                return queryObject.list();
            } catch (RuntimeException re) {
                logger.error("find all failed", re);
                throw re;
            }
        }
    
        @Override
        @Transactional
        public Secuserrole merge(Secuserrole detachedInstance) {
            logger.debug("merging Secuserrole instance");
            Session session = currentSession();
            try {
                detachedInstance.setLastUpdateDate(new Date());
                Secuserrole result = (Secuserrole) session.merge(detachedInstance);
                logger.debug("merge successful");
                return result;
            } catch (RuntimeException re) {
                logger.error("merge failed", re);
                throw re;
            }
        }
    
        @Override
        @Transactional
        public void attachDirty(Secuserrole instance) {
            logger.debug("attaching dirty Secuserrole instance");
            Session session = currentSession();
            try {
                instance.setLastUpdateDate(new Date());
                session.saveOrUpdate(instance);
                logger.debug("attach successful");
            } catch (RuntimeException re) {
                logger.error("attach failed", re);
                throw re;
            }
        }
    
        @Override
        @Transactional
        public void attachClean(Secuserrole instance) {
            logger.debug("attaching clean Secuserrole instance");
            Session session = currentSession();
            try {
                session.lock(instance, LockMode.NONE);
                logger.debug("attach successful");
            } catch (RuntimeException re) {
                logger.error("attach failed", re);
                throw re;
            }
        }
    }