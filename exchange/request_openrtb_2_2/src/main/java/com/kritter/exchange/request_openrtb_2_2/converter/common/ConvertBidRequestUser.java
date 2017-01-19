package com.kritter.exchange.request_openrtb_2_2.converter.common;

import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestParentNodeDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestUserDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestGeoDTO;
import com.kritter.common.caches.account.entity.AccountEntity;
import com.kritter.constants.ConvertErrorEnum;
import com.kritter.constants.ExternalUserIdType;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.user.userid.ExternalUserId;

public class ConvertBidRequestUser {
    public static ConvertErrorEnum convert(Request request, BidRequestParentNodeDTO bidRequest, int version,
                                           AccountEntity dspEntity) {
        BidRequestUserDTO user = new BidRequestUserDTO();
        for(ExternalUserId externalUserId : request.getExternalUserIds()) {
            if(externalUserId.getIdType() == ExternalUserIdType.DSPBUYERUID
                    && externalUserId.getSource() == dspEntity.getAccountId()) {
                user.setUniqueConsumerIdMappedForBuyer(externalUserId.getUserId());
            }
        }

        /**Enrich user object more for better decision making by DSP for better bids.*/
        if(null != request.getBidRequestUserGender())
            user.setGender(request.getBidRequestUserGender());
        if(null != request.getBidRequestUserKeywords())
            user.setConsumerInterestsCSV(request.getBidRequestUserKeywords());
        if(null != request.getBidRequestUserCountry())
        {
            BidRequestGeoDTO geoDTO = new BidRequestGeoDTO();
            geoDTO.setCountry(request.getBidRequestUserCountry());
            user.setBidRequestGeo(geoDTO);
        }
        user.setYearOfBirth(request.getBidRequestUserYOB());
        /**Enriched user object for more information provision to DSPs.*/

        ConvertBidRequestData.convert(request, user, version);
        bidRequest.setBidRequestUser(user);
        return ConvertErrorEnum.HEALTHY_CONVERT;
    }
}
