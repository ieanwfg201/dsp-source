package com.kritter.tencent.reader_v20150313.reader;

import com.kritter.bidrequest.entity.IBidRequest;
import com.kritter.bidrequest.exception.BidRequestException;
import com.kritter.bidrequest.reader.IBidRequestReader;
import com.kritter.tencent.reader_v20150313.converter.request.ConvertRequest;
import com.kritter.tencent.reader_v20150313.entity.TencentBidRequestParentNodeDTO;
import RTB.Tencent.Request;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class reads tencent bid request payload and converts it into open rtb payload.
 */
public class TencentBidRequestReader implements IBidRequestReader
{

    private Logger logger;
    private Logger bidRequestLogger;

    public TencentBidRequestReader(
                               String loggerName,
                               String bidRequestLoggerName
                               )
    {
        this.logger = LogManager.getLogger(loggerName);
        this.bidRequestLogger = LogManager.getLogger(bidRequestLoggerName);
    }

    /**
     * This method converts google tencent bid request payload into open rtb 2.3 object.
     */
    public TencentBidRequestParentNodeDTO readAndConvertBidRequestPayLoadToOpenRTB_2_3(InputStream tencentRequestInputStream)
                                                                                throws IOException
    {
        if(null == tencentRequestInputStream)
            return null;

        Request.Builder tencentBidRequestBuilder = Request.newBuilder();

        byte[] rawBytes = IOUtils.toByteArray(tencentRequestInputStream);
        Base64 base64Encoder = new Base64(0);
        bidRequestLogger.debug(new String(base64Encoder.encode(rawBytes)));

        tencentBidRequestBuilder  = tencentBidRequestBuilder.mergeFrom(rawBytes);
        Request request = tencentBidRequestBuilder.build();
        return ConvertRequest.convert(request);
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
