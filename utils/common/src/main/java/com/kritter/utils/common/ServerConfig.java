package com.kritter.utils.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * This class contains config parameters that are required at a server level.
 * Like prefixes of cdn url, click url, csc url.
 */
public class ServerConfig
{
    public static final String CDN_URL_PREFIX = "cdn-url-prefix";
    public static final String CLICK_URL_PREFIX = "click-url-prefix";
    public static final String CSC_URL_PREFIX = "csc-url-prefix";
    public static final String WIN_URL_PREFIX = "win-url-prefix";
    public static final String WIN_API_URL_PREFIX = "win-api-url-prefix";
    public static final String EXCHANGE_INTERNAL_WIN_API_URL_PREFIX = "exchange-internal-win-url-prefix";
    public static final String MACRO_CLICK_URL_PREFIX = "macro-click-url-prefix";
    public static final String trackingEventUrl_PREFIX = "tracking-event-url-prefix";
    public static final String beventUrl_PREFIX = "bevent-url-prefix";
    public Boolean isHttps = false;

    public static final String DELIMITER = String.valueOf((char)1);
    public static final String DELIMITER_EQUAL_TO = "=";

    private Map<String,String> configMap;

    public ServerConfig(String serverPropertiesFilePath) throws Exception
    {
        if(null == serverPropertiesFilePath)
            throw new Exception("ServerPropertiesFilePath is null, cannot initialize application...");

        this.configMap = new HashMap<String, String>();
        readPropertiesFileAndPopulateConfiguration(serverPropertiesFilePath);
    }

    private void readPropertiesFileAndPopulateConfiguration(String serverPropertiesFilePath) throws Exception
    {

        BufferedReader br = null;
        FileReader fr = null;
        File file = null;

        try
        {
            file = new File(serverPropertiesFilePath);
            fr = new FileReader(file);
            br = new BufferedReader(fr);

            String line = null;

            while(null != (line = (br.readLine())))
            {
                if(line.contains(DELIMITER))
                {
                    String parts[] = line.split(DELIMITER);

                    if(null == parts || parts.length != 2)
                        throw new Exception("The line in server config file is corrupt..." + line +
                                " , file path being " + serverPropertiesFilePath);

                    configMap.put(parts[0],parts[1]);
                }
                else if(line.contains(DELIMITER_EQUAL_TO) && !line.contains(DELIMITER))
                {
                    String parts[] = line.split(DELIMITER_EQUAL_TO);
                    if(null == parts || parts.length < 1)
                        throw new Exception("The line in server config file is corrupt..." + line +
                                " , file path being " + serverPropertiesFilePath);

                    StringBuffer sb = new StringBuffer();

                    for(int i=1;i<parts.length;i++)
                    {
                        sb.append(parts[i]);
                        sb.append(DELIMITER_EQUAL_TO);
                    }

                    sb.deleteCharAt(sb.length()-1);

                    configMap.put(parts[0],sb.toString());
                }
                else
                    throw new Exception("The line in server config file is corrupt... " + line +
                            " , file path being" + serverPropertiesFilePath);
            }

            isHttps = Boolean.valueOf(getValueForKey("tracking_https_enabled"));
            if(isHttps == true)
                for(Map.Entry<String,String> entry:configMap.entrySet()){
                    if(entry.getValue().indexOf("http:") != -1){
                        String value = entry.getValue().replaceAll("http:","https:");
                        configMap.put(entry.getKey(),value);
                    }
                }
        }
        finally
        {
            if(null != fr)
                fr.close();
            if(null != br)
                br.close();
        }
    }

    public String getValueForKey(String key)
    {
        if(null != configMap)
            return this.configMap.get(key);

        return null;
    }

    public String getValueForKey(String key,int secure)
    {
        if(null != configMap)
        {
            if(secure == 1){
                for(Map.Entry<String,String> entry:configMap.entrySet()){
                    if(entry.getValue().indexOf("http:") != -1){
                        String value = entry.getValue().replaceAll("http:","https:");
                        configMap.put(entry.getKey(),value);
                    }
                }
                return this.configMap.get(key);
            }else{
                return this.configMap.get(key);
            }

        }
        return null;
    }
}