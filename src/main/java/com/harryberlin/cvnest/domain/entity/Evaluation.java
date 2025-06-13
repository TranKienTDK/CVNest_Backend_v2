package com.harryberlin.cvnest.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "evaluations")
public class Evaluation {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private String id;

    private String cvId;
    private String jobId;

    Double score;

    @Lob
    private String explanation; 
    
    @ElementCollection
    List<String> skills;    String feedback;
    
    private String recommendedAction;
    
    private String actionReason;

    // Thời gian cập nhật của CV
    private LocalDateTime updatedAt;
}
