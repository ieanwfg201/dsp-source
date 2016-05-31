package com.kritter.kumbaya.countdistinct.kritter_user_counts.common;

import org.apache.commons.codec.binary.Base64;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kritter.adserving.thrift.struct.AdservingRequestResponse;

public class AdservingThriftLogConverter {
    private static final Logger LOG = LoggerFactory.getLogger(AdservingThriftLogConverter.class);
    
    /**
     *  Convert logline to AdservingRequestResponse object
     */
    public static AdservingRequestResponse convert(String logline) throws Exception {
        if(logline == null){
            return null;
        }
        try{
            TDeserializer tde =  new TDeserializer(new TBinaryProtocol.Factory());
            Base64 decoder = new Base64(0);
            AdservingRequestResponse convertedLog = new AdservingRequestResponse();
            tde.deserialize(convertedLog, decoder.decodeBase64(logline.getBytes()));
            return convertedLog;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            return null;
        }

    }

}
