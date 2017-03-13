package models.entities.audience;

import com.kritter.api.entity.audience.Audience;
import lombok.Getter;
import lombok.Setter;
import models.entities.Entity;
import play.data.validation.Constraints.Required;
import org.springframework.beans.BeanUtils;
import com.kritter.constants.AudienceSourceEnum;
import com.kritter.constants.AudienceTypeEnum;
/**
 * Created by zhangyan on 2/3/17.
 */
public class AudienceEntity extends Entity {

    @Getter@Setter
    private int id = -1 ;

    @Getter@Setter@Required
    private String name =null;

    @Getter@Setter
    private String source_id = null;

    @Getter@Setter
    private String tags = null;

    @Getter@Setter
    private int type = AudienceTypeEnum.MMA_TAG.getCode();//audience_type   1:audience code,  2 audience package

    @Getter@Setter
    private String file_path = null;

    @Getter@Setter
    private int deleted = 1 ;//0:没删除，1：删除

    @Getter@Setter
    private long created_on = 0;

    @Getter@Setter
    private long last_modified = 0;

    @Getter@Setter
    private String account_guid;

    public Audience getEntity(){
        Audience audience = new Audience();
        BeanUtils.copyProperties(this, audience);
        return audience;
    }
}
