package models.entities.ssp;

import com.kritter.api.entity.ssp.SSPEntity;

import play.data.validation.Constraints.Required;


public class SSPFormEntity {
    @Required
    private String dpa="-1";
    @Required
    private double ecpm = 0.0;
    public String getDpa() {
        return dpa;
    }
    public void setDpa(String dpa) {
        this.dpa = dpa;
    }
    public double getEcpm() {
        return ecpm;
    }
    public void setEcpm(double ecpm) {
        this.ecpm = ecpm;
    }
    public SSPEntity getEntity(){
        SSPEntity sspEntity = new SSPEntity();
        sspEntity.setDpa(this.dpa);
        sspEntity.setEcpm(this.ecpm);
        return sspEntity;
    }
    
}
