package models.entities;

import org.springframework.beans.BeanUtils;

import play.data.validation.Constraints.Required;

import com.kritter.api.entity.creative_container.Creative_container;
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


import lombok.Getter;
import lombok.Setter;

public class CreativeContainerEntity extends Entity{
    private int id = -1;
    private String guid = null;
    private String account_guid = null;
    private String label = null;
    @Required
    private int format_id = 2;
    @Required
    private String creative_attr = "[]";
    private String text = null; 
    private String resource_uri_ids  = null; 
    private String html_content = null; 
    private int modified_by = -1;
    private long created_on = 0;
    private long last_modified = 0;
    private String ext_resource_url  = "";
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
    private String creative_macro = "[]";
    @Getter@Setter
    private int creative_macro_quote = 0;
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
    private String trackingStr = "[]";
    @Getter@Setter
    private String direct_videos = "[]";
    
    public String getExt_resource_url() {
        return ext_resource_url;
    }
    public void setExt_resource_url(String ext_resource_url) {
        if(ext_resource_url != null){
            this.ext_resource_url = ext_resource_url.trim();
        }
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
    public int getProtocol() {
        return protocol;
    }
    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }
    public Creative_container getEntity(){
    	Creative_container cc = new Creative_container();
    	BeanUtils.copyProperties(this, cc);
    	return cc;
    }
}
