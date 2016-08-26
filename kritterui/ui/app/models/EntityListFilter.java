package models;

import com.kritter.constants.StatusIdEnum;

import models.Constants.EntityType;
import models.Constants.PageType;

public class EntityListFilter {
	
	private int pageNumber;
	
	private int pageSize; 
 
	private EntityType entityType;
	
	private StatusIdEnum status;
	
	private String accountType;
	
	private String accountGuid;  
	
	private int campaignId;
	
	private int countryId;
	
	private int exchangeId;
	
	private String osId;

	private String dealGuid;
	
	private PageType pageType = PageType.none;
	
	/** Created getiddefinition */
	private String ids;
	    private int id_guid;
	private int get_type;
	
	public PageType getPageType() {
        return pageType;
    }

    public void setPageType(PageType pageType) {
        this.pageType = pageType;
    }

    public String getAccountGuid() {
		return accountGuid;
	}

	public void setAccountGuid(String accountGuid) {
		this.accountGuid = accountGuid;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}

	public StatusIdEnum getStatus() {
		return status;
	}

	public void setStatus(StatusIdEnum status) {
		this.status = status;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public int getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(int campaignId) {
		this.campaignId = campaignId;
	}

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public int getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(int exchangeId) {
        this.exchangeId = exchangeId;
    }

    public String getOsId() {
        return osId;
    }

    public void setOsId(String osId) {
        this.osId = osId;
    }
    
    public String getIds() {
        return ids;
    }

	public void setDealGuid(String dealGuid){this.dealGuid = dealGuid;}

	public String getDealGuid(){return this.dealGuid;}

    public void setIds(String ids) {
        this.ids = ids;
    }

    public int getId_guid() {
        return id_guid;
    }

    public void setId_guid(int id_guid) {
        this.id_guid = id_guid;
    }

    public int getGet_type() {
        return get_type;
    }

    public void setGet_type(int get_type) {
        this.get_type = get_type;
    }


}
