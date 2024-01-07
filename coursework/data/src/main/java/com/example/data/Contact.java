package com.example.data;

import jakarta.persistence.*;

@Entity
public class Contact {
  @Id @GeneratedValue private int id;

  @ManyToOne(cascade = CascadeType.MERGE)
  public ContactType type;

  public ContactValue value;
  public String comment;
  public boolean verified;
  public boolean preferred;
  public boolean need_verification;

  @Embeddable
  public static class ContactValue {
    public String city;
    public String number;
    public String country;
    public String formatted;
    public String email;

    public ContactValue() {}

    public ContactValue(String city, String country, String formatted, String number) {
      this.city = city;
      this.country = country;
      this.formatted = formatted;
      this.number = number;
    }

    public ContactValue(String email) {
      this.email = email;
    }
  }
}
