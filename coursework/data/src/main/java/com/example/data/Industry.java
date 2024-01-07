package com.example.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Industry {
  @Id public String id;
  public String name;
}
