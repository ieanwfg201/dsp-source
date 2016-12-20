package com.kritter.naterial_upload.valuemaker.entity;

/**
 * Created by zhangyan on 16-9-29.
 */

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode
public class VamMaterialUploadEntity {
    @Getter
    @Setter
    private String id;
    @Getter
    @Setter
    private String landingpage;
    @Getter
    @Setter
    private Integer width;//width 必须 Integer 创意宽度
    @Getter
    @Setter
    private Integer height;//height height 必须 Integer 创意高度
    @Getter
    @Setter
    private Integer format;
    @Getter
    @Setter
    private Integer adtype;
    @Getter
    @Setter
    private Integer category;
    @Getter
    @Setter
    private String[] adomain_list;
    @Getter
    @Setter
    private String[] pic_urls;
    @Getter
    @Setter
    private String title;
    @Getter
    @Setter
    private String text;

    public VamMaterialUploadEntity() {
    }

    public VamMaterialUploadEntity(String id, String landingpage, Integer width, Integer height, Integer format, Integer adtype,
                                      Integer category, String[] adomain_list, String[] pic_urls) {
        super();
        this.id = id;
        this.landingpage = landingpage;
        this.width = width;
        this.height = height;
        this.format = format;
        this.adtype = adtype;
        this.category = category;
        this.adomain_list = adomain_list;
        this.pic_urls = pic_urls;
    }


}
