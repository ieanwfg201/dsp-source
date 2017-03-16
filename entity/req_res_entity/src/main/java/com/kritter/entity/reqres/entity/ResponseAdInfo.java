package com.kritter.entity.reqres.entity;

import com.kritter.constants.MarketPlace;
import com.kritter.entity.video_props.VideoInfo;
import com.kritter.entity.video_props.VideoProps;
import com.kritter.entity.native_props.demand.NativeDemandProps;
import com.kritter.entity.native_props.demand.NativeIcon;
import com.kritter.entity.native_props.demand.NativeScreenshot;
import com.kritter.serving.demand.entity.Creative;
import com.kritter.serving.demand.entity.CreativeBanner;
import com.kritter.serving.demand.entity.CreativeSlot;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * This class can be used to keep ad information like bid,budget,etc.
 * for an ad used in creative formatting or thrift logging.
 */
@EqualsAndHashCode(of={"adId"})
public class ResponseAdInfo
{
    @Getter
    private Integer adId;
    /**
     * hard bid is the internal bid (used in adserving purposes
     * and regarded as max bid for bidder) and advertiser bid is
     * the bid value facing the advertiser to see.
     */
    @Getter
    private Double hardBid;
    @Getter
    private Double advertiserBid;
    @Getter
    private Double dailyRemainingBudget;
    @Getter @Setter
    private String impressionId;
    @Getter @Setter
    private Double ecpmValue;
    @Getter @Setter
    private MarketPlace marketPlace;
    @Getter @Setter
    private String commonURIForPostImpression;
    @Getter @Setter
    private Integer campaignId;
    @Getter @Setter
    private CreativeBanner creativeBanner;
    /*The below set is used where there is requirement for sending out all images in the response,
    * should only be applicable for network traffic and not for ad-exchanges.*/
    @Getter @Setter
    private List<CreativeBanner> creativeBannerSetForNetworkTraffic;
    @Getter @Setter
    private short slotId;
    @Getter @Setter
    private CreativeSlot creativeSlot;
    @Getter @Setter
    private NativeDemandProps nativeDemandProps;
    @Getter @Setter
    private NativeIcon nativeIcon;
    @Getter @Setter
    private NativeScreenshot nativeScreenshot;
    @Getter @Setter
    private Creative creative;
    @Getter @Setter
    private double ctrValue;
    @Getter @Setter
    private double cpaValue;
    @Getter @Setter
    private boolean richMediaAdIsCompatibleForAdserving;
    @Getter @Setter
    private String richMediaPayLoadFromCreative;
    @Getter @Setter
    private Integer requestingWidthForWhichCreativeFound;
    @Getter @Setter
    private Integer requestingHeightForWhichCreativeFound;
    @Getter @Setter
    private Integer demandtype;
    @Getter @Setter
    private String guid;
    @Getter @Setter
    private String advertiserGuid;
    @Getter @Setter
    private String dealId;
    @Getter @Setter
    private VideoProps videoProps;
    @Getter @Setter
    private VideoInfo videoInfo;
    @Getter @Setter
    private Double expectedCtr;


    public ResponseAdInfo(Integer adId,Double hardBid,Double advertiserBid,Double dailyRemainingBudget)
    {
        this.adId = adId;
        this.hardBid = hardBid;
        this.advertiserBid = advertiserBid;
        this.dailyRemainingBudget = dailyRemainingBudget;

        //default value as -1 for slotid.
        this.slotId = -1;
    }
}
