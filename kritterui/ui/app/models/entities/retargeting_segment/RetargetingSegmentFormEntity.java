package models.entities.retargeting_segment;

import org.springframework.beans.BeanUtils;

import com.kritter.entity.retargeting_segment.RetargetingSegment;

import play.data.validation.Constraints.Required;

import lombok.Getter;
import lombok.Setter;
import models.entities.Entity;

public class RetargetingSegmentFormEntity extends Entity{
    
    @Getter@Setter
    public int retargeting_segment_id = -1;
    @Getter@Setter@Required
    public String name;
    @Getter@Setter
    public String tag;
    @Getter@Setter
    public boolean is_deprecated = false;
    @Getter@Setter
    public String account_guid;
    @Getter@Setter
    private long created_on = 0;
    @Getter@Setter
    private long last_modified = 0;
    @Getter@Setter
    private int modified_by = 1;

    public RetargetingSegment getEntity(){
        RetargetingSegment retargetingSegment = new RetargetingSegment();
    	BeanUtils.copyProperties(this, retargetingSegment);
    	return retargetingSegment;
    }
    
     
    
}
