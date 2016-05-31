
package models.entities.algo;

import com.kritter.entity.algomodel.AlgoModelEntity;


public class AlgoFormEntity extends AlgoFormBaseEntity{

	public AlgoFormEntity(AlgoModelEntity algoModelEntity){
		super(algoModelEntity);
	}
	
	public AlgoFormEntity(){
		super();
	}
	 
    public String getAdvertisers() {
        return idListToString(algoModelEntity.getAdvId());
    }

    public void setAdvertisers(String advertisers) { 
        algoModelEntity.setAdvId(stringToIdList(advertisers));
    } 
    public String getCampaigns() {
        return idListToString(algoModelEntity.getCampaignId());
    }

    public void setCampaigns(String campaigns) { 
        algoModelEntity.setCampaignId(stringToIdList(campaigns));
    }

    public String getAds() {
        return idListToString(algoModelEntity.getAdId());
    }
    public void setAds(String ads) {
        algoModelEntity.setAdId(stringToIdList(ads));
    }
    public int getAlgomodel() {
        return algoModelEntity.getAlgomodel();
    }
    public void setAlgomodel(int algomodel) {
        this.algoModelEntity.setAlgomodel(algomodel);
    } 
    public int getAlgomodelType() {
        return algoModelEntity.getAlgomodelType();
    }
    public void setAlgomodelType(int algomodelType) {
        this.algoModelEntity.setAlgomodelType(algomodelType);
    } 

    public AlgoModelEntity getReportEntity(){ 
        return algoModelEntity;
    }

}
