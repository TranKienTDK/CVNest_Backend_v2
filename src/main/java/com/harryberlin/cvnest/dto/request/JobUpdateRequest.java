package com.harryberlin.cvnest.dto.request;

import com.harryberlin.cvnest.util.constant.JobContractEnum;
import com.harryberlin.cvnest.util.constant.JobTypeEnum;
import com.harryberlin.cvnest.util.constant.LevelEnum;
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
public class JobUpdateRequest {
    String id;
    String title;
    JobContractEnum contract;
    LevelEnum level;
    JobTypeEnum jobType;
    LocalDate startDate;
    LocalDate endDate;
    String experienceYear;
    boolean isActive;
    String description;
    BigDecimal salary;
    String companyId;
    List<String> skillIds;
}
