package models.formbinders;


public class AdWorkFlowEntity {
 
	private int adId;
	
	private String action;
	
	private String comment;
 
	
	public AdWorkFlowEntity() {
		super();
	}

	public AdWorkFlowEntity(int adId, String action) {
		super(); 
		this.adId = adId;
		this.action = action; 
	}
 
	public int getAdId() {
		return adId;
	}

	public void setAdId(int adId) {
		this.adId = adId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	
}
