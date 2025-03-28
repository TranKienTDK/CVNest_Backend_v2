package com.harryberlin.cvnest.elasticsearch.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.harryberlin.cvnest.util.constant.JobContractEnum;
import com.harryberlin.cvnest.util.constant.JobTypeEnum;
import com.harryberlin.cvnest.util.constant.LevelEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.elasticsearch.annotations.Document;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(indexName = "jobs")
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobDocument {
    @Id
    String id;
    String title;

    @Enumerated(EnumType.STRING)
    JobContractEnum contract;

    @Enumerated(EnumType.STRING)
    LevelEnum level;

    @Enumerated(EnumType.STRING)
    JobTypeEnum jobType;

    String experienceYear;
    boolean isActive;
    BigDecimal salary;
    String companyId;
    List<String> skillIds;
}
