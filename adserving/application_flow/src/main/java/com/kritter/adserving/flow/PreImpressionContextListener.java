package com.kritter.adserving.flow;

import com.kritter.common.caches.ext_supply_attr_cache.ExternalSupplyAttributesCache;
import com.kritter.common.caches.slot_size_cache.CreativeSlotSizeCache;
import com.kritter.common.site.cache.SiteMetaDataLoader;
import com.kritter.constants.LoggingResource;
import com.kritter.core.workflow.Workflow;
import com.kritter.device.common.HandsetPopulationProvider;
import com.kritter.fanoutinfra.executorservice.common.KExecutor;
import com.kritter.geo.common.entity.reader.*;
import com.kritter.geo.common.entity.writer.IspMappingsLoader;
import com.kritter.geo.entity.populator.DataLoaderExecutor;
import com.mysql.jdbc.AbandonedConnectionCleanupThread;
import org.apache.log4j.PropertyConfigurator;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Properties;

public class PreImpressionContextListener implements ServletContextListener
{

    private ApplicationContext applicationContext;
    private BeanFactory beanFactory;
    private Workflow preImpressionWorkflow;
    private DataLoaderExecutor geoDataLoaderExecutor;
    private IspMappingsLoader ispMappingsLoader;
    private CountryDetectionCache countryDetectionCache;
    private ISPDetectionCache ispDetectionCache;
    private StateDetectionCache stateDetectionCache;
    private CityDetectionCache cityDetectionCache;
    private HandsetPopulationProvider handsetPopulationProvider;
    private CustomIPFileDetectionCache customIPFileDetectionCache;
    private CreativeSlotSizeCache creativeSlotSizeCache;
    private ZipCodeDetectionCache zipCodeDetectionCache;
    private ZipCodeFileDataCache zipCodeFileDataCache;
    private ExternalSupplyAttributesCache externalSupplyAttributesCache;
    private SiteMetaDataLoader siteMetaDataLoader;
    private KExecutor kexecutor;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent)
    {
        ServletContext servletContext = servletContextEvent.getServletContext();

        System.out.println("Servlet Context Initialization event received, going to initialize ad serving...");

        String log4jFilePath = servletContext.getInitParameter(LoggingResource.LOG4J_PROPERTIES_ADSERVING_KEY);

        System.out.println("Configure log4j in ad serving using properties file path " + log4jFilePath);
        PropertyConfigurator.configure(log4jFilePath);

        System.out.println("Done configuring log4j, now initializing the entire ad serving Workflow");

        Properties properties = new Properties();
        properties.put("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
        RuntimeSingleton.init(properties);

        this.applicationContext            = new ClassPathXmlApplicationContext("workflow.xml");
        this.beanFactory                   = this.applicationContext;

        this.preImpressionWorkflow         = (Workflow)this.beanFactory.getBean("Workflow");
        this.geoDataLoaderExecutor         = (DataLoaderExecutor)this.beanFactory.getBean("geo_data_loader");
        this.ispMappingsLoader             = (IspMappingsLoader)this.beanFactory.getBean("isp_mappings_loader");
        this.countryDetectionCache         = (CountryDetectionCache)this.beanFactory.getBean("country_detector");
        this.ispDetectionCache             = (ISPDetectionCache)this.beanFactory.getBean("isp_detector");
        this.handsetPopulationProvider     = (HandsetPopulationProvider) this.beanFactory.getBean("handset_data_populator");
        this.customIPFileDetectionCache    = (CustomIPFileDetectionCache)this.beanFactory.getBean("custom_ip_detector");
        this.creativeSlotSizeCache         = (CreativeSlotSizeCache)this.beanFactory.getBean("creative_slot_size_cache");
        this.zipCodeDetectionCache         = (ZipCodeDetectionCache)this.beanFactory.getBean("zip_code_detector");
        this.zipCodeFileDataCache          = (ZipCodeFileDataCache)this.beanFactory.getBean("zip_code_file_cache");
        this.externalSupplyAttributesCache = (ExternalSupplyAttributesCache)this.beanFactory.getBean("external_supply_attr_cache");
        this.siteMetaDataLoader            = (SiteMetaDataLoader)this.beanFactory.getBean("site_meta_data_loader");
        this.kexecutor                     = KExecutor.getKExecutor("adserving.application");

        try
        {
            this.stateDetectionCache       = (StateDetectionCache) this.beanFactory.getBean("state_detector");
            this.cityDetectionCache        = (CityDetectionCache) this.beanFactory.getBean("city_detector");
        }
        catch (Exception e)
        {
            System.out.println("Exception in fetching state ,city beans,must not be defined, no error " +
                                e.getMessage());
        }

        servletContext.setAttribute("workflow",this.preImpressionWorkflow);

        System.out.println("AdServing Application context initialized .... ");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent)
    {
        System.out.println("Calling destroy of PreImpressionContextListener...");

        this.preImpressionWorkflow.destroy();
        if(null != this.geoDataLoaderExecutor)
            this.geoDataLoaderExecutor.releaseResources();
        if(null != this.ispMappingsLoader)
            this.ispMappingsLoader.releaseResources();
        if(null != this.countryDetectionCache)
            this.countryDetectionCache.releaseResources();
        if(null != this.ispDetectionCache)
            this.ispDetectionCache.releaseResources();
        if(null != this.stateDetectionCache)
            this.stateDetectionCache.releaseResources();
        if(null != this.cityDetectionCache)
            this.cityDetectionCache.releaseResources();
        if(null != this.handsetPopulationProvider)
            this.handsetPopulationProvider.releaseResources();
        if(null != this.customIPFileDetectionCache)
            this.customIPFileDetectionCache.releaseResources();
        if(null != this.creativeSlotSizeCache)
            this.creativeSlotSizeCache.releaseResources();
        if(null != this.zipCodeDetectionCache)
            this.zipCodeDetectionCache.releaseResources();
        if(null != this.zipCodeFileDataCache)
            this.zipCodeFileDataCache.releaseResources();
        if(null != this.externalSupplyAttributesCache)
            this.externalSupplyAttributesCache.releaseResources();
        if(null != siteMetaDataLoader)
            this.siteMetaDataLoader.releaseResources();
        if(null != this.kexecutor){
            this.kexecutor.shutdown();
        }
        try
        {
            AbandonedConnectionCleanupThread.shutdown();
        }
        catch (InterruptedException e)
        {
            System.out.println("InterruptedException inside PreImpressionServlet in shutting " +
                               "down AbandonedConnectionCleanupThread");
        }

        System.out.println("Done Calling destroy of PreImpressionContextListener...");
    }
}