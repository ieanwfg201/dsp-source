package models.entities.adxbasedexchanges;

import models.entities.Entity;
import play.data.validation.Constraints.Required;

import org.springframework.beans.BeanUtils;
import com.kritter.entity.adxbasedexchanges_metadata.AdxBasedExchangesMetadata;

import lombok.Getter;
import lombok.Setter;

public class AdxBasedExchangesMetadataEntity extends Entity{
    @Getter@Setter
    private int internalid = -1;
    @Required@Getter@Setter
    private Integer pubIncId;
    @Getter@Setter
    private boolean advertiser_upload=false;
    @Getter@Setter
    private boolean adposition_get=false;
    @Getter@Setter
    private boolean banner_upload=false;
    @Getter@Setter
    private boolean video_upload=false;
    @Getter@Setter
    private long last_modified = 0;

    public AdxBasedExchangesMetadata getEntity(){
    	AdxBasedExchangesMetadata adxBased = new AdxBasedExchangesMetadata();
    	BeanUtils.copyProperties(this, adxBased);
    	return adxBased;
    }
}
