package com.kritter.constants.error;

public enum ErrorEnum {
    /**
     * NP = NOT PROVIDED
     * NF = NOT FOUND
     */
    NO_ERROR(0,"OK"),
    Internal_ERROR_1(1,"Connection refused"),
    Internal_ERROR_2(2,"Account Object null"),
    USERID_ALREADY_TAKEN(3,"UserId Already Taken"),
    SQL_EXCEPTION(4,"SQL Exception"),
    JSON_EXCEPTION(5,"JSON Exception"),
    ACCOUNT_NOT_CREATED(6,"Unable to create account"),
    ACCOUNT_LIST_ENTITY_NULL(7,"Account List Entity null"),
    ACCOUNT_NOT_UPDATED(8,"Unable to update account"),
    USERIDINCORRECT(9,"UserId incorrect"),
    IO_NULL(10,"IO Object null"),
    IO_NOT_CREATED(11,"IO Not Created"),
    IO_ACCOUNT_EXIST(12,"Order number and account already exist"),
    IOLIST_ENTITY_NULL(13,"IO List Entity Object null"),
    IO_NOT_FOUND(14,"IO Not Found"),
    IO_NOT_UPDATED(15,"IO Not updated"),
    ACCOUNT_BUDGET_NULL(16,"Account Budget Object null"),
    ACCOUNT_BUDGET_NOT_INSERTED(17,"Account Budget Not Inserted"),
    ACCOUNT_BUDGET_NOT_FOUND(18,"Account Budget Not Found"),
    ACCOUNT_BUDGET_NOT_UPDATED(19,"Account Budget Not Updated"),
    METADATA_EMPTY(20,"Metadata Empty"),
    SITE_OBJECT_NULL(21,"Site Object Null"),
    SITE_NOT_INSERTED(22,"Site Not Inserted"),
    SITE_NOT_UPDATED(23,"Site Not Updated"),
    SITE_LIST_ENTITY_NULL(24,"Site List Entity Object null"),
    SITE_NOT_FOUND(25,"SITE Not Found"),
    META_INPUT_NULL(26,"Meta Input null"),
    CAMPAIGN_NULL(27,"Campaign Object null"),
    CAMPAIGN_NOT_INSERTED(28,"Campaign Not Inserted"),
    CAMPAIGN_NOT_UPDATED(29,"Campaign Not Inserted"),
    CAMPAIGNLIST_ENTITY_NULL(30,"CAMPAIGN List Entity Object null"),
    CAMPAIGN_NOT_FOUND(31,"Campaign Not Found"),
    CAMPAIGN_BUDGET_NULL(32,"Campaign Budget Object null"),
    CAMPAIGN_BUDGET_NOT_INSERTED(33,"Campaign Budget Not Inserted"),
    CAMPAIGN_BUDGET_NOT_UPDATED(34,"Campaign Budget Not Updated"),
    CAMPAIGNBUDGETLIST_ENTITY_NULL(35,"CAMPAIGN Budget List Entity Object null"),
    CAMPAIGN_BUDGET_NOT_FOUND(36,"Campaign Budget Not Found"),
    IO_POPULATION_FAIL(37,"IO CHECK /Poulation failed"),
    IO_STATUS_NOT_NEW(38,"IO Status has already changed"),
    TARGETING_PROFILE_NULL(39,"Targeting Profile null"),
    TARGETING_PROFILE_NOT_INSERTED(40,"Targeting Profile Not Inserted"),
    TARGETING_PROFILE_NOT_UPDATED(41,"Targeting Profile Not Updated"),
    TARGETING_PROFILE_LIST_ENTITY_NULL(42,"Targeting Profile List ENTITY null"),
    TARGETING_PROFILE_NOT_FOUND(43,"TARGETING_PROFILE Not Found"),
    CREATIVE_BANNER_NULL(44,"CREATIVE_BANNER Object null"),
    CREATIVE_BANNER_NOT_INSERTED(45,"CREATIVE_BANNER Not Inserted"),
    CREATIVE_BANNER_NOT_UPDATED(46,"CREATIVE_BANNER Not Updated"),
    CREATIVE_BANNERLIST_ENTITY_NULL(47,"CREATIVE_BANNER List Entity Object null"),
    CREATIVE_BANNER_NOT_FOUND(48,"CREATIVE_BANNER Not Found"),
    CREATIVE_CONTAINER_NULL(49,"CREATIVE_CONTAINER Object null"),
    CREATIVE_CONTAINER_NOT_INSERTED(50,"CREATIVE_CONTAINER Not Inserted"),
    CREATIVE_CONTAINER_NOT_UPDATED(51,"CREATIVE_CONTAINER Not Updated"),
    CREATIVE_CONTAINERLIST_ENTITY_NULL(52,"CREATIVE_CONTAINER List Entity Object null"),
    CREATIVE_CONTAINER_NOT_FOUND(53,"CREATIVE_CONTAINER Not Found"),
    AD_NULL(54,"Ad Object null"),
    AD_NOT_INSERTED(55,"AD Not Inserted"),
    AD_NOT_UPDATED(56,"AD Not Updated"),
    AD_LIST_ENTITY_NULL(57,"AD List Entity Object null"),
    AD_NOT_FOUND(58,"AD Not Found"),
    USERID_EMAIL_ALREADY_PRESENT(60,"UserId or Email Id Already Present"),
    EMAIL_ALREADY_PRESENT(61,"Email Id Already Present"),
    PHONE_NULL(62,"PHONE_NULL"),
    INVALID_PHONE(63,"INVALID_PHONE"),
    INSERT_ISP_MAPPING_FAILED(64,"INSERT_ISP_MAPPING_FAILED"),
    UPDATE_ISP_MAPPING_FAILED(65,"UPDATE_ISP_MAPPING_FAILED"),
    EMPTY_RESULT_DUE_TO_INPUT(66,"EMPTY_RESULT_DUE_TO_INPUT"),
    API_KEY_INVALID(67,"API_KEY_INVALID"),
    ADVERTISER_INVALID(68,"ADVERTISER_INVALID"),
    TYPE_INVALID(69,"TYPE_INVALID"),
    START_DATE_INVALID(71,"START_DATE_INVALID"),
    END_DATE_INVALID(72,"END_DATE_INVALID"),
    CAMPAIGN_INVALID(73,"CAMPAIGN_INVALID"),
    AD_INVALID(72,"AD_INVALID"),
    PUBLISHER_INVALID(73,"PUBLISHER_INVALID"),
    AUTH_CREDENTIALS_INVALID(74,"AUTH_CREDENTIALS_INVALID"),
    SAVED_QUERY_OBJECT_NULL(75,"SAVED_QUERY_OBJECT_NULL"),
    SAVED_QUERY_NOT_INSERTED(76,"SAVED_QUERY_NOT_INSERTED"),
    SAVED_QUERY_NOT_UPDATED(77,"SAVED_QUERY_NOT_UPDATED"),
    SAVED_QUERY_LIST_ENTITY_NULL(78,"SAVED_QUERY_LIST_ENTITY_NULL"),
    SAVED_QUERY_NOT_FOUND(79,"SAVED_QUERY_NOT_FOUND"),
    OK_STATUS_FOR_CONVERSION_FEEDBACK(80,"OK"),
    FRAUD_STATUS_FOR_CONVERSION_FEEDBACK(81,"FRAUD"),
    INCORRECTFREQUENCY(82,"INCORRECTFREQUENCY"),
    ACTION_TYPE_ABSENT(83,"ACTION_TYPE_ABSENT"),
    ACCOUNT_TYPE_ABSENT(84,"ACCOUNT_TYPE_ABSENT"),
    EXT_SITE_INPUT_NULL(85,"EXT_SITE_INPUT_NULL"),
    EXT_SITE_NF(86,"EXT_SITE_NF"),
    EXT_SITE_ID_NP(87,"EXT_SITE_ID_NP"),
    EXT_SITE_NO_ROWS_TO_UPDATE(88,"EXT_SITE_NO_ROWS_TO_UPDATE"),
    ISP_MAPPING_NULL(89,"ISP_MAPPING_NULL"),
    ISP_MAPPING_NOT_INSERTED(90,"ISP_MAPPING_NOT_INSERTED"),
    ISP_MAPPING_LIST_ENTITY_NULL(91,"ISP_MAPPING_LIST_ENTITY_NULL"),
    ISP_MAPPING_COUNTRY_ABSENT(92,"ISP_MAPPING_COUNTRY_ABSENT"),
    ISP_MAPPING_NOT_FOUND(93,"ISP_MAPPING_NOT_FOUND"),
    ISP_MAPPING_ENUM_NF(94,"ISP_MAPPING_ENUM_NF"),
    ISP_MAPPING_NF_FOR_UPDATE(95,"ISP_MAPPING_NF_FOR_UPDATE"),
    IMAGE_UPLOAD_FAILED(96,"IMAGE_UPLOAD_FAILED"),
    IMAGE_SIZE_MISMATCH(97,"IMAGE_SIZE_MISMATCH"),
    ACCOUNT_GUID_ABSENT(98,"ACCOUNT_GUID_ABSENT"),
    INTERNAL_ERROR_CONTACT_ADMINISTRATOR(99,"INTERNAL_ERROR_CONTACT_ADMINISTRATOR"),
    FILE_UPLOAD_FAILED(100,"FILE_UPLOAD_FAILED"),
    FILE_EXTENSION_NOT_ALLOWED(101,"FILE_EXTENSION_NOT_ALLOWED"),
    FILE_READ_ERROR(102,"FILE_READ_ERROR"),
    FILE_IP_FORMAT_INCORRECT(103,"FILE_IP_FORMAT_INCORRECT"),
    IDDEFINITION_INPUT_NULL(104,"IDDEFINITION_INPUT_NULL"),
    IDDEFINITION_EMPTY(105,"IDDEFINITION_EMPTY"),
    SSPENTITY_NULL(106,"SSPENTITY_NULL"),
    SSP_GLOBAL_RULES_NOT_UPDATED_INSERTED(107,"SSP_GLOBAL_RULES_NOT_UPDATED_INSERTED"),
    PARENT_ACCOUNT_OBJECT_NULL(108,"Parent Account Object Null"),
    PARENT_ACCOUNT_NOT_INSERTED(109,"Parent Account Not Inserted"),
    PARENT_ACCOUNT_NOT_UPDATED(110,"Parent Account Not Updated"),
    PARENT_ACCOUNT_NOT_FOUND(111,"Parent Account Not Found"),
    RETARGETING_SEGMENT_NULL(112,"RETARGETING_SEGMENT_NULL"),
    RETARGETING_SEGMENT_NOT_INSERTED(113,"RETARGETING_SEGMENT_NOT_INSERTED"),
    RetargetingSegmentInputEntity_null(114,"RetargetingSegmentInputEntity_null"),
    RETARGETING_SEGMENT_NOT_FOUND(115,"RETARGETING_SEGMENT_NOT_FOUND"),
    NATIVE_ICON_NULL(116,"NATIVE_ICON_NULL"),
    NATIVE_ICON_NOT_INSERTED(117,"NATIVE_ICON_NOT_INSERTED"),
    NATIVE_ICON_NOT_UPDATED(118,"NATIVE_ICON_NOT_UPDATED"),
    NATIVE_ICONLIST_ENTITY_NULL(119,"NATIVE_ICONLIST_ENTITY_NULL"),
    NATIVE_ICON_NOT_FOUND(120,"NATIVE_ICON_NOT_FOUND"),
    NATIVE_SCREENSHOT_NULL(121,"NATIVE_SCREENSHOT_NULL"),
    NATIVE_SCREENSHOT_NOT_INSERTED(122,"NATIVE_SCREENSHOT_NOT_INSERTED"),
    NATIVE_SCREENSHOT_NOT_UPDATED(123,"NATIVE_SCREENSHOT_NOT_UPDATED"),
    NATIVE_SCREENSHOTLIST_ENTITY_NULL(124,"NATIVE_SCREENSHOTLIST_ENTITY_NULL"),
    NATIVE_SCREENSHOT_NOT_FOUND(125,"NATIVE_SCREENSHOT_NOT_FOUND"),
    NATIVE_ICON_GUID_NULL(126,"NATIVE_ICON_GUID_NULL"),
    NATIVE_ICON_ID_LIST_NULL(127,"NATIVE_ICON_ID_LIST_NULL"),
    NATIVE_SCREENSHOT_GUID_NULL(128,"NATIVE_SCREENSHOT_GUID_NULL"),
    NATIVE_SCREENSHOT_ID_LIST_NULL(129,"NATIVE_SCREENSHOT_ID_LIST_NULL"),
    ReqLoggingList_NULL(130,"ReqLoggingList_NULL"),
    ReqLoggingInput_NULL(131,"ReqLoggingInput_NULL"),
    ReqLoggingEntity_NF(132,"ReqLoggingEntity_NF"),
    ReqLoggingEntity_NULL(133,"ReqLoggingEntity_NULL"),
    ReqLoggingEntity_NOT_UPDATED(134,"ReqLoggingEntity_NOT_UPDATED"),
    ReqLoggingEntity_NOT_INSERTED(135,"ReqLoggingEntity_NOT_INSERTED"),
    VIDEO_INFO_NULL(136,"VIDEO_INFO_NULL"),
    VIDEO_INFO_NOT_INSERTED(137,"VIDEO_INFO_NOT_INSERTED"),
    VIDEO_INFO_NOT_UPDATED(138,"VIDEO_INFO_NOT_UPDATED"),
    VIDEO_INFOLIST_ENTITY_NULL(139,"VIDEO_INFOLIST_ENTITY_NULL"),
    VIDEO_INFO_ID_LIST_NULL(140,"VIDEO_INFO_ID_LIST_NULL"),
    VIDEO_INFO_GUID_NULL(141,"VIDEO_INFO_GUID_NULL"),
    VIDEO_INFO_NF(142,"VIDEO_INFO_NF"),
    VIDEO_UPLOAD_FAILED(143,"VIDEO_UPLOAD_FAILED");


    private int id;
    private String name;

    private ErrorEnum(int id,String name)
    {
        this.id = id;
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public int getId()
    {
        return this.id;
    }
}
