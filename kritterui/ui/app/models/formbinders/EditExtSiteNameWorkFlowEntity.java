package models.formbinders;

public class EditExtSiteNameWorkFlowEntity {

	private int id; 
	
	private String action;
	
	private String comment;

	
	public EditExtSiteNameWorkFlowEntity() { 
		
	}

	public EditExtSiteNameWorkFlowEntity(int id, String action, String comment) { 
		this.id = id;
		this.action = action; 
		this.comment = comment;
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
