package com.kritter.bidreqres.entity.cloudcross4_4_4;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestSiteDTO;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CloudCrossBidRequestSiteDTO extends BidRequestSiteDTO {
    @JsonProperty("cat")
    @Setter
    private String contentCategoriesForSite;

    @JsonProperty("keywords")
    @Setter
    private String[] siteKeywordsCSV;

    @JsonProperty("pagecat")
    @Setter
    private String pageContentCategories;

    @JsonProperty("sectioncat")
    @Setter
    private String contentCategoriesSubsectionSite;

    @Override
    public String[] getContentCategoriesSubsectionSite() {
        String[] strings = new String[1];
        strings[0] = contentCategoriesSubsectionSite;
        return strings;
    }

    @Override
    public String[] getPageContentCategories() {
        String[] strings = new String[1];
        strings[0] = pageContentCategories;
        return strings;
    }

    @Override
    public String getSiteKeywordsCSV() {
        return siteKeywordsCSV != null && siteKeywordsCSV.length > 0 ? siteKeywordsCSV[0] : "";
    }

    @Override
    public String[] getContentCategoriesForSite() {
        String[] strings = new String[1];
        strings[0] = contentCategoriesForSite;
        return strings;
    }
}