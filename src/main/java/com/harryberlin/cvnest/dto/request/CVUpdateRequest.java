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
public class CVUpdateRequest {
    private int templateId;
    private String profile;
    private String cvName;

    private InfoUpdateRequest info;
    private List<ExperienceUpdateRequest> experiences;
    private List<EducationUpdateRequest> educations;
    private List<SkillCVUpdateRequest> skills;

    private List<LanguageUpdateRequest> languages;
    private List<ProjectUpdateRequest> projects;
    private List<InterestUpdateRequest> interests;
    private List<ConsultantUpdateRequest> consultants;
    private List<ActivityUpdateRequest> activities;
    private List<CertificateUpdateRequest> certificates;

    private String additionalInfo;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class InfoUpdateRequest {
        private String id;
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
    public static class ExperienceUpdateRequest {
        private String id;
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
    public static class EducationUpdateRequest {
        private String id;
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
    public static class SkillCVUpdateRequest {
        private String id;
        private String skill;
        private int rate;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LanguageUpdateRequest {
        private String id;
        private String language;
        private LevelLanguageEnum level;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ProjectUpdateRequest {
        private String id;
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
    public static class InterestUpdateRequest {
        private String id;
        private String interest;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ConsultantUpdateRequest {
        private String id;
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
    public static class ActivityUpdateRequest {
        private String id;
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
    public static class CertificateUpdateRequest {
        private String id;
        private String certificate;
        private String description;
        private LocalDate date;
    }
}