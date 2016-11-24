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

        int siteIncId = -1;
        if(request.getSite() != null)
            siteIncId = request.getSite().getSiteIncId();

        Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
        if(externalUserIds == null) {
            externalUserIds = new HashSet<ExternalUserId>();
            request.setExternalUserIds(externalUserIds);
        }

        if(bidRequestDeviceDTO.getSHA1HashedDeviceId() != null &&
                !bidRequestDeviceDTO.getSHA1HashedDeviceId().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.SHA1_DEVICE_ID, siteIncId,
                    bidRequestDeviceDTO.getSHA1HashedDeviceId()));
        }

        if(bidRequestDeviceDTO.getMD5HashedDeviceId() != null &&
                !bidRequestDeviceDTO.getMD5HashedDeviceId().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.MD5_DEVICE_ID, siteIncId,
                    bidRequestDeviceDTO.getMD5HashedDeviceId()));
        }

        if(bidRequestDeviceDTO.getSHA1HashedDevicePlatformId() != null &&
            !bidRequestDeviceDTO.getSHA1HashedDevicePlatformId().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.SHA1_DEVICE_PLATFORM_ID, siteIncId,
                    bidRequestDeviceDTO.getSHA1HashedDevicePlatformId()));
        }

        if(bidRequestDeviceDTO.getMD5HashedDevicePlatformId() != null &&
            !bidRequestDeviceDTO.getMD5HashedDevicePlatformId().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.MD5_DEVICE_PLATFORM_ID, siteIncId,
                    bidRequestDeviceDTO.getMD5HashedDevicePlatformId()));
        }
    }

    public static void populateUserIdsFromBidRequestDeviceDTO(
            com.kritter.bidrequest.entity.common.openrtbversion2_1.BidRequestDeviceDTO bidRequestDeviceDTO,
            Request request) {
        if(null == bidRequestDeviceDTO)
            return;

        int siteIncId = -1;
        if(request.getSite() != null)
            siteIncId = request.getSite().getSiteIncId();

        Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
        if(externalUserIds == null) {
            externalUserIds = new HashSet<ExternalUserId>();
            request.setExternalUserIds(externalUserIds);
        }

        if(bidRequestDeviceDTO.getSHA1HashedDeviceId() != null &&
                !bidRequestDeviceDTO.getSHA1HashedDeviceId().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.SHA1_DEVICE_ID, siteIncId,
                    bidRequestDeviceDTO.getSHA1HashedDeviceId()));
        }

        if(bidRequestDeviceDTO.getMD5HashedDeviceId() != null &&
                !bidRequestDeviceDTO.getMD5HashedDeviceId().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.MD5_DEVICE_ID, siteIncId,
                    bidRequestDeviceDTO.getMD5HashedDeviceId()));
        }

        if(bidRequestDeviceDTO.getSHA1HashedDevicePlatformId() != null &&
                !bidRequestDeviceDTO.getSHA1HashedDevicePlatformId().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.SHA1_DEVICE_PLATFORM_ID, siteIncId,
                    bidRequestDeviceDTO.getSHA1HashedDevicePlatformId()));
        }

        if(bidRequestDeviceDTO.getMD5HashedDevicePlatformId() != null &&
                !bidRequestDeviceDTO.getMD5HashedDevicePlatformId().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.MD5_DEVICE_PLATFORM_ID, siteIncId,
                    bidRequestDeviceDTO.getMD5HashedDevicePlatformId()));
        }
    }

    public static void populateUserIdsFromBidRequestDeviceDTO(
            com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestDeviceDTO bidRequestDeviceDTO,
            Request request) {
        if(null == bidRequestDeviceDTO)
            return;

        int siteIncId = -1;
        if(request.getSite() != null)
            siteIncId = request.getSite().getSiteIncId();

        Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
        if(externalUserIds == null) {
            externalUserIds = new HashSet<ExternalUserId>();
            request.setExternalUserIds(externalUserIds);
        }

        if(bidRequestDeviceDTO.getSHA1HashedDeviceId() != null &&
                !bidRequestDeviceDTO.getSHA1HashedDeviceId().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.SHA1_DEVICE_ID, siteIncId,
                    bidRequestDeviceDTO.getSHA1HashedDeviceId()));
        }

        if(bidRequestDeviceDTO.getMD5HashedDeviceId() != null &&
                !bidRequestDeviceDTO.getMD5HashedDeviceId().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.MD5_DEVICE_ID, siteIncId,
                    bidRequestDeviceDTO.getMD5HashedDeviceId()));
        }

        if(bidRequestDeviceDTO.getSHA1HashedDevicePlatformId() != null &&
                !bidRequestDeviceDTO.getSHA1HashedDevicePlatformId().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.SHA1_DEVICE_PLATFORM_ID, siteIncId,
                    bidRequestDeviceDTO.getSHA1HashedDevicePlatformId()));
        }

        if(bidRequestDeviceDTO.getMD5HashedDevicePlatformId() != null &&
                !bidRequestDeviceDTO.getMD5HashedDevicePlatformId().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.MD5_DEVICE_PLATFORM_ID, siteIncId,
                    bidRequestDeviceDTO.getMD5HashedDevicePlatformId()));
        }

        if(bidRequestDeviceDTO.getIfa() != null && !bidRequestDeviceDTO.getIfa().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.IFA_USER_ID, siteIncId,
                    bidRequestDeviceDTO.getIfa()));
        }

        if(bidRequestDeviceDTO.getHashedSHA1MacAddressOfDevice() != null &&
                !bidRequestDeviceDTO.getHashedSHA1MacAddressOfDevice().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.MAC_SHA1_DEVICE_ID, siteIncId,
                    bidRequestDeviceDTO.getHashedSHA1MacAddressOfDevice()));
        }

        if(bidRequestDeviceDTO.getHashedMD5MacAddressOfDevice() != null &&
                !bidRequestDeviceDTO.getHashedMD5MacAddressOfDevice().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.MAC_MD5_DEVICE_ID, siteIncId,
                    bidRequestDeviceDTO.getHashedMD5MacAddressOfDevice()));
        }
    }

    public static void populateUserIdsFromBidRequestDeviceDTO(
            com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestDeviceDTO bidRequestDeviceDTO,
            Request request) {
        if(null == bidRequestDeviceDTO)
            return;

        int siteIncId = -1;
        if(request.getSite() != null)
            siteIncId = request.getSite().getSiteIncId();

        Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
        if(externalUserIds == null) {
            externalUserIds = new HashSet<ExternalUserId>();
            request.setExternalUserIds(externalUserIds);
        }

        if(bidRequestDeviceDTO.getSHA1HashedDeviceId() != null &&
                !bidRequestDeviceDTO.getSHA1HashedDeviceId().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.SHA1_DEVICE_ID, siteIncId,
                    bidRequestDeviceDTO.getSHA1HashedDeviceId()));
        }

        if(bidRequestDeviceDTO.getMD5HashedDeviceId() != null &&
                !bidRequestDeviceDTO.getMD5HashedDeviceId().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.MD5_DEVICE_ID, siteIncId,
                    bidRequestDeviceDTO.getMD5HashedDeviceId()));
        }

        if(bidRequestDeviceDTO.getSHA1HashedDevicePlatformId() != null &&
                !bidRequestDeviceDTO.getSHA1HashedDevicePlatformId().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.SHA1_DEVICE_PLATFORM_ID, siteIncId,
                    bidRequestDeviceDTO.getSHA1HashedDevicePlatformId()));
        }

        if(bidRequestDeviceDTO.getMD5HashedDevicePlatformId() != null &&
                !bidRequestDeviceDTO.getMD5HashedDevicePlatformId().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.MD5_DEVICE_PLATFORM_ID, siteIncId,
                    bidRequestDeviceDTO.getMD5HashedDevicePlatformId()));
        }

        if(bidRequestDeviceDTO.getIfa() != null && !bidRequestDeviceDTO.getIfa().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.IFA_USER_ID, siteIncId,
                    bidRequestDeviceDTO.getIfa()));
        }

        if(bidRequestDeviceDTO.getHashedSHA1MacAddressOfDevice() != null &&
                !bidRequestDeviceDTO.getHashedSHA1MacAddressOfDevice().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.MAC_SHA1_DEVICE_ID, siteIncId,
                    bidRequestDeviceDTO.getHashedSHA1MacAddressOfDevice()));
        }

        if(bidRequestDeviceDTO.getHashedMD5MacAddressOfDevice() != null &&
                !bidRequestDeviceDTO.getHashedMD5MacAddressOfDevice().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.MAC_MD5_DEVICE_ID, siteIncId,
                    bidRequestDeviceDTO.getHashedMD5MacAddressOfDevice()));
        }
        if(bidRequestDeviceDTO.getAaid() != null &&
                !bidRequestDeviceDTO.getAaid().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.AAID, siteIncId,
                    bidRequestDeviceDTO.getAaid()));
        }
        if(bidRequestDeviceDTO.getAaidMd5() != null &&
                !bidRequestDeviceDTO.getAaidMd5().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.AAIDMD5, siteIncId,
                    bidRequestDeviceDTO.getAaidMd5()));
        }
        if(bidRequestDeviceDTO.getOpenUDID() != null &&
                !bidRequestDeviceDTO.getOpenUDID().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.OpenUDIDMD5, siteIncId,
                    bidRequestDeviceDTO.getOpenUDID()));
        }
        if(bidRequestDeviceDTO.getOpenUDIDMd5() != null &&
                !bidRequestDeviceDTO.getOpenUDIDMd5().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.OpenUDIDMD5, siteIncId,
                    bidRequestDeviceDTO.getOpenUDIDMd5()));
        }
    }

    public static void populateUserIdsFromBidRequestUserDTO(
            com.kritter.bidrequest.entity.common.openrtb2_0.BidRequestUserDTO bidRequestUserDTO, Request request) {
        if(null == bidRequestUserDTO)
            return;

        int siteIncId = -1;
        if(request.getSite() != null)
            siteIncId = request.getSite().getSiteIncId();

        request.setBuyerUserId(bidRequestUserDTO.getUniqueConsumerIdMappedForBuyer());

        Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
        if(externalUserIds == null) {
            externalUserIds = new HashSet<ExternalUserId>();
            request.setExternalUserIds(externalUserIds);
        }

        if(bidRequestUserDTO.getUniqueConsumerIdOnExchange() != null &&
            !bidRequestUserDTO.getUniqueConsumerIdOnExchange().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.EXCHANGE_CONSUMER_ID, siteIncId,
                    bidRequestUserDTO.getUniqueConsumerIdOnExchange()));
        }

        if(bidRequestUserDTO.getUniqueConsumerIdMappedForBuyer() != null &&
            !bidRequestUserDTO.getUniqueConsumerIdMappedForBuyer().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.BUYER_USER_ID, siteIncId,
                    bidRequestUserDTO.getUniqueConsumerIdMappedForBuyer()));
        }
    }

    public static void populateUserIdsFromBidRequestUserDTO(
            com.kritter.bidrequest.entity.common.openrtbversion2_1.BidRequestUserDTO bidRequestUserDTO,
            Request request) {
        if(null == bidRequestUserDTO)
            return;

        int siteIncId = -1;
        if(request.getSite() != null)
            siteIncId = request.getSite().getSiteIncId();

        request.setBuyerUserId(bidRequestUserDTO.getUniqueConsumerIdMappedForBuyer());

        Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
        if(externalUserIds == null) {
            externalUserIds = new HashSet<ExternalUserId>();
            request.setExternalUserIds(externalUserIds);
        }

        if(bidRequestUserDTO.getUniqueConsumerIdOnExchange() != null &&
                !bidRequestUserDTO.getUniqueConsumerIdOnExchange().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.EXCHANGE_CONSUMER_ID, siteIncId,
                    bidRequestUserDTO.getUniqueConsumerIdOnExchange()));
        }

        if(bidRequestUserDTO.getUniqueConsumerIdMappedForBuyer() != null &&
                !bidRequestUserDTO.getUniqueConsumerIdMappedForBuyer().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.BUYER_USER_ID, siteIncId,
                    bidRequestUserDTO.getUniqueConsumerIdMappedForBuyer()));
        }
    }

    public static void populateUserIdsFromBidRequestUserDTO(
            com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestUserDTO bidRequestUserDTO,
            Request request) {
        if(null == bidRequestUserDTO)
            return;

        int siteIncId = -1;
        if(request.getSite() != null)
            siteIncId = request.getSite().getSiteIncId();

        request.setBuyerUserId(bidRequestUserDTO.getUniqueConsumerIdMappedForBuyer());

        Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
        if(externalUserIds == null) {
            externalUserIds = new HashSet<ExternalUserId>();
            request.setExternalUserIds(externalUserIds);
        }

        if(bidRequestUserDTO.getUniqueConsumerIdOnExchange() != null &&
                !bidRequestUserDTO.getUniqueConsumerIdOnExchange().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.EXCHANGE_CONSUMER_ID, siteIncId,
                    bidRequestUserDTO.getUniqueConsumerIdOnExchange()));
        }

        if(bidRequestUserDTO.getUniqueConsumerIdMappedForBuyer() != null &&
                !bidRequestUserDTO.getUniqueConsumerIdMappedForBuyer().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.BUYER_USER_ID, siteIncId,
                    bidRequestUserDTO.getUniqueConsumerIdMappedForBuyer()));
        }
    }

    public static void populateUserIdsFromBidRequestUserDTO(
            com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestUserDTO bidRequestUserDTO,
            Request request) {
        if(null == bidRequestUserDTO)
            return;

        int siteIncId = -1;
        if(request.getSite() != null)
            siteIncId = request.getSite().getSiteIncId();

        request.setBuyerUserId(bidRequestUserDTO.getUniqueConsumerIdMappedForBuyer());


        Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
        if(externalUserIds == null) {
            externalUserIds = new HashSet<ExternalUserId>();
            request.setExternalUserIds(externalUserIds);
        }

        if(bidRequestUserDTO.getUniqueConsumerIdOnExchange() != null &&
                !bidRequestUserDTO.getUniqueConsumerIdOnExchange().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.EXCHANGE_CONSUMER_ID, siteIncId,
                    bidRequestUserDTO.getUniqueConsumerIdOnExchange()));
        }

        if(bidRequestUserDTO.getUniqueConsumerIdMappedForBuyer() != null &&
                !bidRequestUserDTO.getUniqueConsumerIdMappedForBuyer().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.BUYER_USER_ID, siteIncId,
                    bidRequestUserDTO.getUniqueConsumerIdMappedForBuyer()));
        }
    }
}
