package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.EvalFunc;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConvertTimeWindowInCsv extends EvalFunc<String> {
    private static final Logger LOG = LoggerFactory.getLogger(ConvertTimeWindowInCsv.class);
    public String exec(Tuple input) throws IOException {
        String defaultRet = "";
        try {
            if(input != null && input.size()==6 && input.get(0)!=null){
                defaultRet = input.get(0).toString();
                if(input.get(1) != null && input.get(2) != null && input.get(3)!= null && input.get(4) != null && input.get(5) != null){
                    int position = Integer.parseInt(input.get(1).toString());
                    String str = input.get(2).toString();
                    String delimiter = input.get(3).toString();
                    String prefixDate = input.get(4).toString();
                    String conversionKey = input.get(5).toString();
                    String strSplit[] = str.split(delimiter);
                    if(position < strSplit.length){
                        int window = Integer.parseInt(strSplit[position]);
                        if("H48".equals(conversionKey)){
                            int belongsto = window/2;
                            if(belongsto < 10){
                                strSplit[position] = prefixDate+ " 0"+belongsto+":00:00";
                            }else{
                                strSplit[position] = prefixDate+ " "+belongsto+":00:00" ;
                            }
                        }
                        boolean first = true;
                        StringBuffer sbuffer = new StringBuffer("");
                        for(String element:strSplit){
                            if(first){
                                first = false;
                            }else{
                                sbuffer.append(delimiter);
                            }
                                sbuffer.append(element);
                        }
                        return sbuffer.toString();
                    }
                }
            }
            return defaultRet;
        }catch (ExecException e) {
            LOG.error(e.getMessage());
            return defaultRet;
        }
    }
}
