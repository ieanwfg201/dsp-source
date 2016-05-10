package models.formbinders;

public class IOWorkFlowEntity {

	private String accountGuid;
	
	private String ioNumber;
	
	private String action;
	
	private String comment;

	
	public IOWorkFlowEntity() {
		super();
	}

	public IOWorkFlowEntity(String accountGuid, String ioNumber, String action) {
		super();
		this.accountGuid = accountGuid;
		this.ioNumber = ioNumber;
		this.action = action;
	}

	public String getAccountGuid() {
		return accountGuid;
	}

	public void setAccountGuid(String accountGuid) {
		this.accountGuid = accountGuid;
	}

	public String getIoNumber() {
		return ioNumber;
	}

	public void setIoNumber(String ioNumber) {
		this.ioNumber = ioNumber;
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
