package models.entities.reporting;

import com.kritter.api.entity.reporting.ReportingEntity;
import com.kritter.constants.Frequency;


public class ReportFormEntity extends BaseReportEntity{

	public ReportFormEntity(ReportingEntity reportingEntity){
		super(reportingEntity);
	}
	
	public ReportFormEntity(){
		super();
	}
	 
	public int getPagesize() {
		return reportingEntity.getPagesize();
	}

	public void setPagesize(int pagesize) {
		this.reportingEntity.setPagesize(pagesize);
	} 
	
	public int getStartindex() {
		return this.reportingEntity.getStartindex();
	}
 
	public void setStartindex(int startindex) {
	    if(startindex > 0){
	        this.reportingEntity.setStartindex( startindex -1);
	    }else{
	        this.reportingEntity.setStartindex(0);
	    }
	} 
    public String getAdvid() {
        return idListToString(reportingEntity.getAdvId());
    }

    public void setAdvid(String advid) { 
        reportingEntity.setAdvId(stringToIdList(advid));
    }

	public String getPublishers() {
		return idListToString(reportingEntity.getPubId());
	}

	public void setPublishers(String publishers) { 
		reportingEntity.setPubId(stringToIdList(publishers));
	}

	public String getSites() {
		return idListToString(reportingEntity.getSiteId());
	}

	public void setSites(String sites) { 
		reportingEntity.setSiteId(stringToIdList(sites));
	} 
	public String getChannel() {
		return idListToString(reportingEntity.getChannelId());
	}

	public void setChannel(String channel) { 
		reportingEntity.setChannelId(stringToIdList(channel));
	} 
	public String getAdposition() {
		return idListToString(reportingEntity.getAdpositionId());
	}

	public void setAdposition(String adposition) { 
		reportingEntity.setAdpositionId(stringToIdList(adposition));
	} 
    public String getExtsites() {
        return idListToString(reportingEntity.getExt_site());
    }

    public void setExtsites(String extsites) { 
        reportingEntity.setExt_site(stringToIdList(extsites));
    } 
    public String getSupply_source_type() {
        return idListToString(reportingEntity.getSupply_source_type());
    }

    public void setSupply_source_type(String supply_source_type) { 
        reportingEntity.setSupply_source_type(stringToIdList(supply_source_type));
    }
	public String getAdvertisers() {
		return stringIdListToString(reportingEntity.getAdvertiserId());
	}

	public void setAdvertisers(String advertisers) { 
		reportingEntity.setAdvertiserId(stringToStringIdList(advertisers));
	}

	public String getCampaigns() {
		return idListToString(reportingEntity.getCampaignId());
	}

	public void setCampaigns(String campaigns) { 
		reportingEntity.setCampaignId(stringToIdList(campaigns));
	}

	public String getAds() {
		return idListToString(reportingEntity.getAdId());
	}

	public void setAds(String ads) { 
		reportingEntity.setAdId(stringToIdList(ads));
	}

	public String getCountry() {
		return idListToString(reportingEntity.getCountryId());
	}
	
	public void setCountry(String country) { 
		reportingEntity.setCountryId(stringToIdList(country));
	}

	public String getState() {
		return idListToString(reportingEntity.getStateId());
	}
	
	public void setState(String state) { 
		reportingEntity.setStateId(stringToIdList(state));
	}

	public String getCity() {
		return idListToString(reportingEntity.getCityeId());
	}
	
	public void setCity(String city) { 
		reportingEntity.setCityeId(stringToIdList(city));
	}

	public String getCarrier() {
		return idListToString(reportingEntity.getCountryCarrierId());
	}

	public void setCarrier(String carrier) { 
		reportingEntity.setCountryCarrierId(stringToIdList(carrier));
	}
	
	public String getConnection_type() {
	        return idListToString(reportingEntity.getConnection_type());
	}

	public void setConnection_type(String connection_type) { 
	        reportingEntity.setConnection_type(stringToIdList(connection_type));
	}

	public String getOs() {
		return idListToString(reportingEntity.getDeviceOsId());
	}

	public void setOs(String os) { 
		reportingEntity.setDeviceOsId(stringToIdList(os));
	}

	public String getBrowser() {
        return idListToString(reportingEntity.getBrowserId());
    }

