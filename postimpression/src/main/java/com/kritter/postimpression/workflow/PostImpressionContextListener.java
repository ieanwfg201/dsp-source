package com.kritter.postimpression.workflow;

import com.kritter.constants.LoggingResource;
import com.kritter.core.workflow.Workflow;
import com.kritter.device.common.HandsetPopulationProvider;
import com.kritter.fanoutinfra.executorservice.common.KExecutor;
import com.kritter.geo.common.entity.reader.CountryDetectionCache;
import com.kritter.geo.common.entity.reader.ISPDetectionCache;
import com.kritter.geo.entity.populator.DataLoaderExecutor;
import com.kritter.postimpression.cache.ConversionEventIdStorageCache;
import com.kritter.postimpression.cache.EventIdStorageCache;
import org.apache.log4j.PropertyConfigurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.ServletContext;
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
    private EventIdStorageCache billableEventStorage;
    private EventIdStorageCache macroClickEventStorage;
    private KExecutor kexecutor;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent)
    {
        ServletContext servletContext = servletContextEvent.getServletContext();

        System.out.println("Postimpression context initialization event received, going for initialization...");

        /*Enable synchronous logging using log4j2*/
        System.setProperty("log4j.configurationFile","/var/data/kritter/log4j2-postimpression.xml");
        System.setProperty("Log4jContextSelector","org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        ((LifeCycle) LogManager.getContext()).start();

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
        this.billableEventStorage = (EventIdStorageCache)this.beanFactory.getBean("billable_event_id_storage");
        this.macroClickEventStorage = (EventIdStorageCache)this.beanFactory.getBean("macro_click_event_id_storage");
        this.kexecutor = KExecutor.getKExecutor("postimpression.application");

        servletContext.setAttribute("workflow",this.postImpressionWorkflow);

        System.out.print("Completed initialization of PostImpressionContextListener...");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent)
    {
        ((LifeCycle) LogManager.getContext()).stop();

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

        if(null != this.billableEventStorage)
            this.billableEventStorage.releaseResources();

        if(null != this.macroClickEventStorage)
            this.macroClickEventStorage.releaseResources();
        
        if(null != this.kexecutor){
            this.kexecutor.shutdown();
        }

        System.out.println("Completed destruction of PostImpressionContextListener...");
    }
}
