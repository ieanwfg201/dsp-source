package com.kritter.exchange.request_openrtb_2_2.converter.common;

import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestImpressionBannerObjectDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestImpressionDTO;
import com.kritter.common.caches.account.entity.AccountEntity;
import com.kritter.constants.ConvertErrorEnum;
import com.kritter.constants.CreativeAttribute;
import com.kritter.entity.reqres.entity.Request;
import org.apache.commons.lang.ArrayUtils;

public class ConvertBidRequestImpBanner {
    public static ConvertErrorEnum convert(Request request, BidRequestImpressionDTO bidRequestImpressionDTO, int version,
            AccountEntity accountEntity){
        if(bidRequestImpressionDTO == null){
            return ConvertErrorEnum.BID_IMP_NULL;
        }
        if(request.getSite() == null){
            return ConvertErrorEnum.REQ_SITE_NF;
        }
        BidRequestImpressionBannerObjectDTO banner = new BidRequestImpressionBannerObjectDTO();
        
        if(request.getRequestedSlotWidths()== null || request.getRequestedSlotWidths().length < 1){
            return ConvertErrorEnum.REQ_WIDTH_NF;
        }
        if(request.getRequestedSlotHeights()== null || request.getRequestedSlotHeights().length < 1){
            return ConvertErrorEnum.REQ_HEIGHT_NF;
        }
        banner.setBannerWidthInPixels(request.getRequestedSlotWidths()[0]);
        banner.setMaxBannerWidthInPixels(request.getRequestedSlotWidths()[0]);
        banner.setBannerHeightInPixels(request.getRequestedSlotHeights()[0]);
        banner.setMaxBannerHeightInPixels(request.getRequestedSlotHeights()[0]);
        banner.setBannerUniqueId(request.getRequestId());
        if(accountEntity.getBtype() != null){
            banner.setBlockedCreativeTypes(accountEntity.getBtype());
        }else{
            banner.setBlockedCreativeTypes(new Short[0]);
        }
        if(request.getSite().isCreativeAttributesForExclusion()){
            Short[] battr =  request.getSite().getCreativeAttributesForInclusionExclusion();
            if(battr== null){
                banner.setBlockedCreativeAttributes(new Short[0]);;
            }else{
                ArrayUtils.removeElement(battr, new Short((short)CreativeAttribute.EXTRA.getCode()));
                ArrayUtils.removeElement(battr, new Short((short)CreativeAttribute.Native.getCode()));
                banner.setBlockedCreativeAttributes(battr);;
            }
        }else{
            banner.setBlockedCreativeAttributes(new Short[0]);;
        }
        
        bidRequestImpressionDTO.setBidRequestImpressionBannerObject(banner);
        return ConvertErrorEnum.HEALTHY_CONVERT;
    }

}