    public void setBrowser(String browser) { 
        reportingEntity.setBrowserId(stringToIdList(browser));
    }

	public String getBrand() {
		return idListToString(reportingEntity.getDeviceManufacturerId());
	}

	public void setBrand(String brand) { 
		reportingEntity.setDeviceManufacturerId(stringToIdList(brand));
	}

	public String getModel() {
		return idListToString(reportingEntity.getDeviceModelId());
	}

	public void setModel(String model) { 
		reportingEntity.setDeviceModelId(stringToIdList(model));
	}

	

//	public boolean isAdvertiser_spend() {
//		return reportingEntity.is;
//	}
//
//	public void setAdvertiser_spend(boolean advertiser_spend) {
//		this.advertiser_spend = advertiser_spend;
//	}
//
//	public boolean isPublisher_revenue() {
//		return publisher_revenue;
//	}
//
//	public void setPublisher_revenue(boolean publisher_revenue) {
//		this.publisher_revenue = publisher_revenue;
//	}

	public ReportingEntity getReportEntity(){ 
		return reportingEntity;
	}

	public int getTop_n_for_last_x_hours() {
		return reportingEntity.getTop_n_for_last_x_hours(); 
	}

	public void setTop_n_for_last_x_hours(int top_n_for_last_x_hours) {
	    if(!"".equals(top_n_for_last_x_hours)){
	    	
	    	reportingEntity.setTop_n_for_last_x_hours(top_n_for_last_x_hours); 
	    }
	}
	
	public boolean getDate_as_dimension() {
        return reportingEntity.isDate_as_dimension();
    } 
	
    public void setDate_as_dimension(boolean date_as_dimension) {  
            reportingEntity.setDate_as_dimension(date_as_dimension); 
    }
    
    public String getFrequency(){
	    return reportingEntity.getFrequency().name();
	}
    
    public void setFrequency(String frequency) { 
        Frequency freq = Frequency.valueOf(frequency);
        if(freq != null){
            reportingEntity.setFrequency(freq);
        }
    }
    public boolean isRoundoffmetric() {
        return reportingEntity.isRoundoffmetric();
    }
    public void setRoundoffmetric(boolean roundoffmetric) {
        reportingEntity.setRoundoffmetric(roundoffmetric);
    }

    public String getCsvDelimiter() {
        return reportingEntity.getCsvDelimiter();
    }
    public void setCsvDelimiter(String csvDelimiter) {
        reportingEntity.setCsvDelimiter(csvDelimiter);
    }
    public String getNofillReason() {
        return stringIdListToString(reportingEntity.getNofillReason());
    }

    public void setNofillReason(String nofillReason) { 
        reportingEntity.setNofillReason(stringToStringIdList(nofillReason));
    }
    public String getReqState() {
        return stringIdListToString(reportingEntity.getReqState());
    }

    public void setReqState(String reqState) { 
        reportingEntity.setReqState(stringToStringIdList(reqState));
    }
    public String getDspNofill() {
        return stringIdListToString(reportingEntity.getDspNoFill());
    }

    public void setDspNofill(String dspNofill) { 
        reportingEntity.setDspNoFill(stringToStringIdList(dspNofill));
    }
    public String getTerminationReason() {
        return stringIdListToString(reportingEntity.getTerminationReason());
    }

    public void setTerminationReason(String terminationReason) { 
        reportingEntity.setTerminationReason(stringToStringIdList(terminationReason));
    }
    public String getPostimpevent() {
        return stringIdListToString(reportingEntity.getPostimpevent());
    }

    public void setPostimpevent(String postimpevent) { 
        reportingEntity.setPostimpevent(stringToStringIdList(postimpevent));
    }
    public String getTeventtype() {
        return stringIdListToString(reportingEntity.getTeventtype());
    }

    public void setTeventtype(String teventtype) { 
        reportingEntity.setTeventtype(stringToStringIdList(teventtype));
    }
	public String getMarketplace() {
		return idListToString(reportingEntity.getMarketplace());
	}
	
	public void setMarketplace(String marketplace) { 
		reportingEntity.setMarketplace(stringToIdList(marketplace));
	}
	
	public String getFormatId() {
		return idListToString(reportingEntity.getFormatId());
	}

	public void setFormatId(String formatId) { 
		reportingEntity.setFormatId(stringToIdList(formatId));
	}


}
