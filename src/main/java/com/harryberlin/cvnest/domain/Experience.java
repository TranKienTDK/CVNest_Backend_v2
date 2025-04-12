package com.harryberlin.cvnest.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "experiences")
public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String company;
    String position;
    LocalDate startDate;
    LocalDate endDate;

    @Lob
    String description;

    String usageTechnologies;

    @ManyToOne
    @JoinColumn(name = "cv_id")
    @JsonBackReference
    CV cv;
}
