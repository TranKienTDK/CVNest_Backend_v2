package com.harryberlin.cvnest.dto.request;

import com.harryberlin.cvnest.util.constant.JobContractEnum;
import com.harryberlin.cvnest.util.constant.JobTypeEnum;
import com.harryberlin.cvnest.util.constant.LevelEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobCreateRequest {
    String title;
    JobContractEnum contract;
    JobTypeEnum jobType;
    LevelEnum level;
    String startDate;
    String endDate;
    String experienceYear;
    String description;
    BigDecimal salary;
    String companyId;
    List<String> skillIds;
}
