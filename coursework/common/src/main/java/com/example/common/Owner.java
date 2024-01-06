package com.example.common;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Owner {
  @Id public String id;
  public Comments comments;

  @Embeddable
  public static class Comments {
    public String url;
    public Counters counters;

    @Embeddable
    public static class Counters {
      public int total;
    }
  }
}
