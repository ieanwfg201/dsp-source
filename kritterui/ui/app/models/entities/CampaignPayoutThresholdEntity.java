package models.entities;


import play.data.validation.Constraints.Required;

import com.kritter.entity.payout_threshold.CampaignPayoutThreshold;

import lombok.Getter;
import lombok.Setter;

public class CampaignPayoutThresholdEntity {
	
	@Getter@Setter
    private int campaign_id = -1;
    @Getter@Setter
    private float absolute_threshold = -1.0f;
    @Getter@Setter
    private float percentage_threshold = -1.0f;
    
    public CampaignPayoutThreshold getEntity(){
    	CampaignPayoutThreshold entity=new CampaignPayoutThreshold();
    	entity.setCampaign_id(this.campaign_id);
    	entity.setAbsolute_threshold(this.absolute_threshold);
    	entity.setPercentage_threshold(this.percentage_threshold);
    	return entity;
    }
    
     
    
}
