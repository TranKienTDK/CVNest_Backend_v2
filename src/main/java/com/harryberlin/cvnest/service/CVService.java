package com.harryberlin.cvnest.service;

import com.harryberlin.cvnest.domain.*;
import com.harryberlin.cvnest.dto.request.CVRequest;
import com.harryberlin.cvnest.dto.request.CVRequest.*;
import com.harryberlin.cvnest.dto.request.CVUpdateRequest;
import com.harryberlin.cvnest.dto.request.CVUpdateRequest.*;
import com.harryberlin.cvnest.exception.BaseException;
import com.harryberlin.cvnest.repository.CVRepository;
import com.harryberlin.cvnest.repository.UserRepository;
import com.harryberlin.cvnest.util.constant.Error;
import com.harryberlin.cvnest.util.constant.RoleEnum;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CVService {
    private final CVRepository cvRepository;
    private final UserRepository userRepository;

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
        cv.setUpdatedAt(LocalDateTime.now());

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

        if (user.getRole().equals(RoleEnum.HR)) {
            return this.cvRepository.findById(cvId)
                    .orElseThrow(() -> new BaseException(Error.CV_NOT_FOUND));
        }

        return this.cvRepository.findByIdAndUser(cvId, user)
                .orElseThrow(() -> new BaseException(Error.CV_NOT_FOUND));
    }

    @Transactional
    public void deleteCVById(String cvId) {
        CV cv = this.cvRepository.findById(cvId)
                .orElseThrow(() -> new BaseException(Error.CV_NOT_FOUND));
        var user = cv.getUser();
        user.deleteCV(cv.getId());
        this.userRepository.save(user);
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
            return new ArrayList<>();
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
        var experienceUpdate = new ExperienceUpdate(cv.getExperiences(), requests, cv);

        cv.setExperiences(experienceUpdate.handleRequests());
    }

    private List<SkillCV> mapToSkills(List<SkillCVRequest> requests, CV cv) {
        if (requests == null || requests.isEmpty()) {
            return new ArrayList<>();
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
        var skillUpdate = new SkillCVUpdate(cv.getSkills(), requests, cv);

        cv.setSkills(skillUpdate.handleRequests());
    }


    private List<Education> mapToEducations(List<EducationRequest> requests, CV cv) {
        if (requests == null || requests.isEmpty()) {
            return new ArrayList<>();
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
        var educationModal = new EducationUpdateModal(cv.getEducations(), requests, cv);
        cv.setEducations(educationModal.handleRequests());
    }

    private List<Language> mapToLanguages(List<LanguageRequest> requests, CV cv) {
        if (requests == null || requests.isEmpty()) {
            return new ArrayList<>();
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
        var updateLanguageModal = new UpdateLanguageModal(cv.getLanguages(), requests, cv);
        cv.setLanguages(updateLanguageModal.handleRequests());
    }

    private List<Project> mapToProjects(List<ProjectRequest> requests, CV cv) {
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
        var projectModal = new UpdateProjectModal(cv.getProjects(), requests, cv);
        cv.setProjects(projectModal.handleRequests());
    }

    private List<Certificate> mapToCertificates(List<CertificateRequest> requests, CV cv) {
        if (requests == null || requests.isEmpty()) {
            return new ArrayList<>();
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
        var updateCertificateModal = new UpdateCertificateModal(cv.getCertificates(), requests, cv);
        cv.setCertificates(updateCertificateModal.handleRequests());
    }

    private List<Activity> mapToActivities(List<ActivityRequest> requests, CV cv) {
        if (requests == null || requests.isEmpty()) {
            return new ArrayList<>();
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
        var activityModal = new UpdateActivitiesModal(cv.getActivities(), requests, cv);
        cv.setActivities(activityModal.handleRequests());
    }

    private List<Interest> mapToInterests(List<InterestRequest> requests, CV cv) {
        if (requests == null || requests.isEmpty()) {
            return new ArrayList<>();
        }

        return requests.stream()
                .map(req -> Interest.builder()
                        .interest(req.getInterest())
                        .cv(cv)
                        .build())
                .collect(Collectors.toList());
    }

    private void updateInterests(CV cv, List<InterestUpdateRequest> requests) {
        var updateInterestModal = new UpdateInterestModal(cv.getInterests(), requests, cv);
        cv.setInterests(updateInterestModal.handleRequests());
    }

    private List<Consultant> mapToConsultants(List<ConsultantRequest> requests, CV cv) {
        if (requests == null || requests.isEmpty()) {
            return new ArrayList<>();
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
        var updateConsultantModal = new UpdateConsultantModal(cv.getConsultants(), requests, cv);
        cv.setConsultants(updateConsultantModal.handleRequests());
    }

    public List<CV> getAllCVs() {
        log.info("Retrieving all CVs");
        return this.cvRepository.findAll();
    }

    private record UpdateCVModal<T>(List<T> addItems, Map<String, T> updateItems) {
        public static <T extends BaseRequest> UpdateCVModal<T> empty() {
            return new UpdateCVModal<>(Collections.emptyList(), Collections.emptyMap());
        }
    }

    private abstract static class AbstractUpdateModel<E extends BaseEntity, T extends BaseRequest> {
        @NotNull
        private final List<E> entities;
        @NotNull
        private final UpdateCVModal<T> updateCVModal;

        private final CV cv;

        private AbstractUpdateModel(List<E> entities, List<T> requests, CV cv) {
            this.entities = entities == null ? new ArrayList<>() : entities;
            this.updateCVModal = splitUpdateRequests(requests);
            this.cv = cv;
        }

        protected List<E> handleRequests() {
            this.doDelete();

            this.doAdd();

            this.doUpdate();

            return this.entities;
        }

        private void doUpdate() {
            if (updateCVModal.updateItems.isEmpty()) {
                return;
            }

            updateCVModal.updateItems
                    .forEach((id, request) -> {
                        var entity = this.entities.stream()
                                .filter(e -> Objects.equals(e.getId(), id))
                                .findFirst()
                                .orElse(null);
                        if (entity == null) {
                            return;
                        }
                        this.doUpdate(entity, request);
                    });
        }

        private void doAdd() {
            var addItems = updateCVModal.addItems;
            if (CollectionUtils.isEmpty(addItems)) {
                //
                return;
            }

            var newItems = addItems.stream()
                    .map(item -> {
                        var entity = this.createNewItem(item);
                        entity.setCv(this.cv);
                        return entity;
                    })
                    .toList();
            this.entities.addAll(newItems);
        }

        protected abstract E createNewItem(T request);

        protected abstract void doUpdate(E entity, T request);

        private void doDelete() {
            var updateMap = this.updateCVModal.updateItems;
            ///
            this.entities.removeIf(e -> !updateMap.containsKey(e.getId()));
        }

        private <U extends BaseRequest> UpdateCVModal<U> splitUpdateRequests(List<U> requests) {
            if (CollectionUtils.isEmpty(requests)) {
                return UpdateCVModal.empty();
            }

            List<U> addItems = new ArrayList<>();
            Map<String, U> updateItems = new HashMap<>();

            for (var request : requests) {
                if (request.getId() == null) {
                    addItems.add(request);
                } else {
                    updateItems.put(request.getId(), request);
                }
            }

            return new UpdateCVModal<>(addItems, updateItems);
        }
    }

    private final class ExperienceUpdate extends AbstractUpdateModel<Experience, ExperienceUpdateRequest> {

        private ExperienceUpdate(List<Experience> entities, List<ExperienceUpdateRequest> requests, CV cv) {
            super(entities, requests, cv);
        }

        @Override
        protected Experience createNewItem(ExperienceUpdateRequest request) {
            return Experience.builder()
                    .company(request.getCompany())
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .description(request.getDescription())
                    .usageTechnologies(request.getUsageTechnologies())
                    .position(request.getPosition())
                    .build();
        }

        @Override
        protected void doUpdate(Experience entity, ExperienceUpdateRequest request) {
            entity.setCompany(request.getCompany());
            entity.setStartDate(request.getStartDate());
            entity.setEndDate(request.getEndDate());
            entity.setDescription(request.getDescription());
            entity.setUsageTechnologies(request.getUsageTechnologies());
            entity.setPosition(request.getPosition());
        }
    }

    private final class SkillCVUpdate extends AbstractUpdateModel<SkillCV, SkillCVUpdateRequest> {
        public SkillCVUpdate(List<SkillCV> skills, List<SkillCVUpdateRequest> requests, CV cv) {
            super(skills, requests, cv);

        }

        @Override
        protected SkillCV createNewItem(SkillCVUpdateRequest request) {
            return SkillCV.builder()
                    .name(request.getSkill())
                    .rate(request.getRate())
                    .build();
        }

        @Override
        protected void doUpdate(SkillCV entity, SkillCVUpdateRequest request) {
            entity.setName(request.getSkill());
            entity.setRate(request.getRate());
        }
    }

    private class EducationUpdateModal extends AbstractUpdateModel<Education, EducationUpdateRequest> {

        private EducationUpdateModal(List<Education> entities, List<EducationUpdateRequest> requests, CV cv) {
            super(entities, requests, cv);
        }

        @Override
        protected Education createNewItem(EducationUpdateRequest request) {
            return Education.builder()
                    .school(request.getSchool())
                    .field(request.getField())
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .description(request.getDescription())
                    .build();
        }

        @Override
        protected void doUpdate(Education entity, EducationUpdateRequest request) {
            entity.setSchool(request.getSchool());
            entity.setField(request.getField());
            entity.setStartDate(request.getStartDate());
            entity.setEndDate(request.getEndDate());
            entity.setDescription(request.getDescription());
        }
    }

    private class UpdateProjectModal extends AbstractUpdateModel<Project, ProjectUpdateRequest> {
        private UpdateProjectModal(List<Project> entities, List<ProjectUpdateRequest> requests, CV cv) {
            super(entities, requests, cv);
        }

        @Override
        protected Project createNewItem(ProjectUpdateRequest request) {
            return Project.builder()
                    .project(request.getProject())
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .description(request.getDescription())
                    .build();
        }

        @Override
        protected void doUpdate(Project entity, ProjectUpdateRequest request) {
            entity.setProject(request.getProject());
            entity.setStartDate(request.getStartDate());
            entity.setEndDate(request.getEndDate());
            entity.setDescription(request.getDescription());
        }
    }

    private class UpdateCertificateModal extends AbstractUpdateModel<Certificate, CertificateUpdateRequest> {
        private UpdateCertificateModal(List<Certificate> entities, List<CertificateUpdateRequest> requests, CV cv) {
            super(entities, requests, cv);
        }

        @Override
        protected Certificate createNewItem(CertificateUpdateRequest request) {
            return Certificate.builder()
                    .certificate(request.getCertificate())
                    .date(request.getDate())
                    .description(request.getDescription())
                    .build();
        }

        @Override
        protected void doUpdate(Certificate entity, CertificateUpdateRequest request) {
            entity.setCertificate(request.getCertificate());
            entity.setDate(request.getDate());
            entity.setDescription(request.getDescription());
        }
    }

    private class UpdateActivitiesModal extends AbstractUpdateModel<Activity, ActivityUpdateRequest> {
        private UpdateActivitiesModal(List<Activity> entities, List<ActivityUpdateRequest> requests, CV cv) {
            super(entities, requests, cv);
        }

        @Override
        protected Activity createNewItem(ActivityUpdateRequest request) {
            return Activity.builder()
                    .activity(request.getActivity())
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .description(request.getDescription())
                    .build();
        }

        @Override
        protected void doUpdate(Activity entity, ActivityUpdateRequest request) {
            entity.setActivity(request.getActivity());
            entity.setStartDate(request.getStartDate());
            entity.setEndDate(request.getEndDate());
            entity.setDescription(request.getDescription());
        }
    }

    private class UpdateInterestModal extends AbstractUpdateModel<Interest, InterestUpdateRequest> {
        private UpdateInterestModal(List<Interest> entities, List<InterestUpdateRequest> requests, CV cv) {
            super(entities, requests, cv);
        }

        @Override
        protected Interest createNewItem(InterestUpdateRequest request) {
            return Interest.builder()
                    .interest(request.getInterest())
                    .build();
        }

        @Override
        protected void doUpdate(Interest entity, InterestUpdateRequest request) {
            entity.setInterest(request.getInterest());
        }
    }

    private class UpdateConsultantModal extends AbstractUpdateModel<Consultant, ConsultantUpdateRequest> {
        private UpdateConsultantModal(List<Consultant> entities, List<ConsultantUpdateRequest> requests, CV cv) {
            super(entities, requests, cv);
        }

        @Override
        protected Consultant createNewItem(ConsultantUpdateRequest request) {
            return Consultant.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .position(request.getPosition())
                    .phone(request.getPhone())
                    .build();
        }

        @Override
        protected void doUpdate(Consultant entity, ConsultantUpdateRequest request) {
            entity.setName(request.getName());
            entity.setEmail(request.getEmail());
            entity.setPosition(request.getPosition());
            entity.setPhone(request.getPhone());
        }
    }

    private class UpdateLanguageModal extends AbstractUpdateModel<Language, LanguageUpdateRequest> {
        private UpdateLanguageModal(List<Language> entities, List<LanguageUpdateRequest> requests, CV cv) {
            super(entities, requests, cv);
        }

        @Override
        protected Language createNewItem(LanguageUpdateRequest request) {
            return Language.builder()
                    .language(request.getLanguage())
                    .level(request.getLevel())
                    .build();
        }

        @Override
        protected void doUpdate(Language entity, LanguageUpdateRequest request) {
            entity.setLanguage(request.getLanguage());
            entity.setLevel(request.getLevel());
        }
    }
}
