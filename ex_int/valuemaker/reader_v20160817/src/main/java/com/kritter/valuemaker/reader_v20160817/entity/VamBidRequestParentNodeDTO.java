package com.kritter.valuemaker.reader_v20160817.entity;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestParentNodeDTO;
import com.kritter.constants.SITE_PLATFORM;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VamBidRequestParentNodeDTO extends BidRequestParentNodeDTO {

    @Setter
    @Getter
    private SITE_PLATFORM sitePlatform; //app or site

    @Setter
    @Getter
    private List<Integer> battr;

}
