package com.kritter.exchange.custom.dsp.response;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseBidEntity;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseEntity;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseSeatBidEntity;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.native1_0.resp.*;
import com.kritter.constants.ConvertErrorEnum;
import com.kritter.exchange.response_openrtb_2_3.converter.common.ConvertEntity;
import com.kritter.exchange.response_openrtb_2_3.converter.v1.ConvertResponse;
import com.kritter.utils.common.ApplicationGeneralUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class converts bid response from pubnative to open rtb 2.3 compliant version.
 */
public class ConvertPubNativeBidResponse extends ConvertResponse
{
    private Logger logger;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public ConvertPubNativeBidResponse(String loggerName)
    {
        super(loggerName);
        this.logger = LogManager.getLogger(loggerName);
    }

    @Override
    public BidResponseEntity convert(String str)
    {
        /**read response into pubnative's custom object*/
        if(null == str || "".equals(str))
           return null;

        PubNativeBidResponseEntity pubNativeBidResponseEntity = null;

        try
        {
            logger.debug("BidResponse from Pubnative DSP is: {} ", str);

            pubNativeBidResponseEntity = objectMapper.readValue(str, PubNativeBidResponseEntity.class);

            /**Form error response in case returned response from pubnative is error.*/
            if (
                null != pubNativeBidResponseEntity              &&
                null != pubNativeBidResponseEntity.getStatus()  &&
                pubNativeBidResponseEntity.getStatus().equalsIgnoreCase("error")
               )
            {
                BidResponseEntity bidResponseEntity = new BidResponseEntity();
                bidResponseEntity.setNoBidReason(ApplicationGeneralUtils.DSP_NO_BID_RESPONSE_ERROR_CODE_KRITTER);
                return bidResponseEntity;
            }

            if(
                null == pubNativeBidResponseEntity          ||
                null == pubNativeBidResponseEntity.getAds() ||
                pubNativeBidResponseEntity.getAds().length <= 0
              )
                return null;
        }
        catch (Exception e)
        {
            logger.error("Exception in reading response payload from pubnative: {} ", str, e);
            return null;
        }

        /*populate 2.3 version response entity using pubnative bid response entity*/
        BidResponseEntity bidResponseEntity = null;

        try
        {
            bidResponseEntity = populateBidResponseEntityUsingPubnative(pubNativeBidResponseEntity);
        }
        catch (Exception e)
        {
            logger.error("Exception inside ConvertPubNativeBidResponse ",e);
        }

        if(null == bidResponseEntity)
            return null;

        ConvertEntity convertEntity = new ConvertEntity();
        convertEntity.setErrorEnum(ConvertErrorEnum.HEALTHY_CONVERT);
        convertEntity.setResponse(bidResponseEntity);

        if(convertEntity.getErrorEnum() == ConvertErrorEnum.HEALTHY_CONVERT)
        {
            return convertEntity.getResponse();
        }

        return null;
    }

