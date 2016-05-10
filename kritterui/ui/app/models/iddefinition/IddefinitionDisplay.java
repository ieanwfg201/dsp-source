package models.iddefinition;


import java.util.List;
import com.kritter.api.entity.iddefinition.Iddefinition;
import models.advertiser.EntityDisplay;
import models.uiutils.Path;

public class IddefinitionDisplay extends EntityDisplay{

	protected Iddefinition iddefinition = null;

	public IddefinitionDisplay(Iddefinition iddefinition){
		this.iddefinition = iddefinition;
	}

	public int getId(){
	    return iddefinition.getId();
	}
    public String getGuid(){
        return iddefinition.getGuid();
    }
    public String getName(){
        return iddefinition.getName();
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
	
}
