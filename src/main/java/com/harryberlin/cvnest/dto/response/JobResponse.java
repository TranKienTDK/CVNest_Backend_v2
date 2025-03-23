package com.harryberlin.cvnest.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobResponse {
    String id;
    String title;
    String type;
    LocalDate startDate;
    LocalDate endDate;
    boolean isActive;
    String experienceYear;
    String description;
    Long startSalary;
    Long endSalary;
    String companyId;
}
