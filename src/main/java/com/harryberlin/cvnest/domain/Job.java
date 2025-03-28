package com.harryberlin.cvnest.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.harryberlin.cvnest.util.constant.JobContractEnum;
import com.harryberlin.cvnest.util.constant.JobTypeEnum;
import com.harryberlin.cvnest.util.constant.LevelEnum;
import jakarta.persistence.*;
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
@Entity(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String title;

    @Enumerated(EnumType.STRING)
    JobContractEnum contract;

    @Enumerated(EnumType.STRING)
    LevelEnum level;

    @Enumerated(EnumType.STRING)
    JobTypeEnum jobType;

    LocalDate startDate;
    LocalDate endDate;
    boolean isActive;

    @Lob
    String description;
    String experienceYear;
    BigDecimal salary;

    @ManyToOne
    @JoinColumn(name = "company_id")
    @JsonIgnore
    Company company;

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "jobs" })
    List<Skill> skills;

}
