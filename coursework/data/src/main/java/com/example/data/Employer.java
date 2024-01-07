package com.example.data;

import jakarta.persistence.*;

@Entity
public class Employer {
  @Id public String id;
  public String alternate_url;
  public LogoUrls logo_urls;
  public String name;
  public String url;

  @Embeddable
  public static class LogoUrls {
    public String original;
    public String ninety;
    public String two_hundred_forty;
  }
}
