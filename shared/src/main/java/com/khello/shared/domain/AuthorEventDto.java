package com.khello.shared.domain;

import java.util.List;
import java.util.UUID;

import com.khello.shared.models.DtoBase;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AuthorEventDto extends DtoBase {
  private String name;
  private String description;
  private List<UUID> books;
}
