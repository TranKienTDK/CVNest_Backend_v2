package com.harryberlin.cvnest.dto.request;

import com.harryberlin.cvnest.util.constant.JobTypeEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobUpdateRequest {
    String id;
    String title;
    JobTypeEnum type;
    LocalDate startDate;
    LocalDate endDate;
    String experienceYear;
    boolean isActive;
    String description;
    Long startSalary;
    Long endSalary;
    String companyId;
}
