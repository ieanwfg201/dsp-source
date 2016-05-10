package com.kritter.serving.demand.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import com.kritter.constants.CreativeFormat;
import com.kritter.entity.creative_macro.CreativeMacro;
import com.kritter.entity.native_props.demand.NativeDemandProps;
import com.kritter.entity.video_props.VideoProps;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * This class represents a creative that could be used by many ad units.
 * A creative is the representation of what has to be shown to end user.
 */

@Getter
@ToString
@EqualsAndHashCode(of={"creativeGuid"})
public class Creative implements IUpdatableEntity<Integer>
{
    private final Integer creativeIncId;
    private final String creativeGuid;
    private final String accountGuid;
    private final CreativeFormat creativeFormat;
    private final Short[] creativeAttributes;
    private final String text;
    private final Integer[] bannerUriIds;
    private final String htmlContent;
    private final String externalResourceURL;
    private final boolean markedForDeletion;
    private final Long modificationTime;
    private final NativeDemandProps native_demand_props;
    private final CreativeMacro creative_macro;
    private final VideoProps videoProps;
    
    public Creative(CreativeEntityBuilder creativeEntityBuilder)
    {
        this.creativeIncId = creativeEntityBuilder.creativeIncId;
        this.creativeGuid = creativeEntityBuilder.creativeGuid;
        this.accountGuid = creativeEntityBuilder.accountGuid;
        this.creativeFormat = creativeEntityBuilder.creativeFormat;
        this.creativeAttributes = creativeEntityBuilder.creativeAttributes;
        this.text = creativeEntityBuilder.text;
        this.bannerUriIds = creativeEntityBuilder.bannerUriIds;
        this.htmlContent = creativeEntityBuilder.htmlContent;
        this.externalResourceURL = creativeEntityBuilder.externalResourceURL;
        this.modificationTime = creativeEntityBuilder.modificationTime;
        this.markedForDeletion = creativeEntityBuilder.isMarkedForDeletion;
        this.native_demand_props = creativeEntityBuilder.native_demand_props;
        this.creative_macro = creativeEntityBuilder.creative_macro;
        this.videoProps = creativeEntityBuilder.videoProps;
    }

    @Override
    public Integer getId()
    {
        return this.creativeIncId;
    }

    public static class CreativeEntityBuilder
    {
        private final Integer creativeIncId;
        private final String creativeGuid;
        private final String accountGuid;
        private final CreativeFormat creativeFormat;
        private final Short[] creativeAttributes;
        private String text;
        private Integer[] bannerUriIds;
        private String htmlContent;
        private String externalResourceURL;
        private final boolean isMarkedForDeletion;
        private final Long modificationTime;
        private NativeDemandProps native_demand_props;
        private CreativeMacro creative_macro;
        private VideoProps videoProps;

        public CreativeEntityBuilder(Integer creativeIncId,
                                     String creativeGuid,
                                     String accountGuid,
                                     CreativeFormat creativeFormat,
                                     Short[] creativeAttributes,
                                     boolean isMarkedForDeletion,
                                     long modificationTime,
                                     NativeDemandProps native_demand_props,
                                     CreativeMacro creative_macro,
                                     VideoProps videoProps)
        {
            this.creativeIncId = creativeIncId;
            this.creativeGuid = creativeGuid;
            this.accountGuid = accountGuid;
            this.creativeFormat = creativeFormat;
            this.creativeAttributes = creativeAttributes;
            this.isMarkedForDeletion = isMarkedForDeletion;
            this.modificationTime = modificationTime;
            this.native_demand_props = native_demand_props;
            this.creative_macro = creative_macro;
            this.videoProps = videoProps;
        }

        public CreativeEntityBuilder setText(String text)
        {
            this.text = text;
            return this;
        }

        public CreativeEntityBuilder setBannerUriIds(Integer[] bannerUriIds)
        {
            this.bannerUriIds = bannerUriIds;
            return this;
        }

        public CreativeEntityBuilder setHtmlContent(String htmlContent)
        {
            this.htmlContent = htmlContent;
            return this;
        }

        public CreativeEntityBuilder setExternalResourceURL(String externalResourceURL) {
            this.externalResourceURL = externalResourceURL;
            return this;
        }

        public Creative build()
        {
            return new Creative(this);
        }
    }
}
