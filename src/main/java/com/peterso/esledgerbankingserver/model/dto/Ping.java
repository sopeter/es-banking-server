package com.peterso.esledgerbankingserver.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ping DTO that contains the time of the server.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Ping {

  private String serverTime;
}
