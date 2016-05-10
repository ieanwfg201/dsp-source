package models.validators; 
import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import javax.validation.*;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = AtleastOneValidator.class)
@play.data.Form.Display(name="constraint.alluppercase")
public @interface AtleastOne {
    String message() default AtleastOneValidator.message;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}