package com.peterso.esledgerbankingserver.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

/**
 * Validator class for custom PosOrZeroString annotation.
 */
public class PosOrZeroMonetaryStringValidator implements
    ConstraintValidator<PosOrZeroMonetaryString, String> {

  /**
   * Determines if the validation is met by checking if the value is 0 or positive and that the maximum amount of decimals
   * is 2.
   * @param value
   * @param constraintValidatorContext
   * @return
   */
  @Override
  public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
    try {
      BigDecimal valueAsBD = new BigDecimal(value);
      int decimals = getNumberOfDecimalPlaces(valueAsBD);
      return valueAsBD.compareTo(BigDecimal.ZERO) >= 0 && decimals <= 2;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Returns the number of decimals places found in the BigDecimal by using the index of the cleaned
   * big decimal.
   * @param value {@link BigDecimal}
   * @return number of decimal places found
   */
  private int getNumberOfDecimalPlaces(BigDecimal value) {
    String strippedString = value.stripTrailingZeros().toPlainString();
    int index = strippedString.indexOf(".");
    return index < 0 ? 0 : strippedString.length() - index - 1;
  }
}
