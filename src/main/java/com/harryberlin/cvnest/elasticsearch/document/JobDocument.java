package com.harryberlin.cvnest.elasticsearch.document;

import com.harryberlin.cvnest.util.constant.JobTypeEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(indexName = "jobs")
public class JobDocument {
    @Id
    String id;
    String title;

    @Enumerated(EnumType.STRING)
    JobTypeEnum type;
    LocalDate startDate;
    LocalDate endDate;
    String experienceYear;
    boolean isActive;
    String description;
    Long startSalary;
    Long endSalary;
    String companyName;
}
