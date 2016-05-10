package models;


import java.util.ArrayList;
import java.util.List;

import models.formelements.SelectOption;
import securesocial.core.java.SecureSocial;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.kritter.constants.Payment_type;

public class StaticUtils {

	public static String loggedinUserName(){
		try{
			if(SecureSocial.currentUser()!=null)
				return SecureSocial.currentUser().fullName();
		}catch(Exception e){

		}
		return "";
	}
	
	public static int loggedinUserId(){
        try{
            if(SecureSocial.currentUser()!=null)
                return ((AccountIdentity)SecureSocial.currentUser()).getId();
        }catch(Exception e){

        }
        return 1;
    }
 
	public static List<SelectOption> getPaymentOptions(){
		List<SelectOption>  selectOptions = new ArrayList<SelectOption>();

		Payment_type[] types = Payment_type.values();
		for (Payment_type payment_type : types) {
			selectOptions.add(new SelectOption(payment_type.getName(), payment_type.name()));
		}
		return selectOptions;
	}
    public static ArrayNode getPaymentOptionsArray(){
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        Payment_type[] types = Payment_type.values();
        for (Payment_type payment_type : types) {
            optionNodes.add(new SelectOption(payment_type.getName(), payment_type.name()).toJson());
        }
        return optionNodes;
    }

 
	
}

 
	
 
