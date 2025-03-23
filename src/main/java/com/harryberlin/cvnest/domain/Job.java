package com.harryberlin.cvnest.domain;

import com.harryberlin.cvnest.util.constant.JobTypeEnum;
import com.harryberlin.cvnest.util.listener.JobEntityListener;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(JobEntityListener.class)
@Entity(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String title;

    @Enumerated(EnumType.STRING)
    JobTypeEnum type;
    LocalDate startDate;
    LocalDate endDate;
    boolean isActive;

    @Column(columnDefinition = "MEDIUMTEXT")
    String description;
    String experienceYear;
    Long startSalary;
    Long endSalary;

    @ManyToOne
    @JoinColumn(name = "company_id")
    Company company;


}
