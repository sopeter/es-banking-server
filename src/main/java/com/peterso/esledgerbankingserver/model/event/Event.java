package com.peterso.esledgerbankingserver.model.event;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

/**
 * Abstract class representing an Event.
 * Contains a:
 * id (UUID),
 * createdTime (String)
 */
@Getter
public abstract class Event {
  public final UUID id = UUID.randomUUID();
  public final String createdTime = LocalDateTime.now().toString();
}
