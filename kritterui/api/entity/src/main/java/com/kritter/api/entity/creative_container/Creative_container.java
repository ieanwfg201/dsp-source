package com.kritter.api.entity.creative_container;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.constants.APIFrameworks;
import com.kritter.constants.ContentDeliveryMethods;
import com.kritter.constants.StatusIdEnum;
import com.kritter.constants.VASTCompanionTypes;
import com.kritter.constants.VideoBidResponseProtocols;
import com.kritter.constants.VideoBoxing;
import com.kritter.constants.VideoDemandType;
import com.kritter.constants.VideoLinearity;
import com.kritter.constants.VideoMaxExtended;
import com.kritter.constants.VideoMimeTypes;
import com.kritter.constants.VideoPlaybackMethods;

import lombok.Getter;
import lombok.Setter;

public class Creative_container {
    /** mandatory - auto generated at creation */
    private int id = -1;
    /** mandatory - auto generated at creation */
    private String guid = null;
    /** mandatory = guid of account @see com.kritter.api.entity.account.Account */
    private String account_guid = null;
    /** mandatory */
    private String label = null;
    /** mandatory - @see com.kritter.constants.CreativeFormat */
    private int format_id = 2;
    /** mandatory - json array of creative attribute ids*/
    private String creative_attr = null;
    /** optional - when format is text*/
    private String text = null; 
    /** optional - for banner ads, jssor array of creative banner id @see com.kritter.api.entity.creative_banner.Creative_banner */
    private String resource_uri_ids  = null;
    /** optional - for rich media etc*/
    private String html_content = null;
    /** mandatory - id of account which is modifying this entity @see com.kritter.api.entity.account.Account */
    private int modified_by = -1;
    /** auto populated */
    private long created_on = 0;
    /** auto populated */
    private long last_modified = 0;
    /** mandatory - @see com.kritter.constants.StatusIdEnum */
    private StatusIdEnum status_id = StatusIdEnum.Pending;
    /** for rich media ads to specify external creative url if required */
    private String ext_resource_url = "";
    @Getter@Setter
    private String native_icons;
    @Getter@Setter
    private String native_screenshots;
    @Getter@Setter
    private String native_title;
    @Getter@Setter
    private String native_desc;
    @Getter@Setter
    private String native_cta;
    @Getter@Setter
    private Boolean native_rating;
    @Getter@Setter
    private String native_download_count;
    @Getter@Setter
    private String native_active_players;
    @Getter@Setter
    private String creative_macro="[]";
    @Getter@Setter
    private int creative_macro_quote = 0;
    /**Video Begin*/
    @Getter@Setter
    private int videoDemandType = VideoDemandType.VastTagUrl.getCode();
    @Getter@Setter
    private String vastTagUrl = null;
    @Getter@Setter
    private String vastTagMacro = "[]";
    @Getter@Setter
    private int vastTagMacroQuote = 0;
    @Getter@Setter
    private int mime = VideoMimeTypes.MPEG4.getCode();
    @Getter@Setter
    private int duration = -1;
    @Getter@Setter
    private int protocol = VideoBidResponseProtocols.VAST_3_0_WRAPPER.getCode();
    @Getter@Setter
    private int startdelay = -11;
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
    private String trackingStr="[]";
    @Getter@Setter
    private String direct_videos;
    @Getter@Setter
    private String comment;
    @Getter@Setter
    private String slot_info="[]";

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((account_guid == null) ? 0 : account_guid.hashCode());
		result = prime * result + api;
		result = prime * result + bitrate;
		result = prime * result + boxingallowed;
		result = prime * result + companiontype;
		result = prime * result + (int) (created_on ^ (created_on >>> 32));
		result = prime * result + ((creative_attr == null) ? 0 : creative_attr.hashCode());
		result = prime * result + ((creative_macro == null) ? 0 : creative_macro.hashCode());
		result = prime * result + creative_macro_quote;
		result = prime * result + delivery;
		result = prime * result + ((direct_videos == null) ? 0 : direct_videos.hashCode());
		result = prime * result + duration;
		result = prime * result + ((ext_resource_url == null) ? 0 : ext_resource_url.hashCode());
		result = prime * result + format_id;
		result = prime * result + ((guid == null) ? 0 : guid.hashCode());
		result = prime * result + height;
		result = prime * result + ((html_content == null) ? 0 : html_content.hashCode());
		result = prime * result + id;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + (int) (last_modified ^ (last_modified >>> 32));
		result = prime * result + linearity;
		result = prime * result + maxextended;
		result = prime * result + mime;
		result = prime * result + modified_by;
		result = prime * result + ((native_active_players == null) ? 0 : native_active_players.hashCode());
		result = prime * result + ((native_cta == null) ? 0 : native_cta.hashCode());
		result = prime * result + ((native_desc == null) ? 0 : native_desc.hashCode());
		result = prime * result + ((native_download_count == null) ? 0 : native_download_count.hashCode());
		result = prime * result + ((native_icons == null) ? 0 : native_icons.hashCode());
		result = prime * result + ((native_rating == null) ? 0 : native_rating.hashCode());
		result = prime * result + ((native_screenshots == null) ? 0 : native_screenshots.hashCode());
		result = prime * result + ((native_title == null) ? 0 : native_title.hashCode());
		result = prime * result + playbackmethod;
		result = prime * result + protocol;
		result = prime * result + ((resource_uri_ids == null) ? 0 : resource_uri_ids.hashCode());
		result = prime * result + startdelay;
		result = prime * result + ((status_id == null) ? 0 : status_id.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((trackingStr == null) ? 0 : trackingStr.hashCode());
		result = prime * result + ((vastTagUrl == null) ? 0 : vastTagUrl.hashCode());
		result = prime * result + videoDemandType;
		result = prime * result + width;
		return result;
	}
    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Creative_container other = (Creative_container) obj;
		if (account_guid == null) {
			if (other.account_guid != null)
				return false;
		} else if (!account_guid.equals(other.account_guid))
			return false;
		if (api != other.api)
			return false;
		if (bitrate != other.bitrate)
			return false;
		if (boxingallowed != other.boxingallowed)
			return false;
		if (companiontype != other.companiontype)
			return false;
		if (created_on != other.created_on)
			return false;
		if (creative_attr == null) {
			if (other.creative_attr != null)
				return false;
		} else if (!creative_attr.equals(other.creative_attr))
			return false;
		if (creative_macro == null) {
			if (other.creative_macro != null)
				return false;
		} else if (!creative_macro.equals(other.creative_macro))
			return false;
		if (creative_macro_quote != other.creative_macro_quote)
			return false;
		if (delivery != other.delivery)
			return false;
		if (direct_videos == null) {
			if (other.direct_videos != null)
				return false;
		} else if (!direct_videos.equals(other.direct_videos))
			return false;
		if (duration != other.duration)
			return false;
		if (ext_resource_url == null) {
			if (other.ext_resource_url != null)
				return false;
		} else if (!ext_resource_url.equals(other.ext_resource_url))
			return false;
		if (format_id != other.format_id)
			return false;
		if (guid == null) {
			if (other.guid != null)
				return false;
		} else if (!guid.equals(other.guid))
			return false;
		if (height != other.height)
			return false;
		if (html_content == null) {
			if (other.html_content != null)
				return false;
		} else if (!html_content.equals(other.html_content))
			return false;
		if (id != other.id)
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (last_modified != other.last_modified)
			return false;
		if (linearity != other.linearity)
			return false;
		if (maxextended != other.maxextended)
			return false;
		if (mime != other.mime)
			return false;
		if (modified_by != other.modified_by)
			return false;
		if (native_active_players == null) {
			if (other.native_active_players != null)
				return false;
		} else if (!native_active_players.equals(other.native_active_players))
			return false;
		if (native_cta == null) {
			if (other.native_cta != null)
				return false;
		} else if (!native_cta.equals(other.native_cta))
			return false;
		if (native_desc == null) {
			if (other.native_desc != null)
				return false;
		} else if (!native_desc.equals(other.native_desc))
			return false;
		if (native_download_count == null) {
			if (other.native_download_count != null)
				return false;
		} else if (!native_download_count.equals(other.native_download_count))
			return false;
		if (native_icons == null) {
			if (other.native_icons != null)
				return false;
		} else if (!native_icons.equals(other.native_icons))
			return false;
		if (native_rating == null) {
			if (other.native_rating != null)
				return false;
		} else if (!native_rating.equals(other.native_rating))
			return false;
		if (native_screenshots == null) {
			if (other.native_screenshots != null)
				return false;
		} else if (!native_screenshots.equals(other.native_screenshots))
			return false;
		if (native_title == null) {
			if (other.native_title != null)
				return false;
		} else if (!native_title.equals(other.native_title))
			return false;
		if (playbackmethod != other.playbackmethod)
			return false;
		if (protocol != other.protocol)
			return false;
		if (resource_uri_ids == null) {
			if (other.resource_uri_ids != null)
				return false;
		} else if (!resource_uri_ids.equals(other.resource_uri_ids))
			return false;
		if (startdelay != other.startdelay)
			return false;
		if (status_id != other.status_id)
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (trackingStr == null) {
			if (other.trackingStr != null)
				return false;
		} else if (!trackingStr.equals(other.trackingStr))
			return false;
		if (vastTagUrl == null) {
			if (other.vastTagUrl != null)
				return false;
		} else if (!vastTagUrl.equals(other.vastTagUrl))
			return false;
		if (videoDemandType != other.videoDemandType)
			return false;
		if (width != other.width)
			return false;
		return true;
	}
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getGuid() {
        return guid;
    }
    public void setGuid(String guid) {
        this.guid = guid;
    }
    public String getAccount_guid() {
        return account_guid;
    }
    public void setAccount_guid(String account_guid) {
        this.account_guid = account_guid;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public int getFormat_id() {
        return format_id;
    }
    public void setFormat_id(int format_id) {
        this.format_id = format_id;
    }
    public String getCreative_attr() {
        return creative_attr;
    }
    public void setCreative_attr(String creative_attr) {
        this.creative_attr = creative_attr;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getResource_uri_ids() {
        return resource_uri_ids;
    }
    public void setResource_uri_ids(String resource_uri_ids) {
        this.resource_uri_ids = resource_uri_ids;
    }
    public String getHtml_content() {
        return html_content;
    }
    public void setHtml_content(String html_content) {
        this.html_content = html_content;
    }
    public int getModified_by() {
        return modified_by;
    }
    public void setModified_by(int modified_by) {
        this.modified_by = modified_by;
    }
    public long getCreated_on() {
        return created_on;
    }
    public void setCreated_on(long created_on) {
        this.created_on = created_on;
    }
    public long getLast_modified() {
        return last_modified;
    }
    public void setLast_modified(long last_modified) {
        this.last_modified = last_modified;
    }
    public StatusIdEnum getStatus_id() {
        return status_id;
    }
    public void setStatus_id(StatusIdEnum status_id) {
        this.status_id = status_id;
    }
    public String getExt_resource_url() {
        return ext_resource_url;
    }
    public void setExt_resource_url(String ext_resource_url) {
        this.ext_resource_url = ext_resource_url;
    }

    
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static Creative_container getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        Creative_container entity = objectMapper.readValue(str, Creative_container.class);
        return entity;

    }
    
    
}
