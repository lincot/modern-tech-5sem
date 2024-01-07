package com.example.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Portfolio {
  @Id @GeneratedValue private int id;
  public String description;
  public String medium;
  public String small;
}
