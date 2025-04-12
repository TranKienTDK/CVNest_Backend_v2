package com.harryberlin.cvnest.dto.request;

import com.harryberlin.cvnest.util.constant.GenderEnum;
import com.harryberlin.cvnest.util.constant.LevelLanguageEnum;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CVRequest {
    private int templateId;
    private String profile;
    private String cvName;

    private InfoRequest info;
    private List<ExperienceRequest> experiences;
    private List<EducationRequest> educations;
    private List<SkillCVRequest> skills;

    private List<LanguageRequest> languages;
    private List<ProjectRequest> projects;
    private List<InterestRequest> interests;
    private List<ConsultantRequest> consultants;
    private List<ActivityRequest> activities;
    private List<CertificateRequest> certificates;

    private String additionalInfo;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class InfoRequest {
        private String fullName;
        private String address;
        private String city;
        private String avatar;
        private LocalDate dob;
        private String email;
        private GenderEnum gender;
        private String phone;
        private String position;
        private String profile;
        private String linkedin;
        private String github;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ExperienceRequest {
        private String company;
        private String position;
        private String description;
        private LocalDate startDate;
        private LocalDate endDate;
        private String usageTechnologies;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class EducationRequest {
        private String school;
        private String field;
        private String description;
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SkillCVRequest {
        private String skill;
        private int rate;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LanguageRequest {
        private String language;
        private LevelLanguageEnum level;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ProjectRequest {
        private String project;
        private String description;
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class InterestRequest {
        private String interest;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ConsultantRequest {
        private String name;
        private String email;
        private String position;
        private String phone;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ActivityRequest {
        private String activity;
        private String description;
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CertificateRequest {
        private String certificate;
        private String description;
        private LocalDate date;
    }
}
