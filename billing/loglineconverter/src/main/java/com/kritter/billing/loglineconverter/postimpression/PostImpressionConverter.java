package com.kritter.billing.loglineconverter.postimpression;

import org.apache.commons.codec.binary.Base64;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.billing.loglineconverter.ILogLineConverter;
import com.kritter.postimpression.thrift.struct.PostImpressionRequestResponse;

public class PostImpressionConverter implements ILogLineConverter<PostImpressionRequestResponse> {
    private static final Logger LOG = LoggerFactory.getLogger(PostImpressionConverter.class);
    
    /**
     *  Convert logline to PostImpressionRequestResponse object
     */
    public PostImpressionRequestResponse convert(String logline) throws Exception {
        if(logline == null){
            return null;
        }
        try{
            TDeserializer tde =  new TDeserializer(new TBinaryProtocol.Factory());
            Base64 decoder = new Base64(0);
            PostImpressionRequestResponse pirr = new PostImpressionRequestResponse();
            tde.deserialize(pirr, decoder.decodeBase64(logline.getBytes()));
            return pirr;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            return null;
        }

    }


}
