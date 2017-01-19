package com.kritter.bidrequest.entity.common.openrtbversion2_4;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * This object describes the content in which the impression will appear, which may be syndicated or non-syndicated 
 * content. This object may be useful when syndicated content contains impressions and does not necessarily match the 
 * publisher’s general content. The exchange might or might not have knowledge of the page where the content is running, 
 * as a result of the syndication method. For example might be a video impression embedded in an iframe on an unknown 
 * web property or device.
 * 
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestContentDTO {

	/**
	 * ID uniquely identifying the content.
	 */
    @Setter@Getter
	private String id;

	/**
	 * Episode number.
	 */
    @Setter@Getter
	private Integer episode;

	/**
	 * Content title. Video Examples: “Search Committee” (television), “A New Hope” (movie), or “Endgame” 
	 * (made for web). Non-Video Example: “Why an Antarctic Glacier Is Melting So Quickly” (Time magazine article).
	 */
    @Setter@Getter
	private String title;
	/**
	 * Content series. Video Examples: “The Office” (television), “Star Wars” (movie), or “Arby ‘N’ The Chief” 
	 * (made for web). Non-Video Example: “Ecocentric” (Time Magazine blog).
	 */
    @Setter@Getter
	private String series;

	/**
	 * Content season (e.g., “Season 3”).
	 */
    @Setter@Getter
	private String season;

	/**
	 * Artist credited with the content.
	 */
    @Setter@Getter
	private String artist;
	/**
	 * Genre that best describes the content (e.g., rock, pop, etc
	 */
    @Setter@Getter
	private String genre;
	/**
	 * Album to which the content belongs; typically for audio.
	 */
    @Setter@Getter
	private String album;
	/**
	 * International Standard Recording Code conforming to ISO-3901.
	 */
    @Setter@Getter
	private String isrc;
	/**
	 * Details about the content Producer (Section 3.2.12).
	 */
    @Setter@Getter
	private BidRequestContentProducerDTO producer;

    /**
     * URL of the content, for buy-side contextualization or review.
     */
    @Setter@Getter
	private String url;

	/**
	 * Array of IAB content categories that describe the content producer. Refer to List 5.1.
	 */
    @Setter@Getter
	private String[] cat;

	/**
	 * Production quality. Refer to List 5.11.
	 */
    @Setter@Getter
	private Integer prodq;
	/**
	 * Note: Deprecated in favor of prodq. Video quality. Refer to List 5.11
	 */
    @Setter@Getter@Deprecated
	private Integer videoquality;

	/**
	 * Type of content (game, video, text, etc.). Refer to List 5.16.
	 */
    @Setter@Getter
	private Integer context;

	/**
	 * Content rating (e.g., MPAA).
	 */
    @Setter@Getter
	private String contentrating;

	/**
	 * User rating of the content (e.g., number of stars, likes, etc.).
	 */
    @Setter@Getter
	private String userrating;

    /**
     * Media rating per IQG guidelines. Refer to List 5.17.
     */
    @Setter@Getter
    private Integer qagmediarating;

	/**
	 * Comma separated list of keywords describing the content
	 */
    @Setter@Getter
	private String keywords;

    /**
	 * 0 = not live, 1 = content is live (e.g., stream, live blog).
	 */
    @Setter@Getter
	private Integer livestream;

	/**
	 * 0 = indirect, 1 = direct.
	 */
    @Setter@Getter
	private Integer sourcerelationship;

	/**
	 * Length of content in seconds; appropriate for video or audio.
	 */
    @Setter@Getter
	private Integer len;

    /**
     * Content language using ISO-639-1-alpha-2.
     */
    @Setter@Getter
    private String language;

    /**
     * Indicator of whether or not the content is embeddable (e.g., an embeddable video player), where 0 = no, 1 = yes.
     */
    @Setter@Getter
    private Integer embeddable;
    
    /**
     * Additional content data. Each Data object (Section 3.2.16) represents a different data source.
     */
    @Setter@Getter
    private BidRequestData data;
    /**
     * extension object.
     */
    @Setter@Getter
    private Object ext;
}
