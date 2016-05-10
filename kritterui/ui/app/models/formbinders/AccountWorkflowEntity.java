package models.formbinders;

public class AccountWorkflowEntity {

	private String accountGuid; 

	private String action;

	private String comment;

	public AccountWorkflowEntity() {
		super();
	}
	
	

	public AccountWorkflowEntity(String accountGuid, String action) {
		super();
		this.accountGuid = accountGuid;
		this.action = action;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public AccountWorkflowEntity(String accountGuid, String action,
			String comment) {
		super();
		this.accountGuid = accountGuid;
		this.action = action;
		this.comment = comment;
	}

	public String getAccountGuid() {
		return accountGuid;
	}

	public void setAccountGuid(String accountGuid) {
		this.accountGuid = accountGuid;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	
	

}
