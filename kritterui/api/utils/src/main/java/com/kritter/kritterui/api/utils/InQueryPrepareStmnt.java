package com.kritter.kritterui.api.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InQueryPrepareStmnt {
    private static final Logger LOG = LoggerFactory.getLogger(InQueryPrepareStmnt.class);
    
    public static String  createInQueryPrepareStatement(String query, String replacementRegex, 
            String inString, String inDelim, boolean quote){
        if(query != null && replacementRegex != null && inString != null && inDelim != null ){
            StringBuffer sbuff = new StringBuffer("");
            String[] inStringSplit = inString.split(inDelim);
            boolean isFirst = true;
            for(String inStr:inStringSplit){
                if(isFirst){
                    isFirst=false;
                }else{
                    sbuff.append(",");    
                }
                if(quote){
                    sbuff.append("'");
                }
                sbuff.append(inStr);
                if(quote){
                    sbuff.append("'");
                }
            }
            query = query.replaceAll(replacementRegex, sbuff.toString());
            //System.out.println(query);
        }
        return query;
    }
}
