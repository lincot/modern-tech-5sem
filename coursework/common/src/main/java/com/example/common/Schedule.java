package com.example.common;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Schedule {
  @Id public String id;
  public String name;
}
