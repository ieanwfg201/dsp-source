package com.kritter.adserving.shortlisting.utils;

import com.kritter.constants.FreqDuration;
import com.kritter.constants.FreqEventType;
import com.kritter.entity.freqcap_entity.FreqCap;
import com.kritter.entity.freqcap_entity.FreqDef;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.cache.CampaignCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.Campaign;

import java.util.HashSet;
import java.util.Set;

public class LifetimeCapUtils {
    /**
     * Given a set of ad ids, returns all the ads that have life time impression cap set
     * @param adIdSet Input set of ad ids.
     * @return set of ad ids that have life time impression cap set
     */
    public static Set<Integer> getCappedAds(AdEntityCache adEntityCache, Set<Integer> adIdSet, FreqEventType eventType) {
        if(adIdSet == null) {
            return null;
        }

        Set<Integer> adIdSetToReturn = new HashSet<Integer>();
        for(int adId : adIdSet) {
            AdEntity adEntity = adEntityCache.query(adId);
            FreqCap freqCap = adEntity.getFrequencyCap();
            boolean isCapped = false;
            if(freqCap != null && freqCap.getFDef() != null && freqCap.getFDef().containsKey(eventType)) {
                Set<FreqDef> frequencyDefinition = freqCap.getFDef().get(eventType);
                for(FreqDef def : frequencyDefinition) {
                    if(def.getDuration() == FreqDuration.LIFE) {
                        isCapped = true;
                    }
                }
            }
            if(isCapped) {
                adIdSetToReturn.add(adId);
            }
        }

        return adIdSetToReturn;
    }

    /**
     * Given a set of ad ids, returns all the ads that don't have life time impression cap set
     * @param adIdSet Input set of ad ids.
     * @return set of ad ids that don't have life time impression cap set
     */
    public static Set<Integer> getNonCappedAds(AdEntityCache adEntityCache, Set<Integer> adIdSet, FreqEventType eventType) {
        if(adIdSet == null) {
            return null;
        }

        Set<Integer> adIdSetToReturn = new HashSet<Integer>();
        for(int adId : adIdSet) {
            AdEntity adEntity = adEntityCache.query(adId);
            FreqCap freqCap = adEntity.getFrequencyCap();
            boolean isCapped = false;
            if(freqCap != null && freqCap.getFDef() != null && freqCap.getFDef().containsKey(eventType)) {
                Set<FreqDef> frequencyDefinition = freqCap.getFDef().get(eventType);
                for(FreqDef def : frequencyDefinition) {
                    if(def.getDuration() == FreqDuration.LIFE) {
                        isCapped = true;
                    }
                }
            }
            if(!isCapped) {
                adIdSetToReturn.add(adId);
            }
        }

        return adIdSetToReturn;
    }

    /**
     * Given a set of ad ids, returns all the ads whose campaign have life time impression cap set
     * @param adIdSet Input set of ad ids.
     * @return set of ad ids whose campaign have life time impression cap set
     */
    public static Set<Integer> getCappedCampaigns(AdEntityCache adEntityCache,
                                                  CampaignCache campaignCache,
                                                  Set<Integer> adIdSet,
                                                  FreqEventType eventType) {
        if(adIdSet == null) {
            return null;
        }

        Set<Integer> adIdSetToReturn = new HashSet<Integer>();
        for(int adId : adIdSet) {
            AdEntity adEntity = adEntityCache.query(adId);
            int campaignId = adEntity.getCampaignIncId();
            Campaign campaign = campaignCache.query(campaignId);
            FreqCap freqCap = campaign.getFrequencyCap();
            boolean isCapped = false;
            if(freqCap != null && freqCap.getFDef() != null && freqCap.getFDef().containsKey(eventType)) {
                Set<FreqDef> frequencyDefinition = freqCap.getFDef().get(eventType);
                for(FreqDef def : frequencyDefinition) {
                    if(def.getDuration() == FreqDuration.LIFE) {
                        isCapped = true;
                    }
                }
            }
            if(isCapped) {
                adIdSetToReturn.add(adId);
            }
        }

        return adIdSetToReturn;
    }

    /**
     * Given a set of campaign ids, returns all the campaigns that don't have life time impression cap set
     * @param adIdSet Input set of campaign ids.
     * @return set of campaign ids that don't have life time impression cap set
     */
    public static Set<Integer> getNonCappedCampaigns(AdEntityCache adEntityCache,
                                                     CampaignCache campaignCache,
                                                     Set<Integer> adIdSet,
                                                     FreqEventType eventType) {
        if(adIdSet == null) {
            return null;
        }

        Set<Integer> adIdSetToReturn = new HashSet<Integer>();
        for(int adId : adIdSet) {
            AdEntity adEntity = adEntityCache.query(adId);
            int campaignId = adEntity.getCampaignIncId();
            Campaign campaign = campaignCache.query(campaignId);
            FreqCap freqCap = campaign.getFrequencyCap();
            boolean isCapped = false;
            if(freqCap != null && freqCap.getFDef() != null && freqCap.getFDef().containsKey(eventType)) {
                Set<FreqDef> frequencyDefinition = freqCap.getFDef().get(eventType);
                for(FreqDef def : frequencyDefinition) {
                    if(def.getDuration() == FreqDuration.LIFE) {
                        isCapped = true;
                    }
                }
            }
            if(!isCapped) {
                adIdSetToReturn.add(adId);
            }
        }

        return adIdSetToReturn;
    }
}
