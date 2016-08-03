package com.kritter.common.site.cache;

import com.kritter.common.site.entity.NoFillPassbackContent;
import com.kritter.common.site.entity.SiteMetaDataEntity;
import com.kritter.constants.SITE_PASSBACK_CONTENT_TYPE;
import com.kritter.utils.common.ServerConfig;
import com.kritter.utils.databasemanager.DBExecutionUtils;
import com.kritter.utils.databasemanager.DatabaseManager;
import com.kritter.utils.http_client.SynchronousHttpClient;
import com.kritter.utils.http_client.entity.HttpRequest;
import com.kritter.utils.http_client.entity.HttpResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class is used for loading site's metadata to common mysql
 * database, this class would work only on master node. On slave
 * nodes it would be initialized however would not function.
 *
 * Sample schema for site_metadata in mysql:
 *   CREATE TABLE site_metadata
     (
     id INTEGER PRIMARY KEY,
     site_guid VARCHAR(100) UNIQUE NOT NULL,
     passback_url VARCHAR(1024) DEFAULT NULL,
     response_content_type VARCHAR(50) DEFAULT NULL,
     response_content BLOB DEFAULT NULL,
     last_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
     )ENGINE=INNODB CHARSET=latin1;
 */

public class SiteMetaDataLoader
{
    private Logger logger;
    private SynchronousHttpClient synchronousHttpClient;
    private int connectTimeout;
    private int readTimeout;
    private long reloadFrequency;
    private Timer timer;
    private TimerTask timerTask;
    private DatabaseManager databaseManager;
    private SiteMetaDataCache siteMetaDataCache;
    //This variable tells whether this machine's/node's application
    //should load data into database or it is just a slave node.
    private boolean dataLoadMasterNode;
    private static final String URL_DATA_INSERT_QUERY = "insert into site_metadata (site_guid,passback_url," +
                                                        "response_content_type,response_content) values (?,?,?,?)";

    private static final String URL_DATA_UPDATE_QUERY = "update site_metadata set passback_url = ?," +
                                                        "response_content_type = ?,response_content = ? where " +
                                                        "site_guid = ?";

    private static final String SITE_PASSBACK_DATA_FETCH_QUERY = "select guid,nofill_backup_content from site where status_id = 1";

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final TypeReference<NoFillPassbackContent[]>
            typeReferenceForPassbackContent= new TypeReference<NoFillPassbackContent[]>(){};

    public SiteMetaDataLoader(
                              String loggerName,
                              long reloadFrequency,
                              String dataLoadMasterNodeParam,
                              ServerConfig serverConfig,
                              DatabaseManager databaseManager,
                              SiteMetaDataCache siteMetaDataCache,
                              SynchronousHttpClient synchronousHttpClient,
                              int connectTimeout,
                              int readTimeout
                             ) throws Exception
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.reloadFrequency = reloadFrequency;
        this.dataLoadMasterNode = Boolean.valueOf(serverConfig.getValueForKey(dataLoadMasterNodeParam));
        this.databaseManager = databaseManager;
        this.siteMetaDataCache = siteMetaDataCache;
        this.synchronousHttpClient = synchronousHttpClient;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;

