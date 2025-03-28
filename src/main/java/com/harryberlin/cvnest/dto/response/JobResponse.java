package com.harryberlin.cvnest.dto.response;

import com.harryberlin.cvnest.domain.Skill;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobResponse {
    String id;
    String title;
    String contract;
    String jobType;
    String level;
    LocalDate startDate;
    LocalDate endDate;
    boolean isActive;
    String experienceYear;
    String description;
    BigDecimal salary;
    String companyId;
    List<String> skillNames;
}
