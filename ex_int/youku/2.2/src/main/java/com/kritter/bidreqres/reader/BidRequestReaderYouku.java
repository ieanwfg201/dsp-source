package com.kritter.bidreqres.reader;

import com.kritter.bidreqres.entity.BidRequestYouku;
import com.kritter.bidreqres.entity.YoukuBidRequestImpressionDTO;
import com.kritter.bidreqres.entity.YoukuBidRequestParentNodeDTO;
import com.kritter.bidrequest.entity.IBidRequest;
import com.kritter.bidrequest.exception.BidRequestException;
import com.kritter.bidrequest.reader.IBidRequestReader;
import com.kritter.constants.VideoMimeTypes;
import com.kritter.utils.uuid.mac.UUIDGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * This class is responsible to read bid request from youku ad-exchange.
 */

public class BidRequestReaderYouku implements IBidRequestReader {
    private UUIDGenerator uuidGenerator;
    private String youkuAuctioneerId;
    private Logger logger;
    private ObjectMapper jacksonObjectMapper;

    public BidRequestReaderYouku(String loggerName, String youkuAuctioneerId) {
        this.logger = LogManager.getLogger(loggerName);
        this.youkuAuctioneerId = youkuAuctioneerId;
        this.uuidGenerator = new UUIDGenerator();
        this.jacksonObjectMapper = new ObjectMapper();
    }

    @Override
    public IBidRequest convertBidRequestPayloadToBusinessObject(String bidRequestJson) throws BidRequestException {
        if (null == bidRequestJson)
            throw new BidRequestException("Exception inside BidRequestReaderYouku, bid request received is null.");

        BidRequestYouku bidRequestYouku;
        YoukuBidRequestParentNodeDTO youkuBidRequestParentNodeDTO;

        try {
            youkuBidRequestParentNodeDTO = jacksonObjectMapper.readValue(
                    bidRequestJson,
                    YoukuBidRequestParentNodeDTO.class
            );
        } catch (JsonParseException e) {
            logger.error("JsonParseException inside convertBidRequestJsonToBusinessObject of BidRequestReaderYouku", e);
            throw new BidRequestException("JsonParseException inside convertBidRequestJsonToBusinessObject " +
                    "of BidRequestReaderYouku", e);
        } catch (JsonMappingException e) {
            logger.error("JsonMappingException inside convertBidRequestJsonToBusinessObject of " +
                    "BidRequestReaderYouku", e);
            throw new BidRequestException("JsonMappingException inside convertBidRequestJsonToBusinessObject ", e);
        } catch (IOException e) {
            logger.error("IOException inside convertBidRequestJsonToBusinessObject of BidRequestReaderYouku", e);
            throw new BidRequestException("IOException inside convertBidRequestJsonToBusinessObject ", e);
        }

        String uniqueInternalBidRequestId = uuidGenerator.generateUniversallyUniqueIdentifier().toString();

        logger.debug("IBidRequest prepared inside BidRequestReaderYouku with bidRequestId: {} ," +
                        "uniqueInternalBidRequestId: {} ",
                youkuBidRequestParentNodeDTO.getBidRequestId(),
                uniqueInternalBidRequestId);

        // youku only give us "video/x-flv", but in fact youku is to support other formats.
        String[] strings = new String[VideoMimeTypes.values().length];
        int n = 0;
        for (VideoMimeTypes videoMimeType : VideoMimeTypes.values()) {
            strings[n] = videoMimeType.getMime();
            n++;
        }
        for (YoukuBidRequestImpressionDTO bidRequestImpressionDTO : youkuBidRequestParentNodeDTO.getYoukuBidRequestImpressionDTOs()) {
            if (bidRequestImpressionDTO.getBidRequestImpressionVideoObject() != null) {
                bidRequestImpressionDTO.getBidRequestImpressionVideoObject().setMimeTypesSupported(strings);
            }
        }

        bidRequestYouku = new BidRequestYouku(
                youkuAuctioneerId,
                uniqueInternalBidRequestId,
                youkuBidRequestParentNodeDTO
        );

        return bidRequestYouku;
    }

    /**
     * This method can check on mandatory parameters whether present inside youku bid
     * request or not, as per their specifications or as required by us.
     *
     * @param bidRequest
     * @throws BidRequestException
     */
    @Override
    public void validateBidRequestForMandatoryParameters(IBidRequest bidRequest) throws BidRequestException {

    }
}
