package com.kritter.tencent.reader_v20150313.converter.request;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestUserDTO;

import RTB.Tencent.Request.User;

public class ConvertUser {

/**
 *     message User {
        optional string id = 1; //tuid,tencent user id
        optional string buyerid=2; //dsp user id matched with cookie mapping.
        optional string gender = 3; //(Not in use at present)M: male; F: female; NULL: unknown.
        optional uint32 age = 4; //(Not in use at present)0: 0-20; 1:21-30; 2: 31-40; 3: 41-50; 4: 51-60; 5: 60+.
    }
 */
    public static BidRequestUserDTO convert(User user){
        if(user == null){
            return null;
        }
        BidRequestUserDTO openrtbUser = new BidRequestUserDTO();
        if(user.hasId()){
            openrtbUser.setUniqueConsumerIdOnExchange(user.getId());
        }
        if(user.hasBuyerid()){
            openrtbUser.setUniqueConsumerIdOnExchange(user.getBuyerid());
        }
        /** TODO Implement when in use
        if(user.getGender() != null){
            openrtbUser.setGender(user.getGender());
        }
        */
        /** TODO Implement when in use
            user.getAge()
        */
        return openrtbUser;
    }
}
