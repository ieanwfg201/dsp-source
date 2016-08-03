package com.kritter.ex_int.native_admarkup;

import java.util.ArrayList;
import java.util.Set;

import com.kritter.constants.*;
import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.utils.common.ApplicationGeneralUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.slf4j.Logger;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.native1_0.resp.RespAsset;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.native1_0.resp.RespData;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.native1_0.resp.RespImage;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.native1_0.resp.RespLink;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.native1_0.resp.RespNativeFirstLevel;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.native1_0.resp.RespNativeParent;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.native1_0.resp.RespTitle;
import com.kritter.bidrequest.exception.BidResponseException;
import com.kritter.entity.native_props.demand.NativeDemandProps;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.formatterutil.CreativeFormatterUtils;
import com.kritter.serving.demand.entity.Creative;

public class NativeAdMarkUp {

    public static String prepare(
            Request request,
            ResponseAdInfo responseAdInfo,
            Response response,
            StringBuffer winNotificationURLBuffer,
            Logger logger, int urlVersion, String secretKey, String postImpressionBaseClickUrl, 
            String postImpressionBaseWinApiUrl, String notificationUrlSuffix, 
            String notificationUrlBidderBidPriceMacro, String postImpressionBaseCSCUrl, 
            String cdnBaseImageUrl
    )throws BidResponseException{

        String clickUri = CreativeFormatterUtils.prepareClickUri
                (
                        logger,
                        request,
                        responseAdInfo,
                        response.getBidderModelId(),
                        urlVersion,
                        request.getInventorySource(),
                        response.getSelectedSiteCategoryId(),
                        secretKey
                        );

        if(null == clickUri)
            throw new BidResponseException("Click URI could not be formed using different attributes like " +
                    "handset,location,bids,version,etc. inside BidRequestResponseCreator");

        StringBuffer clickUrl = new StringBuffer(postImpressionBaseClickUrl);
        clickUrl.append(clickUri);

        /*********prepare win notification url , also include bidder price.****************/
        winNotificationURLBuffer.append(postImpressionBaseWinApiUrl);
        winNotificationURLBuffer.append(clickUri);
        String suffixToAdd = notificationUrlSuffix;
        suffixToAdd = suffixToAdd.replace(
                notificationUrlBidderBidPriceMacro,
                String.valueOf(responseAdInfo.getEcpmValue())
                );
        winNotificationURLBuffer.append(suffixToAdd);
        /*********done preparing win notification url, included bidder price as well*******/

        //set common post impression uri to be used in any type of post impression url.
        responseAdInfo.setCommonURIForPostImpression(clickUri);

        StringBuffer cscBeaconUrl = new StringBuffer(postImpressionBaseCSCUrl);
        cscBeaconUrl.append(clickUri);

        /**modify csc url to have bid-switch exchangeId as a parameter for usage in post-impression server************/
        logger.debug("Going to modify CSC URL if request has user ids available, for kritterUserId:{} ",
                request.getUserId());

        Set<ExternalUserId> externalUserIdSet = request.getExternalUserIds();
        String exchangeUserId = null;
        if(null != externalUserIdSet)
        {
            for(ExternalUserId externalUserId : externalUserIdSet)
            {
                if(externalUserId.getIdType().equals(ExternalUserIdType.EXCHANGE_CONSUMER_ID))
                    exchangeUserId = externalUserId.toString();
            }
        }

        cscBeaconUrl = new StringBuffer(ApplicationGeneralUtils.modifyCSCURLForUserIds(
                exchangeUserId,
                request.getUserId(),
                cscBeaconUrl.toString())
        );

        logger.debug("CSC url is modified to contain exchange and kritter UserId, after modification url:{} ",
                cscBeaconUrl.toString());

        /**************************modifying csc url completed********************************************************/

        Creative creative = responseAdInfo.getCreative();

        if(!creative.getCreativeFormat().equals(CreativeFormat.Native)){
            logger.error("Creative is not native inside BidRequestResponseCreator,adId:{} ",
                    responseAdInfo.getAdId());
            return null;
        }

        NativeDemandProps nativeDemandProps = responseAdInfo.getNativeDemandProps();
        RespNativeParent respNativeParent = new RespNativeParent();
        RespNativeFirstLevel respNative = new RespNativeFirstLevel();
        String[] imptrackers = new String[1];
        imptrackers[0] = cscBeaconUrl.toString();
        respNative.setImptrackers(imptrackers);
        respNative.setVer(1);
        ArrayList<RespAsset> respAssetList = new ArrayList<RespAsset>();
        String[] clicktrackers = new String[1];
        clicktrackers[0] = clickUrl.toString().toString();
        RespLink respLink = new RespLink();
        respLink.setClicktrackers(clicktrackers);
        RespAsset clickRespAsset = new RespAsset();
        clickRespAsset.setLink(respLink);
        respAssetList.add(clickRespAsset);
        if(nativeDemandProps.getTitle() != null){
            RespAsset respAsset = new RespAsset();
            RespTitle title = new RespTitle();
            title.setText(nativeDemandProps.getTitle());
            respAsset.setTitle(title);
            respAsset.setId(NativeAssetId.Title.getCode());
            respAssetList.add(respAsset);
        }
        if(nativeDemandProps.getDesc() != null){
            RespAsset respAsset = new RespAsset();
            RespData data = new RespData();
            data.setLabel(NativeDataAssetType.desc.getName());
            data.setValue(nativeDemandProps.getDesc());
            respAsset.setData(data);
            respAsset.setId(NativeAssetId.Desc.getCode());
            respAssetList.add(respAsset);
        }
        if(responseAdInfo.getNativeIcon() != null){
            StringBuffer iconImageUrl = new StringBuffer(cdnBaseImageUrl);
            iconImageUrl.append(responseAdInfo.getNativeIcon().getResource_uri());
            RespAsset respAsset = new RespAsset();
            RespImage respImage = new RespImage();
            respImage.setUrl(iconImageUrl.toString());
            String s = NativeIconImageSize.getEnum(responseAdInfo.getNativeIcon().getIcon_size()).getName();
            String sSplit[] = s.split("\\*");
            respImage.setW(Integer.parseInt(sSplit[0]));
            respImage.setH(Integer.parseInt(sSplit[1]));
            respAsset.setImg(respImage);
            respAsset.setId(NativeAssetId.Icon.getCode());
            respAssetList.add(respAsset);
        }
        if(responseAdInfo.getNativeScreenshot() != null){
            StringBuffer screenshotUrl = new StringBuffer(cdnBaseImageUrl);
            screenshotUrl.append(responseAdInfo.getNativeScreenshot().getResource_uri());
            RespAsset respAsset = new RespAsset();
            RespImage respImage = new RespImage();
            respImage.setUrl(screenshotUrl.toString());
            String s = NativeScreenShotImageSize.getEnum(responseAdInfo.getNativeScreenshot().getSs_size()).getName();
            String sSplit[] = s.split("\\*");
            respImage.setW(Integer.parseInt(sSplit[0]));
            respImage.setH(Integer.parseInt(sSplit[1]));
            respAsset.setImg(respImage);
            respAsset.setId(NativeAssetId.Screenshot.getCode());
            respAssetList.add(respAsset);
        }
        
        RespAsset[] stockArr = new RespAsset[respAssetList.size()];
        stockArr = respAssetList.toArray(stockArr);
        respNative.setAssets(stockArr);
        respNativeParent.setRespNativeForstLevel(respNative);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        JsonNode jsonNode = objectMapper.valueToTree(respNativeParent);
        return jsonNode.toString();

    }
}
