package com.kritter.adserving.shortlisting.core.directhelper;

import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.entity.video_props.VideoProps;
import com.kritter.entity.video_supply_props.VideoSupplyProps;

public class ValidateVideoDirect {
	public static NoFillReason validate(VideoSupplyProps supply, VideoProps demand){
		if(demand == null){
			return NoFillReason.VIDEO_DEMAND_PROPS_NF;
		}
		if(supply == null){
			return NoFillReason.VIDEO_SUPPLY_PROPS_NF;
		}
		if(supply.getMimes() != null){
			if(!supply.getMimes().contains(demand.getMime())){
				return NoFillReason.Video_Mime;
			}
		}
		if(supply.getProtocols() != null){
			if(!supply.getProtocols().contains(demand.getProtocol())){
				return NoFillReason.Video_Protocol;
			}			
		}
		if(supply.getMinDurationSec() != null){
			if(demand.getDuration() == -1){
				return NoFillReason.Video_Duration;
			}
			if(demand.getDuration() < supply.getMinDurationSec()){
				return NoFillReason.Video_Duration;
			}
		}
		if(supply.getMaxDurationSec() != null){
			if(demand.getDuration() == -1){
				return NoFillReason.Video_Duration;
			}
			if(demand.getDuration() > supply.getMaxDurationSec()){
				return NoFillReason.Video_Duration;
			}
		}
		if(supply.getWidthPixel() != null){
			if(demand.getWidth() == -1){
				return NoFillReason.Video_Width;
			}
			if(supply.getWidthPixel() != demand.getWidth()){
				return NoFillReason.Video_Width;
			}			
		}
		if(supply.getHeightPixel() != null){
			if(demand.getHeight() == -1){
				return NoFillReason.Video_Height;
			}
			if(supply.getHeightPixel() != demand.getHeight()){
				return NoFillReason.Video_Height;
			}			
		}
		if(supply.getStartDelay() != null){
			int s =supply.getStartDelay();
			int d = demand.getStartdelay();
			if(s != d){
				if(s<1 || d<s){
					return NoFillReason.Video_StartDelay;
				}
			}
		}
		if(supply.getLinearity() != null){
			if(demand.getLinearity() != supply.getLinearity() ){
				return NoFillReason.Video_Linearity;
			}
		}
		if(supply.getMaxextended() != null){
			int s =supply.getMaxextended();
			int d = demand.getMaxextended();
			if(s!=d){
				if(d>0 && s<d){
					return NoFillReason.Video_MaxExtended;
				}
			}
		}
		if(supply.getMinBitrate() != null){
			if(demand.getBitrate() == -1){
				return NoFillReason.Video_BitRate;
			}
			if(demand.getBitrate() < supply.getMinBitrate()){
				return NoFillReason.Video_BitRate;
			}
		}
		if(supply.getMaxBitrate() != null){
			if(demand.getBitrate() == -1){
				return NoFillReason.Video_BitRate;
			}
			if(demand.getBitrate() > supply.getMaxBitrate()){
				return NoFillReason.Video_BitRate;
			}
		}
		if(supply.getBoxingallowed() != null){
			if(demand.getBoxingallowed() != supply.getBoxingallowed()){
				return NoFillReason.Video_Boxing;
			}
		}
		if(supply.getPlaybackmethod() != null){
			if(!supply.getPlaybackmethod().contains(demand.getPlaybackmethod())){
				return NoFillReason.Video_PlayBack;
			}			
		}
		if(supply.getDelivery() != null){
			if(!supply.getDelivery().contains(demand.getDelivery())){
				return NoFillReason.Video_Delivery;
			}			
		}
		if(supply.getDelivery() != null){
			if(!supply.getDelivery().contains(demand.getDelivery())){
				return NoFillReason.Video_Delivery;
			}			
		}
		if(supply.getApi() != null){
			if(!supply.getApi().contains(demand.getApi())){
				return NoFillReason.Video_ApiFramework;
			}			
		}
		return NoFillReason.FILL;
	}
}
