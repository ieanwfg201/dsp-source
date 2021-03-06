package com.kritter.entity.video_supply_props;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.kritter.constants.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class VideoSupplyProps {
	/**com.kritter.constants.VideoMimeTypes*/
	@Getter@Setter
	private HashSet<Integer> mimes;
	/**com.kritter.constants.VideoBidResponseProtocols*/
	@Getter@Setter
	private HashSet<Integer> protocols;
	@Getter@Setter
	private Integer minDurationSec;
	@Getter@Setter
	private Integer maxDurationSec;
	@Getter@Setter
	private Integer widthPixel;
	@Getter@Setter
	private Integer heightPixel;
	/**com.kritter.constants.VideoStartDelay*/
	@Getter@Setter
	private Integer startDelay;
	/**com.kritter.constants.VideoLinearity*/
	@Getter@Setter
	private Integer linearity;
	/**
	 * Maximum extended video ad duration if extension is allowed. 
	 * If blank or 0, extension is not allowed. If -1, extension is allowed, 
	 * and there is no time limit imposed. If greater than 0, then the value 
	 * represents the number of seconds of extended play supported beyond the maxduration value.
	 */
	@Getter@Setter
	private Integer maxextended;
	/** in Kbps	 */
	@Getter@Setter
	private Integer minBitrate;
	/** in Kbps	 */
	@Getter@Setter
	private Integer maxBitrate;
	/**Indicates if letter-boxing of 4:3 content into a 16:9 window is allowed, where 0 = no, 1 = yes.*/
	@Getter@Setter
	private Integer boxingallowed;
	/**com.kritter.constants.VideoPlaybackMethods*/
	@Getter@Setter
	private HashSet<Integer> playbackmethod;
	/**com.kritter.constants.ContentDeliveryMethods*/
	@Getter@Setter
	private HashSet<Integer> delivery;
	/**com.kritter.constants.VideoAdPos*/
	@Getter@Setter
	private Integer pos;
	/**TODO *** -CompanionAds*/
	/**com.kritter.constants.APIFrameworks*/
	@Getter@Setter
	private HashSet<Integer> api;
	/**com.kritter.constants.VASTCompanionTypes*/
	@Getter@Setter
	private HashSet<Integer> companiontype;

	private static final ObjectMapper objectMapper = new ObjectMapper();
	static {
		objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
	}
    public JsonNode toJson(){
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static VideoSupplyProps getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        return getObject(objectMapper,str);
    }
    public static VideoSupplyProps getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
    	VideoSupplyProps entity = objectMapper.readValue(str, VideoSupplyProps.class);
        return entity;

    }
    public static Integer[] toIntArray(HashSet<Integer>  set,Integer doNotInclude){
    	if(set ==null){
    		return null;
    	}
    	ArrayList<Integer> arrlist = new ArrayList<Integer>();
    	for(Integer setInteger:set){
    		if(setInteger != doNotInclude){
    			arrlist.add(setInteger);
    		}
    	}
    	Integer list2[] = new Integer[arrlist.size()];
    	list2 = arrlist.toArray(list2);
    	return list2;
    }
    public static String[] toMimes(HashSet<Integer>  set){
    	if(set ==null){
    		return null;
    	}
    	ArrayList<String> arrlist = new ArrayList<String>();
    	for(Integer setInteger:set){
    		VideoMimeTypes m = VideoMimeTypes.getEnum(setInteger);
    		if(m!=null){
    			arrlist.add(m.getMime());
    		}
    	}
    	String list2[] = new String[arrlist.size()];
    	list2 = arrlist.toArray(list2);
    	return list2;
    }
    public static HashSet<Integer> stringtoVideoMimeSetOrDefault(String inStr){
		HashSet<Integer> set = new HashSet<Integer>();
        if(inStr!=null){
        	String inStrTmp= inStr.trim();
        	if(!inStrTmp.isEmpty()){
        		String split[]=inStrTmp.split(",");
        		for(String str:split){
        			try{
        				Integer i = Integer.parseInt(str);
        				VideoMimeTypes enu = VideoMimeTypes.getEnum(i);
        				if(enu != null){
        					set.add(enu.getCode());
        				}
        			}catch(Exception e){
        			}
        		}
        	}
        }
        if(set.size()<1){
        	set.add(VideoMimeTypes.MPEG4.getCode());
        }
		return set;
    }
    public static HashSet<Integer> stringtoVideoProtoColSetOrDefault(String inStr){
		HashSet<Integer> set = new HashSet<Integer>();
        if(inStr!=null){
        	String inStrTmp= inStr.trim();
        	if(!inStrTmp.isEmpty()){
        		String split[]=inStrTmp.split(",");
        		for(String str:split){
        			try{
        				Integer i = Integer.parseInt(str);
        				VideoBidResponseProtocols enu = VideoBidResponseProtocols.getEnum(i);
        				if(enu != null){
        					set.add(enu.getCode());
        				}
        			}catch(Exception e){
        			}
        		}
        	}
        }
        if(set.size()<1){
        	for(VideoBidResponseProtocols enu:VideoBidResponseProtocols.values()){
        		if(enu != VideoBidResponseProtocols.NONVAST){
        			set.add(enu.getCode());
        		}
        	}
        }
		return set;
    }
    public static Integer stringtoVideoLinearityOrDefault(String inStr){
        if(inStr!=null){
        	String inStrTmp= inStr.trim();
        	if(!inStrTmp.isEmpty()){
        			try{
        				Integer i = Integer.parseInt(inStrTmp);
        				VideoLinearity enu = VideoLinearity.getEnum(i);
        				if(enu != null){
        					return enu.getCode();
        				}
        			}catch(Exception e){
        			}
        	}
        }
		return VideoLinearity.LINEAR_IN_STREAM.getCode();
    }
    public static HashSet<Integer> stringtoVideoPlaybackSetOrDefault(String inStr){
		HashSet<Integer> set = new HashSet<Integer>();
        if(inStr!=null){
        	String inStrTmp= inStr.trim();
        	if(!inStrTmp.isEmpty()){
        		String split[]=inStrTmp.split(",");
        		for(String str:split){
        			try{
        				Integer i = Integer.parseInt(str);
        				VideoPlaybackMethods enu = VideoPlaybackMethods.getEnum(i);
        				if(enu != null){
        					set.add(enu.getCode());
        				}
        			}catch(Exception e){
        			}
        		}
        	}
        }
        if(set.size()<1){
        	set.add(VideoPlaybackMethods.Unknown.getCode());
        }
		return set;
    }
    public static HashSet<Integer> stringtoVideoApiFrameworkSetOrDefault(String inStr){
		HashSet<Integer> set = new HashSet<Integer>();
        if(inStr!=null){
        	String inStrTmp= inStr.trim();
        	if(!inStrTmp.isEmpty()){
        		String split[]=inStrTmp.split(",");
        		for(String str:split){
        			try{
        				Integer i = Integer.parseInt(str);
        				APIFrameworks enu = APIFrameworks.getEnum(i);
        				if(enu != null){
        					set.add(enu.getCode());
        				}
        			}catch(Exception e){
        			}
        		}
        	}
        }
        if(set.size()<1){
        	set.add(APIFrameworks.Unknown.getCode());
        }
		return set;
    }
    public static HashSet<Integer> stringtoVideoContentDeliveryMethodSetOrDefault(String inStr){
		HashSet<Integer> set = new HashSet<Integer>();
        if(inStr!=null){
        	String inStrTmp= inStr.trim();
        	if(!inStrTmp.isEmpty()){
        		String split[]=inStrTmp.split(",");
        		for(String str:split){
        			try{
        				Integer i = Integer.parseInt(str);
        				ContentDeliveryMethods enu = ContentDeliveryMethods.getEnum(i);
        				if(enu != null){
        					set.add(enu.getCode());
        				}
        			}catch(Exception e){
        			}
        		}
        	}
        }
        if(set.size()<1){
        	set.add(ContentDeliveryMethods.Unknown.getCode());
        }
		return set;
    }
}
