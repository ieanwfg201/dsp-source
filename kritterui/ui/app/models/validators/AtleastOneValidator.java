package models.validators;

import javax.validation.ConstraintValidator;

import play.libs.F.Tuple;

public class AtleastOneValidator   extends play.data.validation.Constraints.Validator<Object> 
        implements ConstraintValidator<AtleastOne, Object> {

    /* Default error message */
    final static public String message = "Select at least one value";

 

    /**
     * The validation itself
     */
    public boolean isValid(Object object) {
        if(object == null)
            return false;

        if(!(object instanceof String))
            return false;

        String s = object.toString();  
        if(s.length() < 3)
        	return false;
        	

        return true;
    }

    /**
     * Constructs a validator instance.
     */
    public static play.data.validation.Constraints.Validator<Object> atleastOne() {
        return new AtleastOneValidator();
    }
 
	public void initialize(AtleastOne arg0) { 
		
	}
 
	public Tuple<String, Object[]> getErrorMessageKey() { 
		return null;
	}
}