package com.harryberlin.cvnest.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "cvs")
public class CV {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    int templateId;
    String cvName;

    @Lob
    String profile;

    @Lob
    String additionalInfo;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    User user;

    /*
     * Main section fields: Experience, Skill, Education, Info
     */

    @OneToOne(mappedBy = "cv", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    Info info;

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    List<Experience> experiences;

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    List<SkillCV> skills;

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    List<Education> educations;


    /*
     * Other sections fields: Language, Project, Certificate, Activity, Interest, Consultant
     */
    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    List<Language> languages;

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    List<Project> projects;

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    List<Certificate> certificates;

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    List<Activity> activities;

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    List<Interest> interests;

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    List<Consultant> consultants;
}
