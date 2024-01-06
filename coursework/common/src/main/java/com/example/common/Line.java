package com.example.common;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Line {
    @Id
    public String id;
    public String name;
    public String hex_color;
}