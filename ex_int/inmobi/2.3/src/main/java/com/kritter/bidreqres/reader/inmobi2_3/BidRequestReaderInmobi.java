package com.kritter.bidreqres.reader.inmobi2_3;

import com.kritter.bidrequest.entity.IBidRequest;
import com.kritter.bidreqres.entity.inmobi2_3.BidRequestInmobi;
import com.kritter.bidreqres.entity.inmobi2_3.InmobiBidRequestParentNodeDTO;
import com.kritter.bidrequest.exception.BidRequestException;
import com.kritter.bidrequest.reader.IBidRequestReader;
import com.kritter.utils.uuid.mac.UUIDGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;

/**
 * This class is responsible to read bid request from inmobi 2.3 ad-exchange.
 */

public class BidRequestReaderInmobi implements IBidRequestReader
{
    private UUIDGenerator uuidGenerator;
    private String inmobiAuctioneerId;
    private Logger logger;
    private ObjectMapper jacksonObjectMapper;

    public BidRequestReaderInmobi(String loggerName, String inmobiAuctioneerId)
    {
        this.logger = LogManager.getLogger(loggerName);
        this.inmobiAuctioneerId = inmobiAuctioneerId;
        this.uuidGenerator = new UUIDGenerator();
        this.jacksonObjectMapper =  new ObjectMapper();
        jacksonObjectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        jacksonObjectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	}

	@Override
	public IBidRequest convertBidRequestPayloadToBusinessObject(String bidRequestJson) throws BidRequestException
    {
		if (null == bidRequestJson)
			throw new BidRequestException("Exception inside BidRequestReaderInmobi, bid request received is null.");

        BidRequestInmobi bidRequestInmobi;
        InmobiBidRequestParentNodeDTO inmobiBidRequestParentNodeDTO;

		try
        {
            inmobiBidRequestParentNodeDTO = jacksonObjectMapper.readValue(
                                                                                  bidRequestJson,
                                                                                  InmobiBidRequestParentNodeDTO.class
                                                                                 );
		} catch (JsonParseException e)
        {
            logger.error("JsonParseException inside convertBidRequestJsonToBusinessObject of BidRequestReaderInmobi",e);
			throw new BidRequestException("JsonParseException inside convertBidRequestJsonToBusinessObject " +
                                          "of BidRequestReaderInmobi", e);
		}
        catch (JsonMappingException e)
        {
            logger.error("JsonMappingException inside convertBidRequestJsonToBusinessObject of BidRequestReaderInmobi",e);
			throw new BidRequestException("JsonMappingException inside convertBidRequestJsonToBusinessObject ", e);
		}
        catch (IOException e)
        {
            logger.error("IOException inside convertBidRequestJsonToBusinessObject of BidRequestReaderInmobi",e);
			throw new BidRequestException("IOException inside convertBidRequestJsonToBusinessObject ", e);
		}

        String uniqueInternalBidRequestId = uuidGenerator.generateUniversallyUniqueIdentifier().toString();

        logger.debug("IBidRequest prepared inside BidRequestReaderInmobi with bidRequestId: {} ," +
                     "uniqueInternalBidRequestId: {} ",
                     inmobiBidRequestParentNodeDTO.getBidRequestId(),
                     uniqueInternalBidRequestId);

        bidRequestInmobi = new BidRequestInmobi(
                                                      inmobiAuctioneerId,
                                                      uniqueInternalBidRequestId,
                                                      inmobiBidRequestParentNodeDTO
                                                     );

		return bidRequestInmobi;
	}

    /**
     * This method can check on mandatory parameters whether present inside inmobi 2.3 bid
     * request or not, as per their specifications or as required by us.
     * @param bidRequest
     * @throws com.kritter.bidrequest.exception.BidRequestException
     */
	@Override
	public void validateBidRequestForMandatoryParameters(IBidRequest bidRequest) throws BidRequestException
    {

	}
}
