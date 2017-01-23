package com.kritter.fanoutinfra.apiclient.ning;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.ning.http.client.AsyncHandler;
import com.ning.http.client.HttpResponseBodyPart;
import com.ning.http.client.HttpResponseHeaders;
import com.ning.http.client.HttpResponseStatus;

public class NingAsyncHandler implements AsyncHandler<String> {
    private Logger logger;
    @Getter
    private int status;
    private ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    NingAsyncHandler(String loggerName) {
        super();
        this.logger = LogManager.getLogger(loggerName);
    }

    @Override
    public STATE onStatusReceived(final HttpResponseStatus responseStatus) throws Exception {
        logger.debug("Inside onStatusReceived , status is: {} ",responseStatus.getStatusCode());
        status = responseStatus.getStatusCode();
        if(responseStatus.getStatusCode() == 200 ||responseStatus.getStatusCode() == 204){
            return STATE.CONTINUE;
        }else if(responseStatus.getStatusCode() == 204){
            return STATE.CONTINUE;
        }
        /**Changed on 18Jan2017, to capture errors from DSP we must continue.*/
        return STATE.CONTINUE;
    }

    @Override
    public STATE onHeadersReceived(final HttpResponseHeaders headers) throws Exception {
        return STATE.CONTINUE;
    }

    @Override
    public STATE onBodyPartReceived(final HttpResponseBodyPart bodyPart) throws Exception {
        bytes.write(bodyPart.getBodyPartBytes());
        return STATE.CONTINUE;
    }

    @Override
    public String onCompleted() throws Exception {
        String str =  null;
        if(bytes != null){
            try {
                str = bytes.toString("UTF-8");
                bytes.close();
                return str;
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
                return str;
            }finally{
            }
        }
        return str;
    }

    @Override
    public void onThrowable(final Throwable t) {
        if(bytes != null){
            try {
                bytes.close();
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
        }
    }

}