    private BidResponseEntity populateBidResponseEntityUsingPubnative(PubNativeBidResponseEntity pubNativeBidResponseEntity) throws Exception
    {
        if(null == pubNativeBidResponseEntity)
            return null;

        BidResponseEntity bidResponseEntity = new BidResponseEntity();
        bidResponseEntity.setBidRequestId("pubnative_bid_request_id");
        bidResponseEntity.setBidderGeneratedUniqueId("pubnative_bidder_gen_id");


        BidResponseSeatBidEntity[] bidResponseSeatBidEntities = new BidResponseSeatBidEntity[1];
        BidResponseSeatBidEntity bidResponseSeatBidEntity = new BidResponseSeatBidEntity();
        bidResponseSeatBidEntity.setBidderSeatId("pubnative-dsp");
        BidResponseBidEntity bidResponseBidEntity = new BidResponseBidEntity();
        BidResponseBidEntity[] bidResponseBidEntities = new BidResponseBidEntity[1];


        /**convert bid response string content to definition of RespNativeParent 2.3 version*/
        RespNativeParent respNativeParent = new RespNativeParent();
        RespNativeFirstLevel respNativeFirstLevel = new RespNativeFirstLevel();
        float price = 0.0f;

        if(null != pubNativeBidResponseEntity.getAds() && pubNativeBidResponseEntity.getAds().length > 0)
        {
            for(PubNativeBidResponseAd pubNativeBidResponseAd : pubNativeBidResponseEntity.getAds())
            {
                if(null != pubNativeBidResponseAd.getAssets() && pubNativeBidResponseAd.getAssets().length > 0)
                {
                    RespAsset[] respAssets = null;
                    List<RespAsset> respAssetList = new ArrayList<RespAsset>();

                    for(PubNativeBidResponseAdAsset pubNativeBidResponseAdAsset : pubNativeBidResponseAd.getAssets())
                    {
                        RespAsset respAsset = new RespAsset();

                        if(pubNativeBidResponseAdAsset.getType().equalsIgnoreCase("icon"))
                        {
                            RespImage respImage = new RespImage();
                            respImage.setH(pubNativeBidResponseAdAsset.getData().getH());
                            respImage.setW(pubNativeBidResponseAdAsset.getData().getW());
                            respImage.setUrl(pubNativeBidResponseAdAsset.getData().getUrl());
                            respAsset.setImg(respImage);
                            respAssetList.add(respAsset);
                        }
                        else if(pubNativeBidResponseAdAsset.getType().equalsIgnoreCase("banner"))
                        {
                            RespImage respImage = new RespImage();
                            respImage.setH(pubNativeBidResponseAdAsset.getData().getH());
                            respImage.setW(pubNativeBidResponseAdAsset.getData().getW());
                            respImage.setUrl(pubNativeBidResponseAdAsset.getData().getUrl());
                            respAsset.setImg(respImage);
                            respAssetList.add(respAsset);
                        }
                        else if(pubNativeBidResponseAdAsset.getType().equalsIgnoreCase("title"))
                        {
                            RespTitle respTitle = new RespTitle();
                            respTitle.setText(pubNativeBidResponseAdAsset.getData().getText());
                            respAsset.setTitle(respTitle);
                            respAssetList.add(respAsset);
                        }
                        else if(pubNativeBidResponseAdAsset.getType().equalsIgnoreCase("description"))
                        {
                            RespData respData = new RespData();
                            respData.setValue(pubNativeBidResponseAdAsset.getData().getText());
                            respAsset.setData(respData);
                            respAssetList.add(respAsset);
                        }
                    }

                    respAssets = respAssetList.toArray(new RespAsset[respAssetList.size()]);
                    respNativeFirstLevel.setAssets(respAssets);

                    /**set beacons*/
                    if(null != pubNativeBidResponseAd.getBeacons() && pubNativeBidResponseAd.getBeacons().length > 0)
                    {
                        /**set impression trackers*/
                        String[] trackers = new String[pubNativeBidResponseAd.getBeacons().length];
                        int counterTracker = 0;
                        for(PubNativeBidResponseBeacon beacon : pubNativeBidResponseAd.getBeacons())
                        {
                            if(beacon.getType().equalsIgnoreCase("impression"))
                            {
                                /**set impression tracker to be win notification url for now.*/
                                trackers[counterTracker ++ ] = beacon.getData().getUrl();
                                bidResponseBidEntity.setWinNotificationUrl(beacon.getData().getUrl());
                                break;
                            }
                        }
                        //TODO: check later if required, the impression tracker is included as win url for now.
                        //respNativeFirstLevel.setImptrackers(trackers);
                    }

                    String link = null;

                    try
                    {
                        link = generateLinkObject(pubNativeBidResponseAd.getLink());
                    }
                    catch (IOException ioe)
                    {
                        logger.error("IOException inside ConvertPubNativeBidResponse , landing url cannot be used for " +
                                     "link conversion", ioe);
                    }

                    respNativeFirstLevel.setLink(link);

                    /**calculate price value, divide by 1000 to get $ value, 1000 points mean 1$ cpm*/
                    if (null != pubNativeBidResponseAd.getMeta() && pubNativeBidResponseAd.getMeta().length > 0)
                    {
                        for(PubNativeBidResponseMeta meta : pubNativeBidResponseAd.getMeta())
                        {
                            if(meta.getType().equalsIgnoreCase("points"))
                            {
                                float pointsValue = meta.getData().getNumber();
                                float denominator = 1000;
                                price = pointsValue / denominator;
                                break;
                            }
                        }
                    }


                }
            }
        }

        respNativeParent.setRespNativeForstLevel(respNativeFirstLevel);

        bidResponseBidEntity.setAdMarkup(objectMapper.writeValueAsString(respNativeParent));
        bidResponseBidEntity.setPrice(price);
        bidResponseBidEntities[0] = bidResponseBidEntity;

        bidResponseSeatBidEntity.setBidResponseBidEntities(bidResponseBidEntities);

        bidResponseSeatBidEntities[0] = bidResponseSeatBidEntity;
        bidResponseEntity.setBidResponseSeatBid(bidResponseSeatBidEntities);

        return bidResponseEntity;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PubNativeBidResponseEntity
    {
        @Setter @Getter
        private String status;
        @JsonProperty
        @Setter @Getter
        private PubNativeBidResponseAd ads[];
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PubNativeBidResponseAd
    {
        @Setter @Getter
        private String link;
        @Setter @Getter
        private String assetgroupid;
        @Setter @Getter
        private PubNativeBidResponseAdAsset assets[];
        @Setter @Getter
        private PubNativeBidResponseBeacon beacons[];
        @Setter @Getter
        private PubNativeBidResponseMeta meta[];
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PubNativeBidResponseAdAsset
    {
        @Setter @Getter
        private String type;
        @Setter @Getter
        private PubNativeBidResponseAdAssetData data;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PubNativeBidResponseAdAssetData
    {
        @Setter @Getter
        private int w;
        @Setter @Getter
        private int h;
        @Setter @Getter
        private String url;
        @Setter @Getter
        private String text;
        @Setter @Getter
        private int number;
        @Setter @Getter
        private String icon;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PubNativeBidResponseBeacon
    {
        @Setter @Getter
        private String type;
        @Setter @Getter
        private PubNativeBidResponseAdAssetData data;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PubNativeBidResponseMeta
    {
        @Setter @Getter
        private String type;
        @Setter @Getter
        private PubNativeBidResponseAdAssetData data;
    }

    private String generateLinkObject(String landingURL) throws IOException
    {
        Link link = new Link();
        link.setUrl(landingURL);
        return objectMapper.writeValueAsString(link);
    }

    public static class Link
    {
        @Setter @Getter
        private String url;
    }
}