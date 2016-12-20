package com.kritter.bidreqres.reader.cloudcross4_4_4;

import com.kritter.bidreqres.entity.cloudcross4_4_4.BidRequestCloudCross;
import com.kritter.bidreqres.entity.cloudcross4_4_4.BidRequestCloudCrossDTO;
import com.kritter.bidreqres.entity.cloudcross4_4_4.CloudCrossBidRequestParentNodeDTO;
import com.kritter.bidrequest.entity.IBidRequest;
import com.kritter.bidrequest.exception.BidRequestException;
import com.kritter.bidrequest.reader.IBidRequestReader;
import com.kritter.utils.uuid.mac.UUIDGenerator;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 将云联请求的json转成我们需要的标准openRTB2.3
 * Created by hamlin on 7/5/16.
 */
public class BidRequestReaderCloudCross implements IBidRequestReader {
    private ObjectMapper jacksonObjectMapper;
    private UUIDGenerator uuidGenerator;
    private String cloudCrossAuctioneerId;
    private Logger logger;

    public BidRequestReaderCloudCross(String loggerName, String cloudCrossAuctioneerId) {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.cloudCrossAuctioneerId = cloudCrossAuctioneerId;
        this.jacksonObjectMapper = new ObjectMapper();
        this.uuidGenerator = new UUIDGenerator();
    }

    @Override
    public IBidRequest convertBidRequestPayloadToBusinessObject(String bidRequestJson) throws BidRequestException {
        if (null == bidRequestJson)
            throw new BidRequestException("Exception inside BidRequestReaderCloudCross, bid request received is null.");

        BidRequestCloudCross bidRequestCloudCross;

        CloudCrossBidRequestParentNodeDTO cloudCrossBidRequestParentNodeDTO;


        try {
            cloudCrossBidRequestParentNodeDTO = this.jacksonObjectMapper.readValue(bidRequestJson, BidRequestCloudCrossDTO.class).getBidRequest();
            String uuidType = cloudCrossBidRequestParentNodeDTO.getCloudCrossBidRequestDeviceDTO().getUuidType();
            String uuid = cloudCrossBidRequestParentNodeDTO.getCloudCrossBidRequestDeviceDTO().getUuid();
            if (!StringUtils.isEmpty(uuidType)) {
                switch (uuidType) {
                    case "mac":
                        cloudCrossBidRequestParentNodeDTO.getBidRequestDevice().setHashedMD5MacAddressOfDevice(uuid);
                        break;
                    case "idfa":
                        cloudCrossBidRequestParentNodeDTO.getBidRequestDevice().setMD5HashedDeviceId(uuid);
                        break;
                    case "imei":
                        cloudCrossBidRequestParentNodeDTO.getBidRequestDevice().setMD5HashedDeviceId(uuid);
                        break;
                    default:
                        break;
                }
            }
        } catch (JsonParseException e) {
            logger.error("JsonParseException inside convertBidRequestJsonToBusinessObject of BidRequestReaderCloudCross", e);
            throw new BidRequestException("JsonParseException inside convertBidRequestJsonToBusinessObject " +
                    "of BidRequestReaderCloudCross", e);
        } catch (JsonMappingException e) {
            logger.error("JsonMappingException inside convertBidRequestJsonToBusinessObject of BidRequestReaderCloudCross", e);
            throw new BidRequestException("JsonMappingException inside convertBidRequestJsonToBusinessObject ", e);
        } catch (IOException e) {
            logger.error("IOException inside convertBidRequestJsonToBusinessObject of BidRequestReaderCloudCross", e);
            throw new BidRequestException("IOException inside convertBidRequestJsonToBusinessObject ", e);
        }

        String uniqueInternalBidRequestId = "6";

        logger.debug("IBidRequest prepared inside BidRequestReaderCloudCross with bidRequestId: {} ," +
                        "uniqueInternalBidRequestId: {} ",
                cloudCrossBidRequestParentNodeDTO != null ? cloudCrossBidRequestParentNodeDTO.getBidRequestId() : null,
                uniqueInternalBidRequestId);

        bidRequestCloudCross = new BidRequestCloudCross(
                cloudCrossAuctioneerId,
                uniqueInternalBidRequestId,
                cloudCrossBidRequestParentNodeDTO
        );

        return bidRequestCloudCross;
    }

    @Override
    public void validateBidRequestForMandatoryParameters(IBidRequest bidRequest) throws BidRequestException {

    }
}
