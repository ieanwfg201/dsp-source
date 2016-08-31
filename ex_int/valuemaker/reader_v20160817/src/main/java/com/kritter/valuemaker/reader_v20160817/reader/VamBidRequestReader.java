package com.kritter.valuemaker.reader_v20160817.reader;

import RTB.VamRealtimeBidding;
import com.kritter.bidrequest.entity.IBidRequest;
import com.kritter.bidrequest.exception.BidRequestException;
import com.kritter.bidrequest.reader.IBidRequestReader;
import com.kritter.valuemaker.reader_v20160817.converter.request.ConvertRequest;
import com.kritter.valuemaker.reader_v20160817.entity.VamBidRequestParentNodeDTO;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class reads valuemaker bid request payload and converts it into open rtb payload.
 */
public class VamBidRequestReader implements IBidRequestReader
{

    private Logger logger;
    private Logger bidRequestLogger;

    public VamBidRequestReader(
                               String loggerName,
                               String bidRequestLoggerName
                               )
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.bidRequestLogger = LoggerFactory.getLogger(bidRequestLoggerName);
    }

    /**
     * This method converts google valuemaker bid request payload into open rtb 2.3 object.
     */
    public VamBidRequestParentNodeDTO readAndConvertBidRequestPayLoadToOpenRTB_2_3(InputStream vamRequestInputStream)
                                                                                throws IOException
    {
        if(null == vamRequestInputStream)
            return null;

        VamRealtimeBidding.VamRequest bidRequest=null;
        try{
            bidRequest = VamRealtimeBidding.VamRequest.parseFrom(IOUtils.toByteArray(vamRequestInputStream));
        }catch (Exception e){
            e.printStackTrace();
        }

        return ConvertRequest.convert(bidRequest);
    }

    @Override
    public IBidRequest convertBidRequestPayloadToBusinessObject(
            String bidRequestPayload) throws BidRequestException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void validateBidRequestForMandatoryParameters(IBidRequest bidRequest)
            throws BidRequestException {
        // TODO Auto-generated method stub
        
    }
}
