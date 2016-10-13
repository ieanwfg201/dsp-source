package models.entities;

import org.springframework.beans.BeanUtils;

import play.data.validation.Constraints.Required;
import com.kritter.entity.account.Qualification;

import lombok.Getter;
import lombok.Setter;




public class QualificationEntity {

    @Getter@Setter
    public Integer internalid=-1; 
    @Required@Getter@Setter
    public Integer advIncId; 
    @Required@Getter@Setter
    public String qname; /*qualification name*/
    @Required@Getter@Setter
    public String qurl; /*image cdn url*/
    @Required@Getter@Setter
    public String md5; /*image md5*/
    @Getter@Setter
    public Integer state; /*add,update,delete*/

    public Qualification getEntity(){
    	Qualification qualification = new Qualification();
    	BeanUtils.copyProperties(this, qualification);
    	return qualification;
    }
    
}
