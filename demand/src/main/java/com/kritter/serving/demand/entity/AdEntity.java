package com.kritter.serving.demand.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import com.kritter.constants.MarketPlace;
import com.kritter.entity.ad_ext.AdExt;
import com.kritter.entity.external_tracker.ExtTracker;

import com.kritter.entity.freqcap_entity.FreqCap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import java.io.StringReader;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.Template;

/**
 * Ad Entity holds a pointer to the creative for the content and
 * a pointer to the campaign for its targeting parameters
 * Ad(or a line item) is something that a creative becomes once it is published within a campaign
 */
@Getter
@ToString
@EqualsAndHashCode(of={"adGuid"})
public class AdEntity implements IUpdatableEntity<Integer>
{
    private final Integer adIncId;
    private final String adGuid;
    private final Integer creativeId;
    private final String creativeGuid;
    private final String landingUrl;
    private final Integer campaignIncId;
    private final String campaignGuid;
    private final Integer accountId;
    private final Short[] categoriesArray;
    private final Short[] hygieneArray;
    private final TargetingProfile targetingProfile;
    private final MarketPlace marketPlace;
    private final Double bid;
    private final Double advertiserBid;
    private final Integer trackingPartnerId;
    private final boolean markedForDeletion;
    private final Long modificationTime;
    private final Double cpaGoal;
    private final String[] advertiserDomains;
    private final Template lpVelocityTemplate;
    private boolean isFrequencyCapped;
    // Is valid only for frequency capped campaigns. Tells how many impressions should be served
    // for this ad in the given time window and the length of the window
    private int maxCap;
    private int frequencyCapTimeWindowInHours;
    private int demandtype;
    private int qps;
    private final String accountGuid;
    private int bidtype = 0;
    private ExtTracker extTracker;
    private boolean isRetargeted=false;
    private AdExt adExt;
    private FreqCap frequencyCap;

    public AdEntity(AdEntityBuilder adEntityBuilder)
    {
        this.adIncId = adEntityBuilder.adIncId;
        this.adGuid = adEntityBuilder.adGuid;
        this.creativeId = adEntityBuilder.creativeId;
        this.creativeGuid = adEntityBuilder.creativeGuid;
        this.landingUrl = adEntityBuilder.landingUrl;
        this.campaignIncId = adEntityBuilder.campaignIncId;
        this.campaignGuid = adEntityBuilder.campaignGuid;
        this.accountId = adEntityBuilder.accountId;
        this.categoriesArray = adEntityBuilder.categoryList;
        this.hygieneArray = adEntityBuilder.hygieneList;
        this.targetingProfile = adEntityBuilder.targetingProfile;
        this.marketPlace = adEntityBuilder.marketPlace;
        this.bid = adEntityBuilder.bid;
        this.advertiserBid = adEntityBuilder.advertiserBid;
        this.trackingPartnerId = adEntityBuilder.trackingPartnerId;
        this.cpaGoal = adEntityBuilder.cpaGoal;
        this.advertiserDomains = adEntityBuilder.advertiserDomains;
        this.markedForDeletion = adEntityBuilder.isMarkedForDeletion;
        this.modificationTime = adEntityBuilder.modificationTime;
        this.lpVelocityTemplate = adEntityBuilder.lpVelocityTemplate;
        this.isFrequencyCapped = adEntityBuilder.isFrequencyCapped;
        this.maxCap = adEntityBuilder.maxCap;
        this.frequencyCapTimeWindowInHours = adEntityBuilder.frequencyCapTimeWindowInHours;
        this.demandtype = adEntityBuilder.demandtype;
        this.qps = adEntityBuilder.qps;
        this.accountGuid = adEntityBuilder.accountGuid;
        this.bidtype =  adEntityBuilder.bidtype;
        this.extTracker = adEntityBuilder.extTracker;
        this.isRetargeted = adEntityBuilder.isRetargeted;
        this.adExt = adEntityBuilder.adExt;
        this.frequencyCap = adEntityBuilder.frequencyCap;
    }

    @Override
    public Integer getId()
    {
        return this.adIncId;
    }

