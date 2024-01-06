package com.example.common;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Experience {
  @Id @GeneratedValue private int id;

  @ManyToOne(cascade = CascadeType.MERGE)
  public Area area;

  public String company;
  public String company_id;
  public String company_url;
  public String description;

  @ManyToOne(cascade = CascadeType.MERGE)
  public Employer employer;

  @Column(name = "experience_end")
  public String end;

  @ManyToMany(cascade = CascadeType.MERGE)
  public List<Industry> industries;

  @ManyToOne(cascade = CascadeType.MERGE)
  public Industry industry; // deprecated

  public String position;
  public String start;
}
