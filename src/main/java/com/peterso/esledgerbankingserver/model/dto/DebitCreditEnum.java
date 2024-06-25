package com.peterso.esledgerbankingserver.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum for Credit or Debit in a Transaction.
 */
public enum DebitCreditEnum {
  DEBIT("DEBIT"),
  CREDIT("CREDIT");

  private final String debitCreditType;

  DebitCreditEnum(String debitCreditType) {
    this.debitCreditType = debitCreditType;
  }

  /**
   * Converts any string representation of the enum to the enum.
   * Used to make endpoints case-insensitive for debitCreditEnum.
   * @param debitCreditType String representation of enum
   * @return enum representation of the string
   */
  @JsonCreator
  public static DebitCreditEnum fromString(String debitCreditType) {
    return debitCreditType == null ? null : valueOf(debitCreditType.toUpperCase());
  }

  /**
   * Returns the enum as a String
   * @return
   */
  @JsonValue
  public String getDebitCreditType() {
    return this.debitCreditType.toUpperCase();
  }
}

