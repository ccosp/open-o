package oscar.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import oscar.util.HibernateTestDao;

public class TestHiberante {
    
    public static void main(String[] args) {
          ApplicationContext context = new ClassPathXmlApplicationContext("applicationContextTest.xml");
    HibernateTestDao dao = (HibernateTestDao) context.getBean("hibernateTestDao");
    dao.findByExample();
    }
}
