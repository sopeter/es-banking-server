package com.peterso.esledgerbankingserver.validation;

import com.peterso.esledgerbankingserver.model.dto.Amount;
import com.peterso.esledgerbankingserver.model.dto.DebitCreditEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Constraint Validator
 */
public class CheckAmountTypeValidator implements ConstraintValidator<CheckAmountType, Amount> {

  private DebitCreditEnum amountType;

  /**
   * Initializes the Validator with a given amount type.
   * @param constraintAnnotation {@link CheckAmountType}
   */
  @Override
  public void initialize(CheckAmountType constraintAnnotation) {
    this.amountType = constraintAnnotation.value();
  }

  /**
   * Determines if the validation is met by checking that the type in the amount matches the given type.
   * @param value {@link Amount}
   * @param context {@link ConstraintValidatorContext}
   * @return boolean
   */
  @Override
  public boolean isValid(Amount value, ConstraintValidatorContext context) {
    return value.getDebitOrCredit() == amountType;
  }
}