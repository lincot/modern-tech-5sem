package com.example.common;

import jakarta.persistence.*;

@Entity
public class Site {
    @Id
    @GeneratedValue
    private int id;
    @ManyToOne(cascade = CascadeType.MERGE)
    public SiteType type;
    public String url;
}
