package com.peterso.esledgerbankingserver.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The service for the general ledger.
 */
@NoArgsConstructor
@Service
public class LedgerService {

  /**
   * Returns the local date time as a string formatted to fit the schema guideline.
   * @return
   */
  public String getServerTime() {
    LocalDateTime serverTime = LocalDateTime.now();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    return serverTime.format(formatter);
  }
}