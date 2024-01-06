package com.example.common;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Language {
    @Id
    public String id;
    public String name;
    @ManyToOne(cascade = CascadeType.MERGE)
    public LanguageLevel level;
}