        if(this.dataLoadMasterNode)
            scheduleSiteMetaDataLoadingTask();
    }

    private void prepareDatabase() throws Exception
    {
        PreparedStatement pstmt = null;
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtUpdate = null;
        Connection connection = fetchConnectionToDatabase();

        Set<SiteMetaDataEntity> siteMetaDataEntitySet = new HashSet<SiteMetaDataEntity>();

        logger.debug("Inside prepareDatabase() of SiteMetaDataLoader ");
        try
        {
            /*fetch all site's passback data from mysql and use their urls if present to
              fetch third party ad payloads.*/
            pstmt = connection.prepareStatement(SITE_PASSBACK_DATA_FETCH_QUERY);
            pstmtInsert = connection.prepareStatement(URL_DATA_INSERT_QUERY);
            pstmtUpdate = connection.prepareStatement(URL_DATA_UPDATE_QUERY);

            ResultSet rs = pstmt.executeQuery(SITE_PASSBACK_DATA_FETCH_QUERY);

            while(rs.next())
            {
                String noFillBackupContent = rs.getString("nofill_backup_content");
                String siteGuid = rs.getString("guid");

                NoFillPassbackContent[] noFillPassbackContentArray = convertFromJsonToNoFillPassbackArray(noFillBackupContent);

                if(null != noFillPassbackContentArray && noFillPassbackContentArray.length > 0)
                {
                    for(NoFillPassbackContent entry : noFillPassbackContentArray)
                    {
                        /*if the content type is a url then fetch content from it and store*/
                        if(entry.getPassBackContentType() == SITE_PASSBACK_CONTENT_TYPE.PASSBACK_URL.getCode())
                        {
                            String passbackUrl = entry.getPassBackContent();

                            try
                            {
                                URL url = new URL(passbackUrl);
                            }
                            catch (Exception e)
                            {
                                logger.error("Exception in resolving the url {} , skipping passback url content fetch for site id :{}",passbackUrl,siteGuid);
                            }

                            HttpRequest httpRequest = new HttpRequest(
                                                                      passbackUrl,
                                                                      connectTimeout,
                                                                      readTimeout,
                                                                      HttpRequest.REQUEST_METHOD.GET_METHOD,
                                                                      null,
                                                                      null
                                                                     );

                            HttpResponse httpResponse = synchronousHttpClient.fetchResponseFromThirdPartyServer(httpRequest);

                            logger.debug("HttpResponse obtained for url: {} , is not null? {}, status being: {} ",
                                         passbackUrl,(null == httpResponse),httpResponse.getResponseStatusCode());

                            if(null != httpResponse)
                            {
                                if(httpResponse.getResponseStatusCode() == 200)
                                {
                                    String responsePayload = httpResponse.getResponsePayload();

                                    if(null != responsePayload)
                                    {
                                        SiteMetaDataEntity siteMetaDataEntity =

                                                new SiteMetaDataEntity(
                                                                       siteGuid,
                                                                       passbackUrl,
                                                                       httpResponse.getResponseContentType(),
                                                                       responsePayload,
                                                                       System.currentTimeMillis()
                                                                      );

                                        siteMetaDataEntitySet.add(siteMetaDataEntity);
                                    }

                                }
                            }
                        }
                    }
                }
            }

            /*If there are site metadata entries fetched in this go,
              then use them and insert or update into site_metadata table*/
            for(SiteMetaDataEntity siteMetaDataEntity : siteMetaDataEntitySet)
            {
                //update
                if(null != siteMetaDataCache.query(siteMetaDataEntity.getSiteGuid()))
                {
                    pstmtUpdate.setString(1,siteMetaDataEntity.getPassbackURL());
                    pstmtUpdate.setString(2,siteMetaDataEntity.getResponseContentType());
                    pstmtUpdate.setString(3,siteMetaDataEntity.getResponseContent());
                    pstmtUpdate.setString(4,siteMetaDataEntity.getSiteGuid());
                    pstmtUpdate.executeUpdate();
                }
                //insert
                else
                {
                    pstmtInsert.setString(1,siteMetaDataEntity.getSiteGuid());
                    pstmtInsert.setString(2,siteMetaDataEntity.getPassbackURL());
                    pstmtInsert.setString(3,siteMetaDataEntity.getResponseContentType());
                    pstmtInsert.setString(4,siteMetaDataEntity.getResponseContent());
                    pstmtInsert.executeUpdate();
                }
            }

        }
        catch (SQLException sqle)
        {
            throw new Exception("Exception inside prepareDatabase() of SiteMetaDataLoader.", sqle);
        }
        finally
        {
            if(null != pstmt)
                pstmt.close();
            if(null != pstmtInsert)
                pstmtInsert.close();
            if(null != pstmtUpdate)
                pstmtUpdate.close();

            DBExecutionUtils.closeConnection(connection);
        }
    }

    public NoFillPassbackContent[] convertFromJsonToNoFillPassbackArray(String noFillPassbackContent)
    {
        NoFillPassbackContent[] nofillBackupContentArray = null;

        if(null != noFillPassbackContent)
        {
            try
            {
                nofillBackupContentArray =
                        objectMapper.readValue(noFillPassbackContent,typeReferenceForPassbackContent);
            }
            catch (IOException ioe)
            {
            }
        }

        return nofillBackupContentArray;
    }

    private Connection fetchConnectionToDatabase() throws Exception
    {
        return this.databaseManager.getConnectionFromPool();
    }

    public void scheduleSiteMetaDataLoadingTask() throws Exception
    {
        this.timer = new Timer();
        this.timerTask = new SiteMetaDataLoadingTask();
        this.timer.schedule(this.timerTask,reloadFrequency,reloadFrequency);
    }

    public void releaseResources()
    {
        if(null != this.timerTask && null != this.timer)
        {
            this.timerTask.cancel();
            this.timer.cancel();
            this.timer.purge();
        }
    }

    /**
     * This class is responsible for looking up for any updates in country data file.
     */
    private class SiteMetaDataLoadingTask extends TimerTask
    {
        private Logger cacheLogger = LoggerFactory.getLogger("cache.logger");

        @Override
        public void run()
        {
            try
            {
                prepareDatabase();
            }
            catch (Exception e)
            {
                cacheLogger.error("Exception while loading site meta data inside SiteMetaDataLoadingTask inside SiteMetaDataLoader",e);
            }
        }
    }
}
