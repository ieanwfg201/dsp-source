package com.kritter.bidreqres.reader.mopub2_3;

import com.kritter.bidreqres.entity.mopub2_3.BidRequestMopub;
import com.kritter.bidreqres.entity.mopub2_3.MopubBidRequestParentNodeDTO;
import com.kritter.bidrequest.entity.IBidRequest;
import com.kritter.bidrequest.exception.BidRequestException;
import com.kritter.bidrequest.reader.IBidRequestReader;
import com.kritter.utils.uuid.mac.UUIDGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * This class is responsible to read bid request from mopub ad-exchange.
 */

public class BidRequestReaderMopub implements IBidRequestReader
{
	private ObjectMapper jacksonObjectMapper;
    private UUIDGenerator uuidGenerator;
    private String mopubAuctioneerId;
    private Logger logger;

	public BidRequestReaderMopub(String loggerName, String mopubAuctioneerId)
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.mopubAuctioneerId = mopubAuctioneerId;
		this.jacksonObjectMapper = new ObjectMapper();
        this.uuidGenerator = new UUIDGenerator();
	}

	@Override
	public IBidRequest convertBidRequestPayloadToBusinessObject(String bidRequestJson) throws BidRequestException
    {
		if (null == bidRequestJson)
			throw new BidRequestException("Exception inside BidRequestReaderMopub, bid request received is null.");

        BidRequestMopub bidRequestMopub;
        MopubBidRequestParentNodeDTO mopubBidRequestParentNodeDTO;

		try
        {
            mopubBidRequestParentNodeDTO = this.jacksonObjectMapper.readValue(
                                                                              bidRequestJson,
                                                                              MopubBidRequestParentNodeDTO.class
                                                                             );
		} catch (JsonParseException e)
        {
            logger.error("JsonParseException inside convertBidRequestJsonToBusinessObject of BidRequestReaderMopub",e);
			throw new BidRequestException("JsonParseException inside convertBidRequestJsonToBusinessObject " +
                                          "of BidRequestReaderMopub", e);
		}
        catch (JsonMappingException e)
        {
            logger.error("JsonMappingException inside convertBidRequestJsonToBusinessObject of BidRequestReaderMopub",e);
			throw new BidRequestException("JsonMappingException inside convertBidRequestJsonToBusinessObject ", e);
		}
        catch (IOException e)
        {
            logger.error("IOException inside convertBidRequestJsonToBusinessObject of BidRequestReaderMopub",e);
			throw new BidRequestException("IOException inside convertBidRequestJsonToBusinessObject ", e);
		}

        String uniqueInternalBidRequestId = uuidGenerator.generateUniversallyUniqueIdentifier().toString();

        logger.debug("IBidRequest prepared inside BidRequestReaderMopub with bidRequestId: {} ," +
                     "uniqueInternalBidRequestId: {} ",
                     mopubBidRequestParentNodeDTO.getBidRequestId(),
                     uniqueInternalBidRequestId);

        bidRequestMopub = new BidRequestMopub(
                                              mopubAuctioneerId,
                                              uniqueInternalBidRequestId,
                                              mopubBidRequestParentNodeDTO
                                             );

		return bidRequestMopub;
	}

    /**
     * This method can check on mandatory parameters whether present inside mopub bid
     * request or not, as per their specifications or as required by us.
     * @param bidRequest
     * @throws BidRequestException
     */
	@Override
	public void validateBidRequestForMandatoryParameters(IBidRequest bidRequest) throws BidRequestException
    {

	}
}
