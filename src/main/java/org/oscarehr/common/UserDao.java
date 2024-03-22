package org.oscarehr.common;
import java.util.List;
// import org.oscarehr.common.dao.utils.EntityDataGenerator;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.hibernate.HibernateException;
import java.sql.SQLException;
import org.hibernate.Query;
import org.oscarehr.common.Secrole;



@Repository
public class UserDao extends HibernateDaoSupport{
    
    // HibernateTemplate template;
    private SessionFactory sessionFactory;
    public UserDao(SessionFactory sessionfactory){
        this.sessionFactory = sessionfactory;
    }
     
    // public void setTemplate(HibernateTemplate template) { 
    //     setTemplate(template); 
    //     //this.template = template;  
    // }  

    public List<Secrole> getRoles() {
        @SuppressWarnings("unchecked")
        List<Secrole> results = getHibernateTemplate().find("from Secrole r order by roleName");

        return results;
    }


    // public List<String> findDetails(){
    //     // String sql = "FROM Contact";
        
    //     try{
    //         List<String> rs =  (List<String>) template.find("SELECT * FROM CONTACT");
    // 	    System.out.println("Contact find:"+rs); 
    //         return rs;

    //     } catch(Exception e){
    //         e.printStackTrace();
    //     }   
    // }
    
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
