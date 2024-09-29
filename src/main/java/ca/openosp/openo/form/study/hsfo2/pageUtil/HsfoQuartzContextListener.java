//CHECKSTYLE:OFF
package ca.openosp.openo.form.study.hsfo2.pageUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.Logger;
import ca.openosp.openo.ehrutil.MiscUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class HsfoQuartzContextListener implements ServletContextListener, ApplicationListener<ContextRefreshedEvent> {
    protected static Logger logger = MiscUtils.getLogger();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // logger.info("============== start quartz scheduler from ContextListener ===========");
        // try {
        //     HsfoQuartzServlet.schedule();
        // } catch (Exception e) {
        //     logger.error("Failed to start quartz scheduler", e);
        // }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.info("============== start quartz scheduler from ContextListener ===========");
        try {
            HsfoQuartzServlet.schedule();
        } catch (Exception e) {
            logger.error("Failed to start quartz scheduler", e);
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Handle any cleanup if necessary
    }
}