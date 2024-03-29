package ar.edu.itba.paw.webapp.form.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, FIELD, ANNOTATION_TYPE, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidImageValidatorConstraint.class)
public @interface ValidImage {

    String message() default "{ValidImage.formDataBodyPart}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
