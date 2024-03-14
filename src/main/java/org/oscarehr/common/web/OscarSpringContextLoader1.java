package org.oscarehr.common.web;

import org.apache.logging.log4j.Logger;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext; // Add this import

import oscar.OscarProperties;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class OscarSpringContextLoader1 extends ContextLoaderListener {

    private static final Logger log = MiscUtils.getLogger();
    private static final String CONTEXT_BASE_NAME = "classpath:applicationContext";
    private static final String PROPERTY_MODULE_NAMES = "ModuleNames";

    @Override
    protected WebApplicationContext createWebApplicationContext(ServletContext servletContext, ApplicationContext parent) throws BeansException {
        log.info("Creating Spring context");

        ConfigurableWebApplicationContext applicationContext = (ConfigurableWebApplicationContext) new ClassPathXmlApplicationContext();
        applicationContext.setParent(parent);
        //applicationContext.setServletContext(servletContext);
        applicationContext.setServletContext(servletContext);

        // Load various contexts based on module names
        String modules = (String) OscarProperties.getInstance().get(PROPERTY_MODULE_NAMES);
        List<String> moduleList = new ArrayList<>();

        if (modules != null && !modules.isEmpty()) {
            moduleList.addAll(Arrays.asList(modules.split(",")));
        }

        // Prepare list of application context file names
        List<String> configLocations = new ArrayList<>();
        configLocations.add(CONTEXT_BASE_NAME + ".xml");

        for (String moduleName : moduleList) {
            configLocations.add(CONTEXT_BASE_NAME + moduleName + ".xml");
        }

        configLocations.forEach(s -> log.info("Preparing {}", s));

        ((ConfigurableWebApplicationContext) applicationContext).setConfigLocations(configLocations.toArray(new String[0])); // Change to setLocations        
        applicationContext.refresh();

       
        SpringUtils.setApplicationContext(applicationContext);

        return (WebApplicationContext) applicationContext;
    }
}
