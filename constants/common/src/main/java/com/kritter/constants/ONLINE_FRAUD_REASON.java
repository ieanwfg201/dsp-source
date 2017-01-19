package com.kritter.constants;

/**
 */
public enum ONLINE_FRAUD_REASON {

    // if requesting handset is bot, done before actual handset
    // id matching.
    BOT_HANDSET("BOT"),

    // handset id mismatch.
    HANDSET_ID_MISMATCH("HI"),

	//handset id missing from request,
	HANDSET_ID_MISSING_FROM_REQUEST("HIM"),

	//handset id missing from request,
	HANDSET_UNDETECTED("HUD"),

	// impression served is expired.
    IMPRESSION_EXPIRED("EXP"),

	//impression id missing from request
	IMPRESSION_ID_MISSING_FROM_REQUEST("IMP_ID"),

    // url tampered with, hash check failure.
    URL_TAMPERED("URL"),

	// url's data not complete
	URL_DATA_NOT_COMPLETE("URL_MAL"),

	// location id mismatch, only at country level for now.
    LOCATION_MISMATCH("GEO"),

	// location id missing from request
	LOCATION_ID_MISSING_FROM_REQUEST("LI"),

	// location id could not be detected
	LOCATION_UNDETECTED("LU"),

	// site marked as not fit, red marked or suspended.
    SITE_UNFIT("SU"),

	// site id missing from request.
	SITE_ID_MISSING_FROM_REQUEST("SI"),

	// site id not present in cache.
	SITE_ID_NOT_PRESENT_IN_CACHE("SINP"),

	// for analytics related or in general cases.This reason is for
    // post impression event wherein the url received is not
    // from correct source.We do that by checking domain of referer
    // url against the one present in our system.
    REFERER_DOMAIN_UNKNOWN("DOMAIN"),

    // if there is any server error.
    INTERNAL_SERVER_ERROR("SERVER"),

	//inhouse click event,ad id not found.
	AD_ID_NOT_FOUND("ADID"),

	//inhouse click event,ad id missing from request.
	AD_ID_MISSING("ADIDM"),

	// special reason for request not being a fraud.
    HEALTHY_REQUEST("OK"),

	//if conversion expired.
	CONVERSION_EXPIRED("CONV_EXP"),

	//if event repeats.
	EVENT_DUPLICATE("DUPLICATE"),

	BILLABLE_EVENT("BILLABLE_EVENT"),
	//if RETARGETING SEGMENT NOT FOUND.
	RETARGETING_SEGMENT_NF("RETARGETING_SEGMENT_NF"),

    // USER SYNC EXCHANGE ID MISSING
    USER_SYNC_EXCH_ID_MISSING("USER_SYNC_EXCH_ID_MISSING"),

    // USER SYNC DSP ID MISSING
    USER_SYNC_DSP_ID_MISSING("USER_SYNC_DSP_ID_MISSING"),

    // USER SYNC DSP USER ID MISSING
    USER_SYNC_DSP_USER_ID_MISSING("USER_SYNC_DSP_USER_ID_MISSING");

	private String value;

	private ONLINE_FRAUD_REASON(String value) {
		this.value = value;
	}

	public String getFraudReasonValue() {
		return this.value;
	}
}
