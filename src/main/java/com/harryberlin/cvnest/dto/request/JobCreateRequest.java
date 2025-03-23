package com.harryberlin.cvnest.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobCreateRequest {
    String title;
    String type;
    String startDate;
    String endDate;
    String experienceYear;
    String description;
    Long startSalary;
    Long endSalary;
    String companyId;
}
