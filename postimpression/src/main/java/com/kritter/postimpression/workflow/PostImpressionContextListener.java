package com.kritter.postimpression.workflow;

import com.kritter.constants.LoggingResource;
import com.kritter.core.workflow.Workflow;
import com.kritter.device.HandsetPopulationProvider;
import com.kritter.geo.common.entity.reader.CountryDetectionCache;
import com.kritter.geo.common.entity.reader.ISPDetectionCache;
import com.kritter.geo.entity.populator.DataLoaderExecutor;
import com.kritter.postimpression.cache.ConversionEventIdStorageCache;
import com.kritter.postimpression.cache.EventIdStorageCache;
import org.apache.log4j.PropertyConfigurator;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Properties;

public class PostImpressionContextListener implements ServletContextListener
{
    private ApplicationContext applicationContext;
    private BeanFactory beanFactory;
    private Workflow postImpressionWorkflow;
    private DataLoaderExecutor geoDataLoaderExecutor;
    private CountryDetectionCache countryDetectionCache;
    private ISPDetectionCache ispDetectionCache;
    private HandsetPopulationProvider handsetPopulationProvider;
    private EventIdStorageCache cscEventStorage;
    private ConversionEventIdStorageCache conversionEventStorage;
    private ConversionEventIdStorageCache cookieBasedConversionEventStorage;
    private EventIdStorageCache winEventStorage;
    private EventIdStorageCache clickEventStorage;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent)
    {

        System.out.println("Postimpression context initialization event received, going for initialization...");

        System.out.println("Configure log4j in postimpression using properties file path " +
                LoggingResource.LOG_4J_PROPERTIES_FILE_PATH_POSTIMPRESSION);

        PropertyConfigurator.configure(LoggingResource.LOG_4J_PROPERTIES_FILE_PATH_POSTIMPRESSION);

        System.out.println("Done configuring log4j, now intializing the entire Postimpression Workflow");

        Properties properties = new Properties();
        properties.put("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
        RuntimeSingleton.init(properties);

        this.applicationContext = new ClassPathXmlApplicationContext("workflow.xml");
        this.beanFactory = this.applicationContext;
        this.postImpressionWorkflow = (Workflow)this.beanFactory.getBean("Workflow");
        this.geoDataLoaderExecutor = (DataLoaderExecutor)this.beanFactory.getBean("geo_data_loader");
        this.countryDetectionCache = (CountryDetectionCache)this.beanFactory.getBean("country_detector");
        this.ispDetectionCache = (ISPDetectionCache)this.beanFactory.getBean("isp_detector");
        this.handsetPopulationProvider = (HandsetPopulationProvider) this.beanFactory.getBean("handset_data_populator");
        this.cscEventStorage = (EventIdStorageCache)this.beanFactory.getBean("csc_event_id_storage");
        this.conversionEventStorage = (ConversionEventIdStorageCache)this.beanFactory.
                                                                     getBean("conversion_event_id_storage");
        this.cookieBasedConversionEventStorage = (ConversionEventIdStorageCache)this.beanFactory.
                                                                     getBean("cookie_based_conversion_event_id_storage");
        this.winEventStorage = (EventIdStorageCache)this.beanFactory.getBean("win_event_id_storage");
        this.clickEventStorage = (EventIdStorageCache)this.beanFactory.getBean("click_event_id_storage");

        servletContextEvent.getServletContext().setAttribute("workflow",this.postImpressionWorkflow);

        System.out.print("Completed initialization of PostImpressionContextListener...");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent)
    {
        this.postImpressionWorkflow.destroy();

        if(null != this.geoDataLoaderExecutor)
            this.geoDataLoaderExecutor.releaseResources();

        if(null != this.countryDetectionCache)
            this.countryDetectionCache.releaseResources();

        if(null != this.ispDetectionCache)
            this.ispDetectionCache.releaseResources();

        if(null != this.handsetPopulationProvider)
            this.handsetPopulationProvider.releaseResources();

        if(null != this.cscEventStorage)
            this.cscEventStorage.releaseResources();

        if(null != this.conversionEventStorage)
            this.conversionEventStorage.releaseResources();

        if(null != this.cookieBasedConversionEventStorage)
            this.cookieBasedConversionEventStorage.releaseResources();

        if(null != this.winEventStorage)
            this.winEventStorage.releaseResources();

        if(null != this.clickEventStorage)
            this.clickEventStorage.releaseResources();

        System.out.println("Completed destruction of PostImpressionContextListener...");
    }
}
