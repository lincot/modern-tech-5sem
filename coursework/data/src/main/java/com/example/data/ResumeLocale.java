package com.example.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ResumeLocale {
  @Id public String id;
  public String name;
}
