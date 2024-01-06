package com.example.common;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Education {
    @Id
    @GeneratedValue
    private int id;
    @ElementCollection
    public List<AdditionalEducation> additional;
    @ElementCollection
    public List<Attestation> attestation;
    @ElementCollection
    public List<ElementaryEducation> elementary;
    @ManyToOne(cascade = CascadeType.MERGE)
    public EducationLevel level;
    @ElementCollection
    public List<HigherEducation> primary;

    @Embeddable
    public static class AdditionalEducation {
        public String name;
        public String organization;
        public String result;
        @Column(name = "additional_education_year")
        public int year;
    }

    @Embeddable
    public static class Attestation {
        public String name;
        public String organization;
        public String result;
        @Column(name = "attestation_year")
        public int year;
    }

    @Embeddable
    public static class ElementaryEducation {
        public String name;
        @Column(name = "elementary_education_year")
        public int year;
    }

    @Embeddable
    public static class HigherEducation {
        public String name;
        public String name_id;
        public String organization;
        public String organization_id;
        public String result;
        public String result_id;
        @Column(name = "higher_education_year")
        public int year;
    }
}
