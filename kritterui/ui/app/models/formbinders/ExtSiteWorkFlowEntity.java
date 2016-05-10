package models.formbinders;

public class ExtSiteWorkFlowEntity {

	private int id; 
	
	private String action;
	
	private String comment;

	
	public ExtSiteWorkFlowEntity() { 
		
	}

	public ExtSiteWorkFlowEntity(int id, String action) { 
		this.id = id;
		this.action = action; 
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
