package com.example.common;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Currency {
  @Id @GeneratedValue private int id;
  public String abbr;
  public String code;
  public String name;
}
