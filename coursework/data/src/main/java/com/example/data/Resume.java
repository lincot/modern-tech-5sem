package com.example.data;

import jakarta.persistence.*;
import java.util.List;

/** Класс, представляющий резюме с различными характеристиками и атрибутами. */
@Entity
public class Resume {
  @Id public String id;
  public int age;

  @ManyToOne(cascade = CascadeType.MERGE)
  public Area area;

  @ManyToMany(cascade = CascadeType.MERGE)
  public List<Site> site;

  @ManyToOne(cascade = CascadeType.MERGE)
  public Metro metro;

  @ManyToOne(cascade = CascadeType.MERGE)
  public Owner owner;

  @ManyToOne(cascade = CascadeType.MERGE)
  public Photo photo;

  public String title;

  @ManyToOne(cascade = CascadeType.MERGE)
  public Gender gender;

  public Salary salary;

  @Column(length = 2000)
  public String skills;

  public Actions actions;

  @ManyToMany(cascade = CascadeType.MERGE)
  public List<Contact> contact;

  public Download download;

  @ManyToMany(cascade = CascadeType.MERGE)
  public List<Language> language;

  @ManyToOne(cascade = CascadeType.MERGE)
  public Schedule schedule;

  @ManyToOne(cascade = CascadeType.MERGE)
  public Education education;

  public boolean favorited;
  public String last_name;

  @ManyToMany(cascade = CascadeType.MERGE)
  public List<Portfolio> portfolio;

  @ElementCollection public List<Schedule> schedules;
  @ElementCollection public List<String> skill_set;
  public String birth_date;
  public String created_at;

  @ManyToOne(cascade = CascadeType.MERGE)
  public Employment employment;

  @ManyToMany(cascade = CascadeType.MERGE)
  public List<Experience> experience;

  public String first_name;

  @ManyToOne(cascade = CascadeType.MERGE)
  public Relocation relocation;

  public String updated_at;

  @ManyToMany(cascade = CascadeType.MERGE)
  public List<Certificate> certificate;

  @ManyToMany(cascade = CascadeType.MERGE)
  @JoinTable(name = "Resume_citizenship")
  public List<Area> citizenship;

  @ManyToMany(cascade = CascadeType.MERGE) // merge
  public List<Employment> employments;

  public boolean has_vehicle;
  public String middle_name;

  @ManyToOne(cascade = CascadeType.MERGE)
  public Platform platform;

  @ManyToOne(cascade = CascadeType.MERGE)
  public TravelTime travel_time;

  @ManyToMany(cascade = CascadeType.MERGE) // merge
  @JoinTable(name = "Resume_work_ticket")
  public List<Area> work_ticket;

  public String alternate_url;

  @ManyToMany(cascade = CascadeType.MERGE)
  public List<HiddenField> hidden_fields;

  @ElementCollection public List<PaidService> paid_services;

  @ManyToOne(cascade = CascadeType.MERGE)
  public ResumeLocale resume_locale;

  @ManyToMany(cascade = CascadeType.MERGE)
  public List<ProfessionalRole> professional_roles;

  @ElementCollection public List<Recommendation> recommendation;

  @ManyToMany(cascade = CascadeType.MERGE)
  public List<Specialization> specialization;

  public TotalExperience total_experience;
  public boolean can_view_full_info;

  @ManyToMany(cascade = CascadeType.MERGE)
  public List<DriverLicenseType> driver_license_types;

  public NegotiationsHistory negotiations_history;

  @ManyToOne(cascade = CascadeType.MERGE)
  public BusinessTripReadiness business_trip_readiness;

  public double rating;

  @Embeddable
  public static class Download {
    public Pdf pdf;
    public Rtf rtf;

    @Embeddable
    public static class Pdf {
      @Column(name = "pdf_url")
      public String url;
    }

    @Embeddable
    public static class Rtf {
      @Column(name = "rtf_url")
      public String url;
    }
  }

  @Embeddable
  public static class Salary {
    public Double amount;
    public String currency;
  }

  @Embeddable
  public static class Actions {
    @AttributeOverrides({
      @AttributeOverride(name = "pdf.url", column = @Column(name = "download_pdf_url")),
      @AttributeOverride(name = "rtf.url", column = @Column(name = "download_rtf_url")),
    })
    public Download download;

    @AttributeOverrides({
      @AttributeOverride(
          name = "pdf.url",
          column = @Column(name = "download_with_contact_pdf_url")),
      @AttributeOverride(
          name = "rtf.url",
          column = @Column(name = "download_with_contact_rtf_url")),
    })
    public Download download_with_contact;

    public GetWithContact get_with_contact;

    @Embeddable
    public static class GetWithContact {
      @Column(name = "get_with_contact_url")
      public String url;
    }
  }

  @Embeddable
  public static class PaidService {
    @Column(name = "paid_service_id")
    public String id;

    @Column(name = "paid_service_name")
    public String name;

    @Column(name = "paid_service_active")
    public boolean active;

    @Column(name = "paid_service_expires")
    public String expires;

    @Column(name = "paid_service_description")
    public String description;

    public PriceList price_list;
    public QuickPurchase quick_purchase;

    @Embeddable
    public static class PriceList {
      @Column(name = "price_list_alternate_url")
      public String alternate_url;
    }

    @Embeddable
    public static class QuickPurchase {
      @Column(name = "quick_purchase_alternate_url")
      public String alternate_url;

      @ManyToOne(cascade = CascadeType.MERGE)
      public Currency currency;

      @Column(name = "quick_purchase_name")
      public String name;

      @Column(name = "quick_purchase_price")
      public double price;
    }
  }

  @Embeddable
  public static class Recommendation {
    @Column(name = "recommendation_contact")
    public String contact;

    @Column(name = "recommendation_name")
    public String name;

    @Column(name = "recommendation_organization")
    public String organization;

    @Column(name = "recommendation_position")
    public String position;
  }

  @Embeddable
  public static class TotalExperience {
    public Integer months;
  }

  @Embeddable
  public static class NegotiationsHistory {
    public String url;
  }
}
