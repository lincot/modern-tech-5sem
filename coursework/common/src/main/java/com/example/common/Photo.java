package com.example.common;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Photo {
    @Id
    @GeneratedValue
    private int id;
    @JsonProperty("40")
    public String size40;
    @JsonProperty("100")
    public String size100;
    @JsonProperty("500")
    public String size500;
    public String medium;
    public String small;
}