    public static class AdEntityBuilder
    {
        private final Integer adIncId;
        private final String adGuid;
        private final Integer creativeId;
        private final String creativeGuid;
        private final Integer campaignIncId;
        private final String campaignGuid;
        private final Short[] categoryList;
        private final Short[] hygieneList;
        private final TargetingProfile targetingProfile;
        private final MarketPlace marketPlace;
        private final Double bid;
        private final Double advertiserBid;
        private final boolean isMarkedForDeletion;
        private final Long modificationTime;
        private Integer accountId;
        private String landingUrl;
        private  Integer trackingPartnerId;
        private Double cpaGoal;
        private String[] advertiserDomains;
        private Template lpVelocityTemplate;
        private final boolean isFrequencyCapped;
        // Is valid only for frequency capped campaigns. Tells how many impressions should be served
        // for this ad in the given time window and the length of the window
        private final int maxCap;
        private final int frequencyCapTimeWindowInHours;
        private final int demandtype;
        private final int qps;
        private String accountGuid;
        private int bidtype = 0;
        private ExtTracker extTracker;
        private boolean isRetargeted = false;
        private AdExt adExt;
        private FreqCap frequencyCap;
        
        public AdEntityBuilder(Integer adIncId,
                               String adGuid,
                               Integer creativeId,
                               String creativeGuid,
                               Integer campaignIncId,
                               String campaignGuid,
                               Short[] categoryList,
                               Short[] hygieneList,
                               TargetingProfile targetingProfile,
                               MarketPlace marketPlace,
                               Double bid,
                               Double advertiserBid,
                               boolean isMarkedForDeletion,
                               Long lastModifiedTime,
                               boolean isFrequencyCapped,
                               int maxCap,
                               int frequencyCapTimeWindowInHours,
                               int demandtype,
                               int qps,
                               String accountGuid,
                               int bidtype,
                               ExtTracker extTracker,
							   boolean isRetargeted) throws Exception
        {
            this.adIncId = adIncId;
            this.adGuid = adGuid;
            this.creativeId = creativeId;
            this.creativeGuid = creativeGuid;
            this.campaignIncId = campaignIncId;
            this.campaignGuid = campaignGuid;
            this.categoryList = categoryList;
            this.hygieneList = hygieneList;
            this.targetingProfile = targetingProfile;
            this.marketPlace = marketPlace;
            this.bid = bid;
            this.advertiserBid = advertiserBid;
            this.isMarkedForDeletion = isMarkedForDeletion;
            this.modificationTime = lastModifiedTime;
            this.isFrequencyCapped = isFrequencyCapped;
            this.maxCap = maxCap;
            this.frequencyCapTimeWindowInHours = frequencyCapTimeWindowInHours;
            this.demandtype = demandtype;
            this.qps = qps;
            this.accountGuid = accountGuid;
            this.bidtype = bidtype;
            this.extTracker = extTracker;
            this.isRetargeted = isRetargeted;
        }

        public AdEntityBuilder setLandingUrl(String landingUrl)
        {
            this.landingUrl = landingUrl;
            return this;
        }

        public AdEntityBuilder setAccountId(Integer accountId)
        {
            this.accountId = accountId;
            return this;
        }

        public AdEntityBuilder setTrackingPartnerId(Integer trackingPartnerId)
        {
            this.trackingPartnerId = trackingPartnerId;
            return this;
        }

        public AdEntityBuilder setCpaGoal(Double cpaGoal)
        {
            this.cpaGoal = cpaGoal;
            return this;
        }

        public AdEntityBuilder setAdvertiserDomain(String[] advertiserDomains)
        {
            this.advertiserDomains = advertiserDomains;
            return this;
        }
        public AdEntityBuilder setAdExt(AdExt adExt)
        {
            this.adExt = adExt;
            return this;
        }

        public AdEntityBuilder setFrequencyCap(String frequencyCap) throws Exception {
            this.frequencyCap = FreqCap.getObject(frequencyCap);
            return this;
        }

        public AdEntity build()
        {
            /*
               Set the velocity template for the given landing URL
               */
            if(this.landingUrl != null) {
                try {
                    RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();
                    StringReader reader = new StringReader(this.landingUrl);
                    SimpleNode node = runtimeServices.parse(reader, "");
                    this.lpVelocityTemplate = new Template();
                    this.lpVelocityTemplate.setRuntimeServices(runtimeServices);
                    this.lpVelocityTemplate.setData(node);
                    this.lpVelocityTemplate.initDocument();
                } catch(Exception e) {
                    throw new RuntimeException(e);
                }
            }

            return new AdEntity(this);
        }
    }
}
