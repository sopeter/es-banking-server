package com.peterso.esledgerbankingserver.validation;

import com.peterso.esledgerbankingserver.model.dto.DebitCreditEnum;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Constraint validation annotation used to ensure that a transaction request has the matching amount type
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE,
    ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
    validatedBy = {CheckAmountTypeValidator.class}
)
public @interface CheckAmountType {

  String message() default "Transaction type does not match amount type. Ensure that a Load request "
      + "contains CREDIT and an Authorization request contains DEBIT";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  DebitCreditEnum value();
}