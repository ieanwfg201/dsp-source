package com.kritter.bidrequest.entity.common.openrtbversion2_2;

import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The content object itself and all of its parameters are optional, so default
 * values are not provided. If an optional parameter is not specified, it should
 * be considered unknown. This object describes the content in which the
 * impression will appear (may be syndicated or non- syndicated content). This
 * object may be useful in the situation where syndicated content contains
 * impressions and does not necessarily match the publisher’s general content.
 * The exchange might or might not have knowledge of the page where the content
 * is running, as a result of the syndication method. (For example, video
 * impressions embedded in an iframe on an unknown web property or device.)
 * 
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestContentDTO {

	/**
	 * ID uniquely identifying the content.
	 */
	@JsonProperty("id")
    @Setter
	private String contentUniqueId;

    @JsonIgnore
    public String getContentUniqueId(){
        return contentUniqueId;
    }

	/**
	 * Content episode number (typically applies to video content).
	 */
	@JsonProperty("episode")
    @Setter
	private Integer contentEpisodeNumber;

    @JsonIgnore
    public Integer getContentEpisodeNumber(){
        return contentEpisodeNumber;
    }

	/**
	 * Content title. Video examples: “Search Committee” (television) or “A New
	 * Hope” (movie) or “Endgame” (made for web) Non-video example: “Why an
	 * Antarctic Glacier Is Melting So Quickly” (Time magazine article)
	 */
	@JsonProperty("title")
    @Setter
	private String contentTitle;

    @JsonIgnore
    public String getContentTitle(){
        return contentTitle;
    }

	/**
	 * Content series. Video examples: “The Office” (television) or “Star Wars”
	 * (movie) or “Arby ‘N’ The Chief” (made for web) Non-video example:
	 * “Ecocentric” (Time magazine blog)
	 */
	@JsonProperty("series")
    @Setter
	private String contentSeries;

    @JsonIgnore
    public String getContentSeries(){
        return contentSeries;
    }

	/**
	 * Content season. E.g., “Season 3” (typically applies to video content).
	 */
	@JsonProperty("season")
    @Setter
	private String contentSeason;

    @JsonIgnore
    public String getContentSeason(){
        return contentSeason;
    }

	/**
	 * Original URL of the content, for buy-side contextualization or review
	 */
	@JsonProperty("url")
    @Setter
	private String contentOriginalURL;

    @JsonIgnore
    public String getContentOriginalURL(){
        return contentOriginalURL;
    }

	/**
	 * Array of IAB content categories for the content. See Table 6.1 Content
	 * Categories.
	 */
	@JsonProperty("cat")
    @Setter
	private String[] contentCategories;

    @JsonIgnore
    public String[] getContentCategories(){
        return  contentCategories;
    }

	/**
	 * Video quality per the IAB’s classification. See Table 6.14 Video Quality.
	 */
	@JsonProperty("videoquality")
    @Setter
	private Integer videoQuality;

    @JsonIgnore
    public Integer getVideoQuality(){
        return videoQuality;
    }

	/**
	 * Comma separated list of keywords describing the content.
	 */
	@JsonProperty("keywords")
    @Setter
	private String contentKeywordsCSV;

    @JsonIgnore
    public String getContentKeywordsCSV(){
        return contentKeywordsCSV;
    }

	/**
	 * Content rating (e.g., MPAA)
	 */
	@JsonProperty("contentrating")
    @Setter
	private String contentRating;

    @JsonIgnore
    public String getContentRating(){
        return contentRating;
    }

	/**
	 * User rating of the content (e.g., number of stars, likes, etc.).
	 */
	@JsonProperty("userrating")
    @Setter
	private String userRating;

    @JsonIgnore
    public String getUserRating(){
        return userRating;
    }

	/**
	 * Specifies the type of content (game, video, text, etc.).
	 * 
	 * 1 Video (a video file or stream that is being watched by the user,
	 * including (Internet) television broadcasts)
	 * 
	 * 2 Game (an interactive software game that is being played by the user)
	 * 
	 * 3 Music (an audio file or stream that is being listened to by the user,
	 * including (Internet) radio broadcasts)
	 * 
	 * 4 Application (an interactive software application that is being used by
	 * the user)
	 * 
	 * 5 Text (a document that is primarily textual in nature that is being read
	 * or viewed by the user, including web page, ebook, or news article)
	 * 
	 * 6 Other (content type unknown or the user is consuming content which does
	 * not fit into one of the categories above)
	 * 
	 * 7 Unknown
	 */
	@JsonProperty("context")
    @Setter
	private String contentContext;

    @JsonIgnore
    public String getContentContext(){
        return contentContext;
    }

	/**
	 * Is content live? E.g., live video stream, live blog.
	 * 
	 * “1” means content is live.
	 * 
	 * “0” means it is not live.
	 */
	@JsonProperty("livestream")
    @Setter
	private Integer isContentLiveStream;

    @JsonIgnore
    public Integer getIsContentLiveStream(){
        return isContentLiveStream;
    }

	/**
	 * 1 for “direct”;
	 * 
	 * 0 for “indirect”
	 */
	@JsonProperty("sourcerelationship")
    @Setter
	private Integer sourceRelationship;

    @JsonIgnore
    public Integer getSourceRelationship(){
        return sourceRelationship;
    }

	/**
	 * Producer object.
	 */
    @JsonProperty("producer")
    @Setter
	private BidRequestContentProducerDTO bidRequestContentProducer;

    @JsonIgnore
    public BidRequestContentProducerDTO getBidRequestContentProducer(){
        return bidRequestContentProducer;
    }

	/**
	 * Length of content (appropriate for video or audio) in seconds.
	 */
	@JsonProperty("len")
    @Setter
	private Integer contentLength;

    @JsonIgnore
    public Integer getContentLength(){
        return contentLength;
    }

    /**
     * Media rating of the content, per QAG guidelines.See Table 0 QAG
     * Media Ratings for list of possible values
     */
    @JsonProperty("qagmediarating")
    @Setter
    private Integer qagMediaRating;

    @JsonIgnore
    public Integer getQagMediaRating(){
        return qagMediaRating;
    }

    /**
     * From QAG Video Addendum. If content can be embedded (such as
     * an embeddable video player) this value should be set to “1”.
     * If content cannot be embedded, then this should be set to “0”.
     */
    @JsonProperty("embeddable")
    @Setter
    private Integer embeddable;

    @JsonIgnore
    public Integer getEmbeddable(){
        return embeddable;
    }

    /**
     * Language of the content. Use alpha-2/ISO 639-1 codes.
     */
    @JsonProperty("language")
    @Setter
    private String contentLanguage;

    @JsonIgnore
    public String getContentLanguage(){
        return contentLanguage;
    }

    /**
     * extension object.
     */
    @JsonProperty("ext")
    @Setter
    private Object extensionObject;

    @JsonIgnore
    public Object getExtensionObject(){
        return extensionObject;
    }
}
