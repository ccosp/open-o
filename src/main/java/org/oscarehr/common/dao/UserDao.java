package org.oscarehr.common.dao;
import java.util.List;
// import org.oscarehr.common.dao.utils.EntityDataGenerator;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.oscarehr.common.model.Contact;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.hibernate.HibernateException;
import java.sql.SQLException;
import org.hibernate.Query;
import com.quatro.model.security.Secrole;



@Repository
public class UserDao extends HibernateDaoSupport{
    
    // private HibernateTemplate hibernateTemplate;

    // @Autowired
    // public UserDao(HibernateTemplate hibernateTemplate) {
    //     this.hibernateTemplate = hibernateTemplate;
    // }


    // public List<String> findDetails(){
    //     // String sql = "FROM Contact";
        
    //     try{
    //         List<String> rs =  (List<String>) template.find("SELECT * FROM Secrole");
    // 	    System.out.println("Contact find:"+rs); 
    //         return rs;

    //     } catch(Exception e){
    //         e.printStackTrace();
    //         throw e;
    //     }   
    // }

    public List<Secrole> getRoles() {
        @SuppressWarnings("unchecked")
        List<Secrole> results = this.getHibernateTemplate().find("from Secrole r order by roleName");

        return results;
    }
    
    // @Autowired
    // private SessionFactory sessionFactory;

    // public void saveContact(Contact contact){
    //     sessionFactory.getCurrentSession().saveOrUpdate(contact);
    // }

    // @SuppressWarnings("unchecked")
    // public List<Contact> getContactList() {
    //     return (List<Contact>) sessionFactory.getCurrentSession().createCriteria(Contact.class).list();
    // }
}