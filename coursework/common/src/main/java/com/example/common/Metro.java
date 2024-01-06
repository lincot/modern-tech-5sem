package com.example.common;

import jakarta.persistence.*;

@Entity
public class Metro {
    @Id
    public String id;
    public double lat;
    @ManyToOne(cascade = CascadeType.MERGE)
    public Line line;
    public double lng;
    public String name;
    @Column(name = "metro_order")
    public int order;
}
