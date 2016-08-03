package com.kritter.geo.common;

import lombok.Getter;

/**
 * This interface dictates methods to be used in reading any third party
 * database (from file or sql database), make sense out of it and feed
 * entity information into sql database first and then write to CSV
 * files used for detection purposes.
 */
public interface ThirdPartyDataLoader
{

    public enum DATA_LOADER_TYPE
    {
        COUNTRY_ISP_DATA(1,"geo country and isp third party data"),
        COUNTRY_DATA(2,"geo country third party data"),
        ISP_DATA(3,"geo operator/isp third party data"),
        STATE_CITY_DATA(4,"geo state and city third party data"),
        ORG_DATA(5,"geo operator/brand third party data"),
        STATE_DATA(6,"geo state third party data"),
        CITY_DATA(7,"geo city third party data");

        @Getter
        private int geoDataType;
        @Getter
        private String description;

        private  DATA_LOADER_TYPE(int geoDataType,String description)
        {
            this.geoDataType = geoDataType;
            this.description = description;
        }
    }

    /**
     * This function determines what type of data loader the implementing
     * class is.For now country and isp are supported.
     * @return
     */
    public DATA_LOADER_TYPE getGeoDataLoaderType();

    /**
     * Gives the data source name of the third party database.
     * @return dataSourceName
     */
    public String getDataSourceName();

    /**
     * This method is responsible to read data from input files or relational
     * database , understand it as per the entities defined, entity could be
     * country,operator or any other like state,city,zip,lat-long.
     * After understanding it, the entities are fed into mysql database
     * and into CSV files along with ipranges for internal usage.
     *
     * This method also schedules the input database reading and writing to
     * sql database and CSV files.
     * @throws Exception
     */
    public void scheduleInputDatabaseConversionAndPopulationForInternalUsage() throws Exception;

    /**
     * This function releases any resources associated with the data loader like cancelling
     * the timer task,etc.
     */
    public void releaseResources();

}
