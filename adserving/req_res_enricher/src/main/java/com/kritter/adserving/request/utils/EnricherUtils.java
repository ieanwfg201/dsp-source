package com.kritter.adserving.request.utils;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.constants.ExternalUserIdType;

import java.util.HashSet;
import java.util.Set;

/**
 * Class containing utility functions for request enrichers
 */
public class EnricherUtils {
    public static void populateUserIdsFromBidRequestDeviceDTO(
            com.kritter.bidrequest.entity.common.openrtb2_0.BidRequestDeviceDTO bidRequestDeviceDTO,
            Request request) {
        if(null == bidRequestDeviceDTO)
            return;

        Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
        if(externalUserIds == null) {
            externalUserIds = new HashSet<ExternalUserId>();
            request.setExternalUserIds(externalUserIds);
        }

        if(bidRequestDeviceDTO.getSHA1HashedDeviceId() != null) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.SHA1_DEVICE_ID, request.getInventorySource(),
                    bidRequestDeviceDTO.getSHA1HashedDeviceId()));
        }

        if(bidRequestDeviceDTO.getMD5HashedDeviceId() != null) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.MD5_DEVICE_ID, request.getInventorySource(),
                    bidRequestDeviceDTO.getMD5HashedDeviceId()));
        }

        if(bidRequestDeviceDTO.getSHA1HashedDevicePlatformId() != null) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.SHA1_DEVICE_PLATFORM_ID,
                    request.getInventorySource(), bidRequestDeviceDTO.getSHA1HashedDevicePlatformId()));
        }

        if(bidRequestDeviceDTO.getMD5HashedDevicePlatformId() != null) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.MD5_DEVICE_PLATFORM_ID,
                    request.getInventorySource(), bidRequestDeviceDTO.getMD5HashedDevicePlatformId()));
        }
    }

    public static void populateUserIdsFromBidRequestDeviceDTO(
            com.kritter.bidrequest.entity.common.openrtbversion2_1.BidRequestDeviceDTO bidRequestDeviceDTO,
            Request request) {
        if(null == bidRequestDeviceDTO)
            return;

        Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
        if(externalUserIds == null) {
            externalUserIds = new HashSet<ExternalUserId>();
            request.setExternalUserIds(externalUserIds);
        }

        if(bidRequestDeviceDTO.getSHA1HashedDeviceId() != null) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.SHA1_DEVICE_ID, request.getInventorySource(),
                    bidRequestDeviceDTO.getSHA1HashedDeviceId()));
        }

        if(bidRequestDeviceDTO.getMD5HashedDeviceId() != null) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.MD5_DEVICE_ID, request.getInventorySource(),
                    bidRequestDeviceDTO.getMD5HashedDeviceId()));
        }

        if(bidRequestDeviceDTO.getSHA1HashedDevicePlatformId() != null) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.SHA1_DEVICE_PLATFORM_ID,
                    request.getInventorySource(), bidRequestDeviceDTO.getSHA1HashedDevicePlatformId()));
        }

        if(bidRequestDeviceDTO.getMD5HashedDevicePlatformId() != null) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.MD5_DEVICE_PLATFORM_ID,
                    request.getInventorySource(), bidRequestDeviceDTO.getMD5HashedDevicePlatformId()));
        }
    }

    public static void populateUserIdsFromBidRequestDeviceDTO(
            com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestDeviceDTO bidRequestDeviceDTO,
            Request request) {
        if(null == bidRequestDeviceDTO)
            return;

        Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
        if(externalUserIds == null) {
            externalUserIds = new HashSet<ExternalUserId>();
            request.setExternalUserIds(externalUserIds);
        }

        if(bidRequestDeviceDTO.getSHA1HashedDeviceId() != null) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.SHA1_DEVICE_ID, request.getInventorySource(),
                    bidRequestDeviceDTO.getSHA1HashedDeviceId()));
        }

        if(bidRequestDeviceDTO.getMD5HashedDeviceId() != null) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.MD5_DEVICE_ID, request.getInventorySource(),
                    bidRequestDeviceDTO.getMD5HashedDeviceId()));
        }

        if(bidRequestDeviceDTO.getSHA1HashedDevicePlatformId() != null) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.SHA1_DEVICE_PLATFORM_ID,
                    request.getInventorySource(), bidRequestDeviceDTO.getSHA1HashedDevicePlatformId()));
        }

        if(bidRequestDeviceDTO.getMD5HashedDevicePlatformId() != null) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.MD5_DEVICE_PLATFORM_ID,
                    request.getInventorySource(), bidRequestDeviceDTO.getMD5HashedDevicePlatformId()));
        }
    }

    public static void populateUserIdsFromBidRequestDeviceDTO(
            com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestDeviceDTO bidRequestDeviceDTO,
            Request request) {
        if(null == bidRequestDeviceDTO)
            return;

        Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
        if(externalUserIds == null) {
            externalUserIds = new HashSet<ExternalUserId>();
            request.setExternalUserIds(externalUserIds);
        }

        if(bidRequestDeviceDTO.getSHA1HashedDeviceId() != null) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.SHA1_DEVICE_ID, request.getInventorySource(),
                    bidRequestDeviceDTO.getSHA1HashedDeviceId()));
        }

        if(bidRequestDeviceDTO.getMD5HashedDeviceId() != null) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.MD5_DEVICE_ID, request.getInventorySource(),
                    bidRequestDeviceDTO.getMD5HashedDeviceId()));
        }

        if(bidRequestDeviceDTO.getSHA1HashedDevicePlatformId() != null) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.SHA1_DEVICE_PLATFORM_ID,
                    request.getInventorySource(), bidRequestDeviceDTO.getSHA1HashedDevicePlatformId()));
        }

        if(bidRequestDeviceDTO.getMD5HashedDevicePlatformId() != null) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.MD5_DEVICE_PLATFORM_ID,
                    request.getInventorySource(), bidRequestDeviceDTO.getMD5HashedDevicePlatformId()));
        }
    }

    public static void populateUserIdsFromBidRequestUserDTO(
            com.kritter.bidrequest.entity.common.openrtb2_0.BidRequestUserDTO bidRequestUserDTO, Request request) {
        if(null == bidRequestUserDTO)
            return;

        request.setUserId(bidRequestUserDTO.getUniqueConsumerIdOnExchange());
        request.setBuyerUserId(bidRequestUserDTO.getUniqueConsumerIdMappedForBuyer());

        Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
        if(externalUserIds == null) {
            externalUserIds = new HashSet<ExternalUserId>();
            request.setExternalUserIds(externalUserIds);
        }

        if(bidRequestUserDTO.getUniqueConsumerIdOnExchange() != null) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.EXCHANGE_CONSUMER_ID,
                    request.getInventorySource(), bidRequestUserDTO.getUniqueConsumerIdOnExchange()));
        }

        if(bidRequestUserDTO.getUniqueConsumerIdMappedForBuyer() != null) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.BUYER_USER_ID, request.getInventorySource(),
                    bidRequestUserDTO.getUniqueConsumerIdMappedForBuyer()));
        }
    }

    public static void populateUserIdsFromBidRequestUserDTO(
            com.kritter.bidrequest.entity.common.openrtbversion2_1.BidRequestUserDTO bidRequestUserDTO,
            Request request) {
        if(null == bidRequestUserDTO)
            return;

        request.setUserId(bidRequestUserDTO.getUniqueConsumerIdOnExchange());
        request.setBuyerUserId(bidRequestUserDTO.getUniqueConsumerIdMappedForBuyer());


        Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
        if(externalUserIds == null) {
            externalUserIds = new HashSet<ExternalUserId>();
            request.setExternalUserIds(externalUserIds);
        }

        if(bidRequestUserDTO.getUniqueConsumerIdOnExchange() != null) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.EXCHANGE_CONSUMER_ID,
                    request.getInventorySource(), bidRequestUserDTO.getUniqueConsumerIdOnExchange()));
        }

        if(bidRequestUserDTO.getUniqueConsumerIdMappedForBuyer() != null) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.BUYER_USER_ID, request.getInventorySource(),
                    bidRequestUserDTO.getUniqueConsumerIdMappedForBuyer()));
        }
    }

    public static void populateUserIdsFromBidRequestUserDTO(
            com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestUserDTO bidRequestUserDTO,
            Request request) {
        if(null == bidRequestUserDTO)
            return;

        request.setUserId(bidRequestUserDTO.getUniqueConsumerIdOnExchange());
        request.setBuyerUserId(bidRequestUserDTO.getUniqueConsumerIdMappedForBuyer());


        Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
        if(externalUserIds == null) {
            externalUserIds = new HashSet<ExternalUserId>();
            request.setExternalUserIds(externalUserIds);
        }

        if(bidRequestUserDTO.getUniqueConsumerIdOnExchange() != null) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.EXCHANGE_CONSUMER_ID,
                    request.getInventorySource(), bidRequestUserDTO.getUniqueConsumerIdOnExchange()));
        }

        if(bidRequestUserDTO.getUniqueConsumerIdMappedForBuyer() != null) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.BUYER_USER_ID, request.getInventorySource(),
                    bidRequestUserDTO.getUniqueConsumerIdMappedForBuyer()));
        }
    }

    public static void populateUserIdsFromBidRequestUserDTO(
            com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestUserDTO bidRequestUserDTO,
            Request request) {
        if(null == bidRequestUserDTO)
            return;

        request.setUserId(bidRequestUserDTO.getUniqueConsumerIdOnExchange());
        request.setBuyerUserId(bidRequestUserDTO.getUniqueConsumerIdMappedForBuyer());


        Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
        if(externalUserIds == null) {
            externalUserIds = new HashSet<ExternalUserId>();
            request.setExternalUserIds(externalUserIds);
        }

        if(bidRequestUserDTO.getUniqueConsumerIdOnExchange() != null) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.EXCHANGE_CONSUMER_ID,
                    request.getInventorySource(), bidRequestUserDTO.getUniqueConsumerIdOnExchange()));
        }

        if(bidRequestUserDTO.getUniqueConsumerIdMappedForBuyer() != null) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.BUYER_USER_ID, request.getInventorySource(),
                    bidRequestUserDTO.getUniqueConsumerIdMappedForBuyer()));
        }
    }
}
