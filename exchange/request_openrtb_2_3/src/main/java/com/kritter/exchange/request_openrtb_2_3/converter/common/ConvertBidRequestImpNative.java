package com.kritter.exchange.request_openrtb_2_3.converter.common;

import com.kritter.entity.native_props.NativeProps;
import com.kritter.entity.reqres.entity.Request;

import java.util.ArrayList;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionNativeObjectDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.native1_0.req.Asset;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.native1_0.req.Data;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.native1_0.req.Image;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.native1_0.req.Native;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.native1_0.req.Title;
import com.kritter.common.site.entity.Site;
import com.kritter.constants.ConvertErrorEnum;
import com.kritter.constants.NativeAssetId;
import com.kritter.constants.NativeDataAssetType;
import com.kritter.constants.NativeIconImageSize;
import com.kritter.constants.NativeImageAssetType;
import com.kritter.constants.NativeScreenShotImageSize;

public class ConvertBidRequestImpNative {
    public static ConvertErrorEnum convert(Request request, BidRequestImpressionDTO bidRequestImpressionDTO, int version){
        Site site = request.getSite();
        if(site == null){
            return ConvertErrorEnum.REQ_SITE_NF;
        }
        if(!site.isNative()){
            return ConvertErrorEnum.HEALTHY_CONVERT;
        }
        if(site.getNativeProps() == null){
            return ConvertErrorEnum.REQ_NATIVE_PROP_NF;
        }
            NativeProps nativeSupplyProps = site.getNativeProps();
            Native nativeObj = new Native();
            nativeObj.setLayout(nativeSupplyProps.getLayout());
            //Asset
            ArrayList<Asset> assetList = new ArrayList<Asset>();
            if(nativeSupplyProps.getTitle_maxchars() != null){
                Asset asset = new Asset();
                Title title = new Title();
                title.setLen(nativeSupplyProps.getTitle_maxchars());
                asset.setTitle(title);
                asset.setId(NativeAssetId.Title.getCode());
                asset.setRequired(1);
                assetList.add(asset);
            }
            if(nativeSupplyProps.getIcon_imagesize() != null){
                String s = NativeIconImageSize.getEnum(nativeSupplyProps.getIcon_imagesize()).getName();
                String str[] = s.split("\\*");
                Asset asset = new Asset();
                Image img = new Image();
                img.setH(Integer.parseInt(str[1]));
                img.setW(Integer.parseInt(str[0]));
                img.setType(NativeImageAssetType.Icon.getCode());
                asset.setImg(img);
                asset.setId(NativeAssetId.Icon.getCode());
                asset.setRequired(1);
                assetList.add(asset);
            }
            if(nativeSupplyProps.getScreenshot_imagesize() != null || nativeSupplyProps.getScreenshot_size() != null){
                String s = null;

                if(null != nativeSupplyProps.getScreenshot_imagesize())
                    s = NativeScreenShotImageSize.getEnum(nativeSupplyProps.getScreenshot_imagesize()).getName();

                if(null == s)
                    s = nativeSupplyProps.getScreenshot_size();

                String str[] = s.split("\\*");
                Asset asset = new Asset();
                Image img = new Image();
                img.setH(Integer.parseInt(str[1]));
                img.setW(Integer.parseInt(str[0]));
                img.setType(NativeImageAssetType.Main.getCode());
                asset.setImg(img);
                asset.setId(NativeAssetId.Screenshot.getCode());
                asset.setRequired(1);
                assetList.add(asset);
            }
            if(nativeSupplyProps.getDescription_maxchars() != null){
                Asset asset = new Asset();
                Data data = new Data();
                data.setLen(nativeSupplyProps.getDescription_maxchars());
                data.setType(NativeDataAssetType.desc.getCode());
                asset.setData(data);
                asset.setId(NativeAssetId.Desc.getCode());
                asset.setRequired(1);
                assetList.add(asset);
            }
            Asset[] stockArr = new Asset[assetList.size()];
            stockArr = assetList.toArray(stockArr);
            nativeObj.setAssets(stockArr);
            BidRequestImpressionNativeObjectDTO bnative = new BidRequestImpressionNativeObjectDTO();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
            JsonNode jsonNode = objectMapper.valueToTree(nativeObj);
            bnative.setRequest(jsonNode.toString());
            bidRequestImpressionDTO.setBidRequestImpressionNativeObjectDTO(bnative);

        return ConvertErrorEnum.HEALTHY_CONVERT;
    }
}
