package com.example.common;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Specialization {
  @Id public String id;
  public String name;
  public boolean laboring;
  public String profarea_id;
  public String profarea_name;
}
