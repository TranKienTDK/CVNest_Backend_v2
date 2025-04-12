package com.harryberlin.cvnest.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "skills_cv")
public class SkillCV {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;

    // Rate is a number from 1 to 5
    int rate;

    @ManyToOne
    @JoinColumn(name = "cv_id")
    @JsonBackReference
    CV cv;
}
