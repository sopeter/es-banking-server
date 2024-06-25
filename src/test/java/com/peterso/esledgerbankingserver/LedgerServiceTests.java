package com.peterso.esledgerbankingserver;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.peterso.esledgerbankingserver.service.LedgerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = LedgerService.class)
public class LedgerServiceTests {

  private LedgerService ledgerService;

  @BeforeEach
  void setup() {
    ledgerService = new LedgerService();
  }

  /**
   * Tests the _getServerTime function by matching the format of the expected string.
   * Since the time is always changing, we cannot simply test for the outcome.
   */
  @Test
  public void formattedStringFromPingShouldMatchSchema() {
    LedgerService ledgerService = new LedgerService();
    String serverTime = ledgerService.getServerTime();
    assertTrue(serverTime.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z$"));
  }
}
