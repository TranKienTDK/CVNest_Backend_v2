package com.harryberlin.cvnest.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationCriteria {
    private String jobTitle;
    private List<String> programmingLanguages;
    private Integer yearsOfExperience;
    private List<String> certifications;
    private String degreeRequirement;
    private List<LanguageRequirement> languageRequirements;
    private String contractType;


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LanguageRequirement {
        private String language;
        private String level;
    }
}
