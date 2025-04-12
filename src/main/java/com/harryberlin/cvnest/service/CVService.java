package com.harryberlin.cvnest.service;

import com.harryberlin.cvnest.domain.*;
import com.harryberlin.cvnest.dto.request.CVRequest;
import com.harryberlin.cvnest.dto.request.CVRequest.*;
import com.harryberlin.cvnest.dto.request.CVUpdateRequest;
import com.harryberlin.cvnest.dto.request.CVUpdateRequest.*;
import com.harryberlin.cvnest.exception.BaseException;
import com.harryberlin.cvnest.repository.CVRepository;
import com.harryberlin.cvnest.util.constant.Error;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CVService {
    private final CVRepository cvRepository;

    @Transactional
    public CV createCV(CVRequest request, User user) {
        log.info("Creating new CV with template ID: {} for user: {}", request.getTemplateId(), user.getId());

        CV cv = CV.builder()
                .templateId(request.getTemplateId())
                .profile(request.getProfile())
                .cvName(request.getCvName())
                .additionalInfo(request.getAdditionalInfo())
                .user(user)
                .build();
        
        buildCVRelationships(cv, request);
        
        if (user.getCvs() == null) {
            user.setCvs(Collections.singletonList(cv));
        } else {
            user.getCvs().add(cv);
        }
        
        CV savedCV = cvRepository.save(cv);
        log.info("CV created successfully with ID: {}", savedCV.getId());
        
        return savedCV;
    }

    @Transactional
    public CV updateCV(String cvId, CVUpdateRequest request, User user) {
        log.info("Updating CV with ID: {} for user: {}", cvId, user.getId());
        
        CV cv = cvRepository.findById(cvId)
                .orElseThrow(() -> new BaseException(Error.CV_NOT_FOUND));
        
        if (!cv.getUser().getId().equals(user.getId())) {
            throw new BaseException(Error.NOT_AUTHORIZED);
        }
        
        cv.setTemplateId(request.getTemplateId());
        cv.setProfile(request.getProfile());
        cv.setCvName(request.getCvName());
        cv.setAdditionalInfo(request.getAdditionalInfo());
        
        updateCVRelationships(cv, request);
        
        CV updatedCV = cvRepository.save(cv);
        log.info("CV updated successfully with ID: {}", updatedCV.getId());
        
        return updatedCV;
    }

    public Page<CV> getAllCVsByUser(User user, Pageable pageable) {
        log.info("Retrieving CVs for user: {} with pagination: {}", user.getId(), pageable);
        return this.cvRepository.findByUser(user, pageable);
    }

    public CV getCVByIdAndUser(String cvId, User user) {
        log.info("Retrieving CV with ID: {} for user: {}", cvId, user.getId());

        return this.cvRepository.findByIdAndUser(cvId, user)
                .orElseThrow(() -> new BaseException(Error.CV_NOT_FOUND));
    }

    public void deleteCV(String cvId, User user) {
        log.info("Deleting CV with ID: {} for user: {}", cvId, user.getId());

        CV cv = this.cvRepository.findById(cvId)
                .orElseThrow(() -> new BaseException(Error.CV_NOT_FOUND));

        if (!cv.getUser().getId().equals(user.getId())) {
            throw new BaseException(Error.NOT_AUTHORIZED);
        }

        this.cvRepository.delete(cv);
        log.info("CV deleted successfully with ID: {}", cvId);
    }

    private void buildCVRelationships(CV cv, CVRequest request) {
        // Xử lý Info
        if (request.getInfo() != null) {
            Info info = mapToInfo(request.getInfo(), cv);
            cv.setInfo(info);
        }
        
        // Xử lý Experiences
        List<Experience> experiences = mapToExperiences(request.getExperiences(), cv);
        cv.setExperiences(experiences);
        
        // Xử lý Skills
        List<SkillCV> skills = mapToSkills(request.getSkills(), cv);
        cv.setSkills(skills);
        
        // Xử lý Educations
        List<Education> educations = mapToEducations(request.getEducations(), cv);
        cv.setEducations(educations);
        
        // Xử lý Languages
        List<Language> languages = mapToLanguages(request.getLanguages(), cv);
        cv.setLanguages(languages);
        
        // Xử lý Projects
        List<Project> projects = mapToProjects(request.getProjects(), cv);
        cv.setProjects(projects);
        
        // Xử lý Certificates
        List<Certificate> certificates = mapToCertificates(request.getCertificates(), cv);
        cv.setCertificates(certificates);
        
        // Xử lý Activities
        List<Activity> activities = mapToActivities(request.getActivities(), cv);
        cv.setActivities(activities);
        
        // Xử lý Interests
        List<Interest> interests = mapToInterests(request.getInterests(), cv);
        cv.setInterests(interests);
        
        // Xử lý Consultants
        List<Consultant> consultants = mapToConsultants(request.getConsultants(), cv);
        cv.setConsultants(consultants);
    }

    private void updateCVRelationships(CV cv, CVUpdateRequest request) {
        if (request.getInfo() != null) {
            updateInfo(cv, request.getInfo());
        }
        
        if (request.getExperiences() != null) {
            updateExperiences(cv, request.getExperiences());
        }
        
        if (request.getSkills() != null) {
            updateSkills(cv, request.getSkills());
        }
        
        if (request.getEducations() != null) {
            updateEducations(cv, request.getEducations());
        }
        
        if (request.getLanguages() != null) {
            updateLanguages(cv, request.getLanguages());
        }
        
        if (request.getProjects() != null) {
            updateProjects(cv, request.getProjects());
        }
        
        if (request.getCertificates() != null) {
            updateCertificates(cv, request.getCertificates());
        }
        
        if (request.getActivities() != null) {
            updateActivities(cv, request.getActivities());
        }
        
        if (request.getInterests() != null) {
            updateInterests(cv, request.getInterests());
        }
        
        if (request.getConsultants() != null) {
            updateConsultants(cv, request.getConsultants());
        }
    }

    private Info mapToInfo(InfoRequest request, CV cv) {
        if (request == null) return null;
        
        return Info.builder()
                .fullName(request.getFullName())
                .address(request.getAddress())
                .city(request.getCity())
                .avatar(request.getAvatar())
                .dob(request.getDob())
                .email(request.getEmail())
                .gender(request.getGender())
                .phone(request.getPhone())
                .position(request.getPosition())
                .linkedin(request.getLinkedin())
                .github(request.getGithub())
                .cv(cv)
                .build();
    }

    private void updateInfo(CV cv, InfoUpdateRequest request) {
        Info info = cv.getInfo();
        if (info == null) {
            info = Info.builder()
                    .cv(cv)
                    .build();
        }
        
        info.setFullName(request.getFullName());
        info.setAddress(request.getAddress());
        info.setCity(request.getCity());
        info.setAvatar(request.getAvatar());
        info.setDob(request.getDob());
        info.setEmail(request.getEmail());
        info.setGender(request.getGender());
        info.setPhone(request.getPhone());
        info.setPosition(request.getPosition());
        info.setLinkedin(request.getLinkedin());
        info.setGithub(request.getGithub());
        
        cv.setInfo(info);
    }

    private List<Experience> mapToExperiences(List<ExperienceRequest> requests, CV cv) {
        if (requests == null || requests.isEmpty()) {
            return Collections.emptyList();
        }
        
        return requests.stream()
                .map(req -> Experience.builder()
                        .company(req.getCompany())
                        .position(req.getPosition())
                        .description(req.getDescription())
                        .startDate(req.getStartDate())
                        .endDate(req.getEndDate())
                        .usageTechnologies(req.getUsageTechnologies())
                        .cv(cv)
                        .build())
                .collect(Collectors.toList());
    }

    private void updateExperiences(CV cv, List<ExperienceUpdateRequest> requests) {
        if (requests.isEmpty()) {
            cv.getExperiences().clear();
            return;
        }
        
        List<Experience> existing = cv.getExperiences() != null ? cv.getExperiences() : Collections.emptyList();
        List<Experience> updated = requests.stream()
                .map(req -> {
                    if (req.getId() != null) {
                        Optional<Experience> existingExp = existing.stream()
                                .filter(e -> e.getId().equals(req.getId()))
                                .findFirst();
                        
                        if (existingExp.isPresent()) {
                            Experience exp = existingExp.get();
                            exp.setCompany(req.getCompany());
                            exp.setPosition(req.getPosition());
                            exp.setDescription(req.getDescription());
                            exp.setStartDate(req.getStartDate());
                            exp.setEndDate(req.getEndDate());
                            exp.setUsageTechnologies(req.getUsageTechnologies());
                            return exp;
                        }
                    }
                    
                    return Experience.builder()
                            .company(req.getCompany())
                            .position(req.getPosition())
                            .description(req.getDescription())
                            .startDate(req.getStartDate())
                            .endDate(req.getEndDate())
                            .usageTechnologies(req.getUsageTechnologies())
                            .cv(cv)
                            .build();
                })
                .collect(Collectors.toList());
        
        cv.setExperiences(updated);
    }

    private List<SkillCV> mapToSkills(List<SkillCVRequest> requests, CV cv) {
        if (requests == null || requests.isEmpty()) {
            return Collections.emptyList();
        }
        
        return requests.stream()
                .map(req -> SkillCV.builder()
                        .name(req.getSkill())
                        .rate(req.getRate())
                        .cv(cv)
                        .build())
                .collect(Collectors.toList());
    }

    private void updateSkills(CV cv, List<SkillCVUpdateRequest> requests) {
        if (requests.isEmpty()) {
            cv.getSkills().clear();
            return;
        }
        
        List<SkillCV> existing = cv.getSkills() != null ? cv.getSkills() : Collections.emptyList();
        List<SkillCV> updated = requests.stream()
                .map(req -> {
                    if (req.getId() != null) {
                        Optional<SkillCV> existingSkill = existing.stream()
                                .filter(s -> s.getId().equals(req.getId()))
                                .findFirst();
                        
                        if (existingSkill.isPresent()) {
                            SkillCV skill = existingSkill.get();
                            skill.setName(req.getSkill());
                            skill.setRate(req.getRate());
                            return skill;
                        }
                    }
                    
                    return SkillCV.builder()
                            .name(req.getSkill())
                            .rate(req.getRate())
                            .cv(cv)
                            .build();
                })
                .collect(Collectors.toList());
        
        cv.setSkills(updated);
    }

    private List<Education> mapToEducations(List<EducationRequest> requests, CV cv) {
        if (requests == null || requests.isEmpty()) {
            return Collections.emptyList();
        }
        
        return requests.stream()
                .map(req -> Education.builder()
                        .school(req.getSchool())
                        .field(req.getField())
                        .description(req.getDescription())
                        .startDate(req.getStartDate())
                        .endDate(req.getEndDate())
                        .cv(cv)
                        .build())
                .collect(Collectors.toList());
    }

    private void updateEducations(CV cv, List<EducationUpdateRequest> requests) {
        if (requests.isEmpty()) {
            cv.getEducations().clear();
            return;
        }
        
        List<Education> existing = cv.getEducations() != null ? cv.getEducations() : Collections.emptyList();
        List<Education> updated = requests.stream()
                .map(req -> {
                    if (req.getId() != null) {
                        Optional<Education> existingEdu = existing.stream()
                                .filter(e -> e.getId().equals(req.getId()))
                                .findFirst();
                        
                        if (existingEdu.isPresent()) {
                            Education edu = existingEdu.get();
                            edu.setSchool(req.getSchool());
                            edu.setField(req.getField());
                            edu.setDescription(req.getDescription());
                            edu.setStartDate(req.getStartDate());
                            edu.setEndDate(req.getEndDate());
                            return edu;
                        }
                    }
                    
                    return Education.builder()
                            .school(req.getSchool())
                            .field(req.getField())
                            .description(req.getDescription())
                            .startDate(req.getStartDate())
                            .endDate(req.getEndDate())
                            .cv(cv)
                            .build();
                })
                .collect(Collectors.toList());
        
        cv.setEducations(updated);
    }

    private List<Language> mapToLanguages(List<LanguageRequest> requests, CV cv) {
        if (requests == null || requests.isEmpty()) {
            return Collections.emptyList();
        }
        
        return requests.stream()
                .map(req -> Language.builder()
                        .language(req.getLanguage())
                        .level(req.getLevel())
                        .cv(cv)
                        .build())
                .collect(Collectors.toList());
    }

    private void updateLanguages(CV cv, List<LanguageUpdateRequest> requests) {
        if (requests.isEmpty()) {
            cv.getLanguages().clear();
            return;
        }
        
        List<Language> existing = cv.getLanguages() != null ? cv.getLanguages() : Collections.emptyList();
        List<Language> updated = requests.stream()
                .map(req -> {
                    if (req.getId() != null) {
                        Optional<Language> existingLang = existing.stream()
                                .filter(l -> l.getId().equals(req.getId()))
                                .findFirst();
                        
                        if (existingLang.isPresent()) {
                            Language lang = existingLang.get();
                            lang.setLanguage(req.getLanguage());
                            lang.setLevel(req.getLevel());
                            return lang;
                        }
                    }
                    
                    return Language.builder()
                            .language(req.getLanguage())
                            .level(req.getLevel())
                            .cv(cv)
                            .build();
                })
                .collect(Collectors.toList());
        
        cv.setLanguages(updated);
    }

    private List<Project> mapToProjects(List<ProjectRequest> requests, CV cv) {
        if (requests == null || requests.isEmpty()) {
            return Collections.emptyList();
        }
        
        return requests.stream()
                .map(req -> Project.builder()
                        .project(req.getProject())
                        .description(req.getDescription())
                        .startDate(req.getStartDate())
                        .endDate(req.getEndDate())
                        .cv(cv)
                        .build())
                .collect(Collectors.toList());
    }

    private void updateProjects(CV cv, List<ProjectUpdateRequest> requests) {
        if (requests.isEmpty()) {
            cv.getProjects().clear();
            return;
        }
        
        List<Project> existing = cv.getProjects() != null ? cv.getProjects() : Collections.emptyList();
        List<Project> updated = requests.stream()
                .map(req -> {
                    if (req.getId() != null) {
                        Optional<Project> existingProj = existing.stream()
                                .filter(p -> p.getId().equals(req.getId()))
                                .findFirst();
                        
                        if (existingProj.isPresent()) {
                            Project proj = existingProj.get();
                            proj.setProject(req.getProject());
                            proj.setDescription(req.getDescription());
                            proj.setStartDate(req.getStartDate());
                            proj.setEndDate(req.getEndDate());
                            return proj;
                        }
                    }
                    
                    return Project.builder()
                            .project(req.getProject())
                            .description(req.getDescription())
                            .startDate(req.getStartDate())
                            .endDate(req.getEndDate())
                            .cv(cv)
                            .build();
                })
                .collect(Collectors.toList());
        
        cv.setProjects(updated);
    }

    private List<Certificate> mapToCertificates(List<CertificateRequest> requests, CV cv) {
        if (requests == null || requests.isEmpty()) {
            return Collections.emptyList();
        }
        
        return requests.stream()
                .map(req -> Certificate.builder()
                        .certificate(req.getCertificate())
                        .description(req.getDescription())
                        .date(req.getDate())
                        .cv(cv)
                        .build())
                .collect(Collectors.toList());
    }

    private void updateCertificates(CV cv, List<CertificateUpdateRequest> requests) {
        if (requests.isEmpty()) {
            cv.getCertificates().clear();
            return;
        }
        
        List<Certificate> existing = cv.getCertificates() != null ? cv.getCertificates() : Collections.emptyList();
        List<Certificate> updated = requests.stream()
                .map(req -> {
                    if (req.getId() != null) {
                        Optional<Certificate> existingCert = existing.stream()
                                .filter(c -> c.getId().equals(req.getId()))
                                .findFirst();
                        
                        if (existingCert.isPresent()) {
                            Certificate cert = existingCert.get();
                            cert.setCertificate(req.getCertificate());
                            cert.setDescription(req.getDescription());
                            cert.setDate(req.getDate());
                            return cert;
                        }
                    }
                    
                    return Certificate.builder()
                            .certificate(req.getCertificate())
                            .description(req.getDescription())
                            .date(req.getDate())
                            .cv(cv)
                            .build();
                })
                .collect(Collectors.toList());
        
        cv.setCertificates(updated);
    }

    private List<Activity> mapToActivities(List<ActivityRequest> requests, CV cv) {
        if (requests == null || requests.isEmpty()) {
            return Collections.emptyList();
        }
        
        return requests.stream()
                .map(req -> Activity.builder()
                        .activity(req.getActivity())
                        .description(req.getDescription())
                        .startDate(req.getStartDate())
                        .endDate(req.getEndDate())
                        .cv(cv)
                        .build())
                .collect(Collectors.toList());
    }

    private void updateActivities(CV cv, List<ActivityUpdateRequest> requests) {
        if (requests.isEmpty()) {
            cv.getActivities().clear();
            return;
        }
        
        List<Activity> existing = cv.getActivities() != null ? cv.getActivities() : Collections.emptyList();
        List<Activity> updated = requests.stream()
                .map(req -> {
                    if (req.getId() != null) {
                        Optional<Activity> existingAct = existing.stream()
                                .filter(a -> a.getId().equals(req.getId()))
                                .findFirst();
                        
                        if (existingAct.isPresent()) {
                            Activity act = existingAct.get();
                            act.setActivity(req.getActivity());
                            act.setDescription(req.getDescription());
                            act.setStartDate(req.getStartDate());
                            act.setEndDate(req.getEndDate());
                            return act;
                        }
                    }
                    
                    return Activity.builder()
                            .activity(req.getActivity())
                            .description(req.getDescription())
                            .startDate(req.getStartDate())
                            .endDate(req.getEndDate())
                            .cv(cv)
                            .build();
                })
                .collect(Collectors.toList());
        
        cv.setActivities(updated);
    }

    private List<Interest> mapToInterests(List<InterestRequest> requests, CV cv) {
        if (requests == null || requests.isEmpty()) {
            return Collections.emptyList();
        }
        
        return requests.stream()
                .map(req -> Interest.builder()
                        .interest(req.getInterest())
                        .cv(cv)
                        .build())
                .collect(Collectors.toList());
    }

    private void updateInterests(CV cv, List<InterestUpdateRequest> requests) {
        if (requests.isEmpty()) {
            cv.getInterests().clear();
            return;
        }
        
        List<Interest> existing = cv.getInterests() != null ? cv.getInterests() : Collections.emptyList();
        List<Interest> updated = requests.stream()
                .map(req -> {
                    if (req.getId() != null) {
                        Optional<Interest> existingInt = existing.stream()
                                .filter(i -> i.getId().equals(req.getId()))
                                .findFirst();
                        
                        if (existingInt.isPresent()) {
                            Interest interest = existingInt.get();
                            interest.setInterest(req.getInterest());
                            return interest;
                        }
                    }
                    
                    return Interest.builder()
                            .interest(req.getInterest())
                            .cv(cv)
                            .build();
                })
                .collect(Collectors.toList());
        
        cv.setInterests(updated);
    }

    private List<Consultant> mapToConsultants(List<ConsultantRequest> requests, CV cv) {
        if (requests == null || requests.isEmpty()) {
            return Collections.emptyList();
        }
        
        return requests.stream()
                .map(req -> Consultant.builder()
                        .name(req.getName())
                        .email(req.getEmail())
                        .position(req.getPosition())
                        .phone(req.getPhone())
                        .cv(cv)
                        .build())
                .collect(Collectors.toList());
    }

    private void updateConsultants(CV cv, List<ConsultantUpdateRequest> requests) {
        if (requests.isEmpty()) {
            cv.getConsultants().clear();
            return;
        }
        
        List<Consultant> existing = cv.getConsultants() != null ? cv.getConsultants() : Collections.emptyList();
        List<Consultant> updated = requests.stream()
                .map(req -> {
                    if (req.getId() != null) {
                        Optional<Consultant> existingCons = existing.stream()
                                .filter(c -> c.getId().equals(req.getId()))
                                .findFirst();
                        
                        if (existingCons.isPresent()) {
                            Consultant cons = existingCons.get();
                            cons.setName(req.getName());
                            cons.setEmail(req.getEmail());
                            cons.setPosition(req.getPosition());
                            cons.setPhone(req.getPhone());
                            return cons;
                        }
                    }
                    
                    return Consultant.builder()
                            .name(req.getName())
                            .email(req.getEmail())
                            .position(req.getPosition())
                            .phone(req.getPhone())
                            .cv(cv)
                            .build();
                })
                .collect(Collectors.toList());
        
        cv.setConsultants(updated);
    }
}
