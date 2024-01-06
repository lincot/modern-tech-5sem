package com.example.common;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Relocation {
    @Id
    @GeneratedValue
    private int id;
    @ManyToMany(cascade = CascadeType.MERGE)
    public List<Area> area;
    @ManyToMany(cascade = CascadeType.MERGE)
    public List<District> district;
    @ManyToOne(cascade = CascadeType.MERGE)
    public RelocationType type;
}
