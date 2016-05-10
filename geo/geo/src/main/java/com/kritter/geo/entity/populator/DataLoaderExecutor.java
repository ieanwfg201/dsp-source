package com.kritter.geo.entity.populator;

import com.kritter.geo.common.ThirdPartyDataLoader;
import com.kritter.utils.common.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * This class is responsible to execute all the third party data loaders
 * available to it, the result is preparation of a database from which
 * we can expose country,operator,city,etc. targeting on user interface
 * along with form data for detection via CSV files.
 *
 * This class uses reflection to create instances of the data loader.
 * The data loader individually could be loading from filesystem or
 * from some relational database, these details are part of the
 * implementations.
 */
public class DataLoaderExecutor
{
    private Logger logger;
    private ThirdPartyDataLoader[] thirdPartyDataLoaderInstances;

    //This variable tells whether this machine's/node's application
    //should load data into database or it is just a slave node.
    private boolean dataLoadMasterNode;

    /**
     * The constructor here should at least once perform the whole data
     * loading activity and hence schedule the task looking for any future
     * updates.
     * @param loggerName
     * @param thirdPartyDataLoaders
     */
    public DataLoaderExecutor(String loggerName,
                              ThirdPartyDataLoader[] thirdPartyDataLoaders,
                              String dataLoadMasterNodeParam,
                              ServerConfig serverConfig
                             ) throws Exception
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.thirdPartyDataLoaderInstances = thirdPartyDataLoaders;
        this.dataLoadMasterNode = Boolean.valueOf(serverConfig.getValueForKey(dataLoadMasterNodeParam));

        //important message so logging in error mode.
        logger.error("The data loader executor on this node/machine/dsp-application is parent? " +
                     dataLoadMasterNode + " will be loading data from this node if true.");

        if(dataLoadMasterNode)
            executeThirdPartyGeoDataLoaders();
    }

    /**
     * Execute third party data loaders in sequence:
     *
     * IMPORTANT: country first, then isp, then others
     * if supported like state then city then zipcode.
     *
     * @throws Exception
     */
    private void executeThirdPartyGeoDataLoaders() throws Exception
    {
        logger.info("Going to execute third party data loaders.");

        try
        {
            //first run country and isp data loaders.
            for(ThirdPartyDataLoader thirdPartyDataLoader : thirdPartyDataLoaderInstances)
            {
                int geoDataType = thirdPartyDataLoader.getGeoDataLoaderType().getGeoDataType();

                if(geoDataType == ThirdPartyDataLoader.DATA_LOADER_TYPE.COUNTRY_ISP_DATA.getGeoDataType())
                {
                    logger.debug("Going to run country + isp third party data loader for: {}", thirdPartyDataLoader.getDataSourceName());
                    thirdPartyDataLoader.scheduleInputDatabaseConversionAndPopulationForInternalUsage();
                }
            }


            //secondly run country data loaders.
            for(ThirdPartyDataLoader thirdPartyDataLoader : thirdPartyDataLoaderInstances)
            {
                int geoDataType = thirdPartyDataLoader.getGeoDataLoaderType().getGeoDataType();

                if(geoDataType == ThirdPartyDataLoader.DATA_LOADER_TYPE.COUNTRY_DATA.getGeoDataType())
                {
                    logger.debug("Going to run country third party data loader for: {}", thirdPartyDataLoader.getDataSourceName());
                    thirdPartyDataLoader.scheduleInputDatabaseConversionAndPopulationForInternalUsage();
                }
            }

            //thirdly run isp data loaders.
            for(ThirdPartyDataLoader thirdPartyDataLoader : thirdPartyDataLoaderInstances)
            {
                int geoDataType = thirdPartyDataLoader.getGeoDataLoaderType().getGeoDataType();

                if(geoDataType == ThirdPartyDataLoader.DATA_LOADER_TYPE.ISP_DATA.getGeoDataType())
                {
                    logger.debug("Going to run isp third party data loader for: {}", thirdPartyDataLoader.getDataSourceName());
                    thirdPartyDataLoader.scheduleInputDatabaseConversionAndPopulationForInternalUsage();
                }
            }

            //then run organization data loaders. (as in maxmind,org data is separate than isp)
            for(ThirdPartyDataLoader thirdPartyDataLoader : thirdPartyDataLoaderInstances)
            {
                int geoDataType = thirdPartyDataLoader.getGeoDataLoaderType().getGeoDataType();

                if(geoDataType == ThirdPartyDataLoader.DATA_LOADER_TYPE.ORG_DATA.getGeoDataType())
                {
                    logger.debug("Going to run org third party data loader for: {}", thirdPartyDataLoader.getDataSourceName());
                    thirdPartyDataLoader.scheduleInputDatabaseConversionAndPopulationForInternalUsage();
                }
            }

            //lastly run state city data loaders.
            for(ThirdPartyDataLoader thirdPartyDataLoader : thirdPartyDataLoaderInstances)
            {
                int geoDataType = thirdPartyDataLoader.getGeoDataLoaderType().getGeoDataType();

                if(geoDataType == ThirdPartyDataLoader.DATA_LOADER_TYPE.STATE_CITY_DATA.getGeoDataType())
                {
                    logger.debug("Going to run state and city third party data loader for: {}",
                                 thirdPartyDataLoader.getDataSourceName());

                    thirdPartyDataLoader.scheduleInputDatabaseConversionAndPopulationForInternalUsage();
                }
            }
        }
        catch (IOException ioe)
        {
            logger.error("IOException inside DataLoaderExecutor of Geo module",ioe);
            throw new Exception("IOException inside DataLoaderExecutor of Geo module",ioe);
        }
        finally
        {

        }
    }

    /**
     * This function releases resources associated with all the data loaders.
     */
    public void releaseResources()
    {
       logger.debug("Inside releaseResources of DataLoaderExecutor, stopping all timers.");
       for(ThirdPartyDataLoader thirdPartyDataLoader : thirdPartyDataLoaderInstances)
       {
           thirdPartyDataLoader.releaseResources();
       }
    }
}
