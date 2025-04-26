package com.harryberlin.cvnest.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "cvs")
@DynamicUpdate
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

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    User user;

    /*
     * Main section fields: Experience, Skill, Education, Info
     */

    @OneToOne(mappedBy = "cv", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    
    Info info;

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    
    List<Experience> experiences;

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    
    List<SkillCV> skills;

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
//    
    @Fetch(FetchMode.SUBSELECT)
    List<Education> educations;


    /*
     * Other sections fields: Language, Project, Certificate, Activity, Interest, Consultant
     */
    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    List<Language> languages;

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    List<Project> projects;

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    List<Certificate> certificates;

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    List<Activity> activities;

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    List<Interest> interests;

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    List<Consultant> consultants;

//    @PrePersist
//    public void handleBeforeCreate() {
//        this.createdAt = LocalDateTime.now();
//    }
//
//    @PostUpdate
//    public void handleBeforeUpdate() {
//        this.updatedAt = LocalDateTime.now();
//    }
}
