package com.example.common;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Area {
  @Id public String id;
  public String url;
  public String name;
}
