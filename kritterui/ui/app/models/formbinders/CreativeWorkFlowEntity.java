package models.formbinders;


public class CreativeWorkFlowEntity {
 
	private int creativeId;
	
	private String action;
	
	private String comment;
 
	
	public CreativeWorkFlowEntity() {
		super();
	}

	public CreativeWorkFlowEntity(int creativeId, String action) {
		super(); 
		this.creativeId = creativeId;
		this.action = action; 
	}
 
	public int getCreativeId() {
		return creativeId;
	}

	public void setCreativeId(int creativeId) {
		this.creativeId = creativeId;
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
