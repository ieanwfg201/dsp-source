package models.formbinders;

public class SiteWorkFlowEntity {

	private int siteId; 
	
	private String action;
	
	private String comment;

	
	public SiteWorkFlowEntity() { 
		
	}

	public SiteWorkFlowEntity(int siteId, String action) { 
		this.siteId = siteId;
		this.action = action; 
	}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
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
