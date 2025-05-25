package com.harryberlin.cvnest.domain;

import com.harryberlin.cvnest.util.constant.CVStatusEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "applies")
public class Apply {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private String id;
    private String jobId;
    private String cvId;
    private String evaluationId;
    private LocalDateTime appliedAt;
    private CVStatusEnum status;
}
