package com.example.common;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Certificate {
  @Id @GeneratedValue private int id;
  public String achieved_at;
  public String owner;
  public String title;
  public String type;
  public String url;
  public String middle_name;
}
