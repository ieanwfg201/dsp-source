package models.entities.isp_mapping;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.kritter.api.entity.isp_mapping.Isp_mapping;

import models.advertiser.EntityDisplay;
import models.uiutils.Path;


public class Isp_mappingDisplay extends EntityDisplay{

	protected Isp_mapping isp_mapping = null;

	public Isp_mappingDisplay(Isp_mapping isp_mapping){
		this.isp_mapping = isp_mapping;
	}

	public int getId(){
	    return isp_mapping.getId();
	}
    public String getCountryName(){
        return isp_mapping.getCountry_name();
    }
    public String getDataSourceName(){
        return isp_mapping.getData_source_name();
    }
    public String getIspName(){
        return isp_mapping.getIsp_name();
    }
    public boolean isEnableinsert(){
        if(isp_mapping.getIsp_ui_name()==null || "".equals(isp_mapping.getIsp_ui_name().trim()) || (isp_mapping.isIs_marked_for_deletion())){
            return true;
        }
        return false;
    }
    public String getIspUiName(){
        if(isp_mapping.getIsp_ui_name()==null){
            return "";
        }
        return isp_mapping.getIsp_ui_name();
    }
    public int getIspMappingId(){
        return isp_mapping.getIsp_mapping_id();
    }
    @Override
    public String getAccountName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getAccountGuid() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Path> getBreadCrumbPaths() {
        // TODO Auto-generated method stub
        return null;
    }
	
    public String getModifiedOn(){
        if(isp_mapping.getModified_on() != 0){
            Date date = new Date(isp_mapping.getModified_on());
            SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy"); 
            return dt.format(date);
        }
        return "";
    }

}
