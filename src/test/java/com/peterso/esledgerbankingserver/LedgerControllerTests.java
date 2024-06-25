package com.peterso.esledgerbankingserver;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.peterso.esledgerbankingserver.controller.LedgerController;
import com.peterso.esledgerbankingserver.model.dto.Error;
import com.peterso.esledgerbankingserver.model.dto.Ping;
import com.peterso.esledgerbankingserver.service.LedgerService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class LedgerControllerTests {

  @Mock
  private LedgerService ledgerService;

  @InjectMocks
  private LedgerController ledgerController;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  /**
   * Tests the ping endpoint with no expected errors.
   */
  @Test
  public void testPing() {
    // Mock the behavior of ledgerService.getServerTime()
    String serverTime = "2024-04-30T20:04:48.539Z";
    Ping expectedPing = new Ping(serverTime);
    when(ledgerService.getServerTime()).thenReturn(serverTime);

    // Call the ping() method of the controller
    ResponseEntity<?> responseEntity = ledgerController.ping();

    // Verify the response
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Ping ping = (Ping) responseEntity.getBody();
    assertNotNull(ping);
    assertEquals(serverTime, ping.getServerTime());
    assertThat(ping).usingRecursiveComparison().isEqualTo(expectedPing);
  }

  /**
   * Tests the ping endpoint with an expected error.
   */
  @Test
  public void testPingError() {

    Error expectedError = new Error("java.lang.RuntimeException: Server error", Optional.of(HttpStatus.SERVICE_UNAVAILABLE.toString()));

    // Mock the behavior of ledgerService.getServerTime() to throw an exception
    when(ledgerService.getServerTime()).thenThrow(new RuntimeException("Server error"));

    // Call the ping() method of the controller
    ResponseEntity<?> responseEntity = ledgerController.ping();

    // Verify the response
    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, responseEntity.getStatusCode());

    // Check that the response contains the expected error message
    Error errorResponse = (Error) responseEntity.getBody();
    assertNotNull(errorResponse);
    assertTrue(errorResponse.getCode().isPresent());
    assertEquals("java.lang.RuntimeException: Server error", errorResponse.getMessage());
    assertEquals(HttpStatus.SERVICE_UNAVAILABLE.toString(), errorResponse.getCode().get());
    assertThat(errorResponse).usingRecursiveComparison().isEqualTo(expectedError);
  }
}
