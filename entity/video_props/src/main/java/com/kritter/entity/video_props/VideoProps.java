package com.kritter.entity.video_props;

import java.io.IOException;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.kritter.constants.APIFrameworks;
import com.kritter.constants.ContentDeliveryMethods;
import com.kritter.constants.VASTCompanionTypes;
import com.kritter.constants.VideoBidResponseProtocols;
import com.kritter.constants.VideoBoxing;
import com.kritter.constants.VideoDemandType;
import com.kritter.constants.VideoLinearity;
import com.kritter.constants.VideoMaxExtended;
import com.kritter.constants.VideoMimeTypes;
import com.kritter.constants.VideoPlaybackMethods;
import com.kritter.constants.VideoStartDelay;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class VideoProps {
    
    @Getter@Setter
    private int videoDemandType = VideoDemandType.VastTagUrl.getCode();
    @Getter@Setter
    private String vastTagUrl = null;
    @Getter@Setter
    private Set<Integer> vast_tag_macro = null;
    @Getter@Setter
    private Integer vast_tag_macro_quote = 0;
    @Getter@Setter
    private int mime = VideoMimeTypes.MPEG4.getCode();
    @Getter@Setter
    private int duration = -1;
    @Getter@Setter
    private int protocol = VideoBidResponseProtocols.VAST_3_0_WRAPPER.getCode();
    @Getter@Setter
    private int startdelay = VideoStartDelay.Unknown.getCode();
    @Getter@Setter
    private int width=-1;
    @Getter@Setter
    private int height=-1;
    @Getter@Setter
    private int linearity = VideoLinearity.LINEAR_IN_STREAM.getCode();
    @Getter@Setter
    private int maxextended = VideoMaxExtended.Unknown.getCode();
    @Getter@Setter
    private int bitrate=-1;
    @Getter@Setter
    private int boxingallowed = VideoBoxing.Unknown.getCode();
    @Getter@Setter
    private int playbackmethod = VideoPlaybackMethods.Unknown.getCode();
    @Getter@Setter
    private int delivery = ContentDeliveryMethods.Unknown.getCode();
    @Getter@Setter
    private int api = APIFrameworks.Unknown.getCode();
    @Getter@Setter
    private int companiontype = VASTCompanionTypes.Unknown.getCode();
    @Getter@Setter
    private Integer[] tracking; 
    @Getter@Setter
    private String[] video_info; 
    
    /**
     * absent is position and companion ad
     */
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public JsonNode toJson(){
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static VideoProps getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        return getObject(objectMapper,str);
    }
    public static VideoProps getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
        VideoProps entity = objectMapper.readValue(str, VideoProps.class);
        return entity;

    }

}
