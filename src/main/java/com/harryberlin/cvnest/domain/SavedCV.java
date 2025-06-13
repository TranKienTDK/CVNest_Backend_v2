package com.harryberlin.cvnest.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "saved_cv")
public class SavedCV {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String hrId;
    private String cvId;
}
