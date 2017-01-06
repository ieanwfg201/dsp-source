package com.kritter.postimpression.urlreader.impl;

import com.kritter.core.workflow.Context;
import com.kritter.entity.postimpression.entity.Request;
import com.kritter.postimpression.urlreader.PostImpressionEventUrlReader;
import com.kritter.postimpression.utils.PostImpressionUtils;
import com.kritter.utils.common.ApplicationGeneralUtils;
import com.kritter.utils.dbconnector.DBConnector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * This class reads our own tracking url given to third party
 * which upon some event calls the url.
 * The event could be download, click, install or some other special event.
 *
 * Party could be an advertiser or publisher.
 * The url will be:
 * http://techhinge.com/url-type/event-id/third-party-id/md5Hash?params.
 *
 * Here url-type would be trk, event-id could be
 * d - download, c-click
 *
 * As such uri does not contain any demand specific data, demand specific
 * data resides in params.
 */

public class TrackingUrlFromThirdPartyReader implements PostImpressionEventUrlReader{

    private String name;
    private String md5SecretKeyForHash;
    private String postImpressionBaseUrl;
    private Properties dbProperties;
    private String dbPropertiesFile;
    private String trackingUrlSelectQuery;
    private PostImpressionUtils postImpressionUtils;

    public TrackingUrlFromThirdPartyReader(
                                           String name, String md5SecretKeyForHash,
                                           String postImpressionBaseUrl,String dbPropertiesFile,
                                           String trackingUrlSelectQuery,PostImpressionUtils postImpressionUtils
                                          )throws IOException{

        this.name = name;
        this.md5SecretKeyForHash = md5SecretKeyForHash;
        this.postImpressionBaseUrl = postImpressionBaseUrl;
        this.dbPropertiesFile = dbPropertiesFile;
        this.dbProperties = new Properties();
        this.dbProperties.load(new FileInputStream(new File(this.dbPropertiesFile)));
        this.trackingUrlSelectQuery = trackingUrlSelectQuery;
        this.postImpressionUtils = postImpressionUtils;
    }


    public enum TRACKING_URL_EVENTS{

        CLICK("c", "Third party has seen click on demand."),
        DOWNLOAD("d","Third party has seen download on demand."),
        INSTALL("i", "Third party has seen installation/transaction for the demand.");

        private String eventId;
        private String description;

        private TRACKING_URL_EVENTS(String eventId,String description){

            this.eventId = eventId;
            this.description = description;

        }

        public String getEventId(){
            return this.eventId;
        }

        public String getDescription(){
            return this.description;
        }

    };

    @Override
    public String getName() {
        return this.name;
    }


    @Override
    public void decipherPostImpressionUrl(Request postImpressionRequest, String requestURI,
                                             Context context) throws Exception {
        if(null == postImpressionRequest || null== requestURI)
            throw new Exception("The supplied request object is null inside decipherPostImpressionUrl() of " +
                    "TrackingUrlFromThirdPartyReader.");

        String requestURIParts[] = requestURI.split(this.postImpressionUtils.getUriFieldsDelimiter());

        if(null == requestURIParts || requestURIParts.length != 5)
            throw new Exception("Inside decipherPostImpressionUrl() of TrackingUrlFromThirdPartyReader, " +
                    "URI is malformed");

        try{

            String eventId = requestURIParts[2];
            String thirdPartyId = requestURIParts[3];
            String md5HashValue = requestURIParts[4];

            StringBuffer dataInHash = new StringBuffer();
            dataInHash.append(eventId);
            dataInHash.append(thirdPartyId);

            String hashValueForUrl = ApplicationGeneralUtils.calculateHashForData(dataInHash.toString(),
                    this.md5SecretKeyForHash);

            if(!md5HashValue.equals(hashValueForUrl))
                throw new Exception("The requested tracking url is not valid, the hash match failed. => " + requestURI);

            postImpressionRequest.setThirdPartyTrackingUrlEventId(eventId);
            postImpressionRequest.setThirdPartyId(thirdPartyId);
            postImpressionRequest.setThirdPartyUrlHash(md5HashValue);

        }catch (RuntimeException e){
            throw new Exception("RuntimeException inside decipherPostImpressionUrl() of " +
                    "TrackingUrlFromThirdPartyReader", e);
        }
    }

    /**
     * This method returns third party tracking urls available, otherwise,
     * creates new urls and returns.
     *
     */
    public String fetchTrackingUrlsForThirdParties() throws Exception
    {
        Connection conn = DBConnector.getConnection(this.dbProperties);

        StringBuffer result = new StringBuffer();

        try
        {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(this.trackingUrlSelectQuery);

            while(rs.next())
            {
                int thirdPartyId = rs.getInt("third_party_id");
                String thirdPartyName = rs.getString("third_party_name");
                String thirdPartyTrackingUri = rs.getString("tracking_uri");
                String thirdPartyTrackingUriDescription = rs.getString("tracking_uri_description");
                boolean status = rs.getBoolean("status");
                Timestamp lastModified = rs.getTimestamp("last_modified");
                Timestamp createdOn = rs.getTimestamp("created_on");

                result.append("Third Party Id[");
                result.append(thirdPartyId);
                result.append("], ThirdPartyName[");
                result.append(thirdPartyName);
                result.append("], TrackingUrl[");
                result.append(this.postImpressionBaseUrl);
                result.append(thirdPartyTrackingUri);
                result.append("], Description[");
                result.append(thirdPartyTrackingUriDescription);
                result.append("], Status[");
                result.append(status);
                result.append("],[");
                result.append(lastModified);
                result.append("],[");
                result.append(createdOn);
                result.append("]<br>");

            }
        }
        catch(SQLException sqle )
        {
            result = new StringBuffer();
            result.append("DATABASE ERROR");
            //log the error
            return result.toString();
        }

        return result.toString();
    }

}



