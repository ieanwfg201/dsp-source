
package models.entities.req_logging;

import com.kritter.entity.req_logging.ReqLoggingEntity;


public class ReqLoggingFormEntity{

    protected ReqLoggingEntity reqLoggingEntity = new ReqLoggingEntity();


	public ReqLoggingFormEntity(ReqLoggingEntity reqLoggingEntity ){
		this.reqLoggingEntity = reqLoggingEntity;
	}
	
	public ReqLoggingFormEntity(){
	}
	 
    public String getPubId() {
        return reqLoggingEntity.getPubId();
    }

    public void setPubId(String pubId) { 
        reqLoggingEntity.setPubId(pubId);
    } 
    public boolean isEnable() {
        return reqLoggingEntity.isEnable();
    }

    public void setEnable(boolean enable) { 
        reqLoggingEntity.setEnable(enable);
    }

    public int getTime_period() {
        return reqLoggingEntity.getTime_period();
    }
    public void setTime_period(int time_period) {
        reqLoggingEntity.setTime_period(time_period);
    }
    public ReqLoggingEntity getEntity(){ 
        return reqLoggingEntity;
    }
}
