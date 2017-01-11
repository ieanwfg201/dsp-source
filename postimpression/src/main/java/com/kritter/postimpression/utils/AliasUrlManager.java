package com.kritter.postimpression.utils;

import com.kritter.postimpression.cache.AliasUrlCache;
import com.kritter.postimpression.cache.entity.UrlAliasEntity;
import com.kritter.postimpression.cache.index.UrlAliasSecondaryIndex;
import com.kritter.utils.dbconnector.DBConnector;
import com.kritter.utils.uuid.rand.UUIDGenerator;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 * This class takes care of creating a new alias url for a given url or
 * returns alias url if already present for the given url.
 *
 * Alias url will be like:
 *
 * http://techhinge.com/tclick/uuid-value.clk
 */
public class AliasUrlManager {

    private Properties dbProperties;
    private String dbPropertiesFile;
    private String aliasUrlBaseUrl;

    private static final String SELECT_QUERY = "select * from url_alias where status = true and actual_url = ";
    private static final String INSERT_QUERY = "insert into url_alias (actual_url,alias_url,status,last_modified,created_on) values ";
    private static final String SELECT_QUERY_THIRD_PARTY_PUBLISHERS = "select * from third_party_publishers where status = true";

    private static final String REQUEST_PARAMS_URI_DELIMITER = "?";
    private static final String REQUEST_PARAM_DELIMITER = "&";
    private static final String LOCATION_HEADER = "Location";
    private UUIDGenerator uuidGenerator;

    public AliasUrlManager(String dbPropertiesFile,String aliasUrlBaseUrl) throws IOException{

        this.dbPropertiesFile = dbPropertiesFile;
        dbProperties = new Properties();
        dbProperties.load(new FileInputStream(new File(this.dbPropertiesFile)));
        this.aliasUrlBaseUrl = aliasUrlBaseUrl;
        this.uuidGenerator = new UUIDGenerator();
    }


    /**
     * This function takes input as a url and returns alias for it.
     * This is used for hiding the actual url from outside, the alias url
     * when hit to server performs lookup in db and then redirects to the
     * actual url (the last in the chain upon a series of re-directions.)
     *
     *
     */
    public String fetchOrCreateAliasURLForURL(String url) throws Exception{

        if(null==url)
            throw new Exception("URL supplied is null inside PostImpressionUtils, alias url can not be created.");

        //will throw malformed url exception if url not correct.
        URL suppliedUrl = new URL(url);

        Connection conn = DBConnector.getConnection(this.dbProperties);

        Statement stmt = null;
        String aliasUrl = null;
        ResultSet rs = null;

        try{

            StringBuffer sb = new StringBuffer();
            sb.append(SELECT_QUERY);
            sb.append("'");
            sb.append(url);
            sb.append("'");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sb.toString());

            while(rs.next()){

                aliasUrl = rs.getString("alias_url");

                StringBuffer urlSb = new StringBuffer();
                urlSb.append(this.aliasUrlBaseUrl);
                urlSb.append(aliasUrl);
                urlSb.append(".clk");
                return urlSb.toString();

            }

            //if not found then insert new value and return.
            String uuidValueForAliasUrl = this.uuidGenerator.generateUniversallyUniqueIdentifier().toString();
            StringBuffer urlSb = new StringBuffer();
            urlSb.append(this.aliasUrlBaseUrl);
            urlSb.append(uuidValueForAliasUrl);
            urlSb.append(".clk");

            stmt = conn.createStatement();
            sb = new StringBuffer();
            sb.append(INSERT_QUERY);
            sb.append("('");
            sb.append(url);
            sb.append("','");
            sb.append(uuidValueForAliasUrl);
            sb.append("',true,now(),now())");
            int result = stmt.executeUpdate(sb.toString());

            if(result!=1)
                throw new SQLException("Insertion failed inside AliasUrlManager to create new entry for url " + url);
            else
                return urlSb.toString();


        }catch(SQLException sqle){
            //TODO log the error., send message to try again.
            aliasUrl = "Something went wrong, try again to access the url." + sqle.getMessage();
        }


        finally {

            try{

            if(null!=rs)
                rs.close();
            if(null!=stmt)
                stmt.close();
            if(null!=conn)
                conn.close();
            }

            catch(SQLException sqle){
                rs = null;
                stmt = null;
                conn = null;
            }

        }

        return aliasUrl;
    }

    /**
     * This function looks up alias url into cache and redirects user to actual url.
     */
    public static void lookUpActualUrlAndRedirectUser(Logger logger,String aliasUrl,
                                                      String queryStringToAppend,HttpServletResponse response,
                                                      AliasUrlCache aliasUrlCache) throws Exception{

        if(null==aliasUrl)
            throw new Exception("The alias url is null, error inside AliasUrlManager");

        PostImpressionUtils.logDebug(logger,"Looking up alias url [",
                aliasUrl ,"]for actual url to perform redirection.");

        Set<Integer> urlAliasEntityIds = aliasUrlCache.query(new UrlAliasSecondaryIndex(aliasUrl));

        PostImpressionUtils.logDebug(logger,
                "The primary ids retrieved for alias url : ", urlAliasEntityIds.toString());
        UrlAliasEntity entity = null;

        Iterator<Integer> it = urlAliasEntityIds.iterator();

        //only one entity is expected.
        while(it.hasNext()){

            entity = aliasUrlCache.query(it.next());
            break;

        }

        if(null==entity)
            throw new Exception("Url Alias Entity could not be found in store inside AliasUrlManager");

        StringBuffer actualUrl = new StringBuffer(entity.getActualUrl());


        if(null!=queryStringToAppend){

            if(actualUrl.toString().contains(REQUEST_PARAMS_URI_DELIMITER)){

                actualUrl.append(REQUEST_PARAM_DELIMITER);
                actualUrl.append(queryStringToAppend);

            }
            else{

                actualUrl.append(REQUEST_PARAMS_URI_DELIMITER);
                actualUrl.append(queryStringToAppend);

            }

        }

        PostImpressionUtils.logDebug(logger,"User Redirected to actual url : " , actualUrl.toString());
        response.setHeader(LOCATION_HEADER,actualUrl.toString());
        response.setStatus(HttpServletResponse.SC_FOUND);

    }

    public String fetchAffiliatePublisherInfo() throws Exception{

        Connection conn = DBConnector.getConnection(this.dbProperties);

        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer sb = new StringBuffer();

        try{

            stmt = conn.createStatement();
            rs = stmt.executeQuery(SELECT_QUERY_THIRD_PARTY_PUBLISHERS);

            while(rs.next()){
                sb.append("zone_id: [");
                sb.append(rs.getInt("third_party_pub_id"));
                sb.append("], ");
                sb.append("publisher-name: [");
                sb.append(rs.getString("third_party_pub_name"));
                sb.append("], ");
                sb.append("publisher-description: [");
                sb.append(rs.getString("third_party_pub_desc"));
                sb.append("], ");
                sb.append("status: [");
                sb.append(rs.getBoolean("status"));
                sb.append("]<br>");

            }

        }catch(SQLException sqle){
            //TODO log the error., send message to try again.
            throw new Exception("SQLException inside AliasUrlManager in getting third party publishers");
        }


        finally {

            try{

                if(null!=rs)
                    rs.close();
                if(null!=stmt)
                    stmt.close();
                if(null!=conn)
                    conn.close();
            }

            catch(SQLException sqle){
                rs = null;
                stmt = null;
                conn = null;
            }

        }

        return sb.toString();
    }

}
