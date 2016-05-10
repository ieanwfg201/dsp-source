package models.advertiser;

import java.text.SimpleDateFormat;
import java.util.Date;

import services.DataAPI;

import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.insertion_order.Insertion_Order;
 

public class IoDisplay {
	
	private Insertion_Order io = null;
	
	public IoDisplay(Insertion_Order io){
		this.io = io;
	}
	
	public String getOrderNumber(){
		return io.getOrder_number();
	}
	
	public String getName(){
		return io.getName();
	}
	public String getCreatedDate(){		
		Date date = new Date(io.getCreated_on());
		SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy"); 
		return dt.format(date);
	}
	
	public String getLastModifiedDate(){		
		Date date = new Date(io.getLast_modified());
		SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy"); 
		return dt.format(date);
	}
	
	public String getAccountGuid(){				 
		return io.getAccount_guid(); 
	}
	
	public String getAccountName(){				 
		Account account = DataAPI.getAccountByGuid(io.getAccount_guid());
		return account.getName();
	}
	
	public double getOrderValue(){
		return io.getTotal_value();
	}
	
	public String getComment(){
		return io.getComment();
	}
	
    public String getCreatedByName(){
        return io.getCreated_by_name();
    }
	
    public String getBelongsToName(){
        return io.getBelongs_to_name();
    }
	
	public String getAccountUrl(){		
		return controllers.advertiser.routes.AdvertiserController.home(io.getAccount_guid()).url();
	}
	
	public String getApproveUrl(){		
		return controllers.advertiser.routes.AdvertiserController.home(io.getAccount_guid()).url();
	}

	public String getRejectUrl(){		
		return controllers.advertiser.routes.AdvertiserController.home(io.getAccount_guid()).url();
	}
}
