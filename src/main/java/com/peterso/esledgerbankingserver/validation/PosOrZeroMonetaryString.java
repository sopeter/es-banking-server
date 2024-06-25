package com.peterso.esledgerbankingserver.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE,
    ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
    validatedBy = {PosOrZeroMonetaryStringValidator.class}
)
public @interface PosOrZeroMonetaryString {

  String message() default "String must be zero or a positive numerical monetary representation";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
