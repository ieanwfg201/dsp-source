package models.entities.payout;

import com.kritter.entity.payout_threshold.DefaultPayoutThreshold;
import lombok.Getter;
import lombok.Setter;
import play.data.validation.Constraints.Required;


public class PayoutDefaultFormEntity {
    @Required@Getter@Setter
    private String name;
    @Required@Getter@Setter
    private float value ;
    public DefaultPayoutThreshold getEntity(){
    	DefaultPayoutThreshold entity = new DefaultPayoutThreshold();
    	entity.setName(this.name);
    	entity.setValue(this.value);
        return entity;
    }
    
}
