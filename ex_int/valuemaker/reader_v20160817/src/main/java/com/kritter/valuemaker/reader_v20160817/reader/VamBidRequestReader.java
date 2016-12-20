package com.kritter.valuemaker.reader_v20160817.reader;

import RTB.VamRealtimeBidding;
import com.kritter.bidrequest.entity.IBidRequest;
import com.kritter.bidrequest.exception.BidRequestException;
import com.kritter.bidrequest.reader.IBidRequestReader;
import com.kritter.utils.uuid.mac.UUIDGenerator;
import com.kritter.valuemaker.reader_v20160817.converter.request.ConvertRequest;
import com.kritter.valuemaker.reader_v20160817.entity.BidRequestVam;
import com.kritter.valuemaker.reader_v20160817.entity.VamBidRequestParentNodeDTO;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class reads valuemaker bid request payload and converts it into open rtb payload.
 */
public class VamBidRequestReader implements IBidRequestReader {

    private Logger logger;
    private String auctioneerId;
    private UUIDGenerator uuidGenerator;

    public VamBidRequestReader(String loggerName, String auctioneerId) {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.auctioneerId = auctioneerId;
        this.uuidGenerator = new UUIDGenerator();
    }

    public IBidRequest convertBidRequestPayloadToBusinessObject(InputStream vamRequestInputStream) throws IOException {
        if (null == vamRequestInputStream) {
            return null;
        }
        VamRealtimeBidding.VamRequest bidRequest = null;
        try {
            bidRequest = VamRealtimeBidding.VamRequest.parseFrom(IOUtils.toByteArray(vamRequestInputStream));
        } catch (Exception e) {
            logger.error(e.toString());
        }
        logger.debug(bidRequest.toString());

        VamBidRequestParentNodeDTO vamBidRequestParentNodeDTO = ConvertRequest.convert(bidRequest);
        String uniqueInternalBidRequestId = uuidGenerator.generateUniversallyUniqueIdentifier().toString();
        IBidRequest iBidRequest = new BidRequestVam(auctioneerId, uniqueInternalBidRequestId, vamBidRequestParentNodeDTO);

        return iBidRequest;
    }

    @Override
    public IBidRequest convertBidRequestPayloadToBusinessObject(String bidRequestPayload) throws BidRequestException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void validateBidRequestForMandatoryParameters(IBidRequest bidRequest) throws BidRequestException {
        // TODO Auto-generated method stub

    }
}
