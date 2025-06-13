package com.harryberlin.cvnest.service;

import com.harryberlin.cvnest.domain.Apply;
import com.harryberlin.cvnest.domain.CV;
import com.harryberlin.cvnest.domain.Job;
import com.harryberlin.cvnest.domain.User;
import com.harryberlin.cvnest.event.notification.ApplySummittedEvent;
import com.harryberlin.cvnest.event.notification.ApplyApprovedEvent;
import com.harryberlin.cvnest.event.notification.ApplyRejectedEvent;
import com.harryberlin.cvnest.exception.BaseException;
import com.harryberlin.cvnest.repository.ApplyRepository;
import com.harryberlin.cvnest.repository.CVRepository;
import com.harryberlin.cvnest.repository.JobRepository;
import com.harryberlin.cvnest.repository.UserRepository;
import com.harryberlin.cvnest.util.constant.CVStatusEnum;
import com.harryberlin.cvnest.util.constant.Error;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplyService {
    private final ApplyRepository applyRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final CVRepository cvRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void applyCVForJob(String userId, String jobId, String cvId) {
        Optional<User> userOptional = this.userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.error("User with ID {} not found", cvId);
            throw new BaseException(Error.USER_NOT_FOUND);
        }

        Optional<Job> jobOptional  = this.jobRepository.findById(jobId);
        if (jobOptional.isEmpty()) {
            log.error("Job with ID {} not found", jobId);
            throw new BaseException(Error.JOB_NOT_FOUND);
        }

        Optional<CV> cvOptional  = this.cvRepository.findById(cvId);
        if (cvOptional.isEmpty()) {
            log.error("CV with ID {} not found", cvId);
            throw new BaseException(Error.CV_NOT_FOUND);
        }
        if (!cvOptional.get().getUser().getId().equals(userId)) {
            log.error("CV with ID {} does not belong to user with ID {}", cvId, userId);
            throw new BaseException(Error.CV_NOT_BELONG_TO_USER);
        }

        // Save apply
        var apply = Apply.builder()
                .jobId(jobId)
                .cvId(cvId)
                .appliedAt(LocalDateTime.now())
                .status(CVStatusEnum.PENDING)
                .build();
        this.applyRepository.save(apply);

        this.eventPublisher.publishEvent(new ApplySummittedEvent(userId, jobId, cvId, apply.getId()));
    }
    
    public List<Apply> getApplicationsByJob(String jobId) {
        return this.applyRepository.findByJobId(jobId);
    }
    
    public List<Apply> getApplicationsByUser(String userId) {
        List<CV> userCVs = this.cvRepository.findByUserId(userId);
        List<String> cvIds = userCVs.stream().map(CV::getId).toList();
        return this.applyRepository.findByCvIdIn(cvIds);
    }
      @Transactional
    public void updateApplicationStatus(String applyId, CVStatusEnum status) {
        Optional<Apply> applyOptional = this.applyRepository.findById(applyId);
        if (applyOptional.isEmpty()) {
            log.error("Apply with ID {} not found", applyId);
            throw new BaseException(Error.APPLY_NOT_FOUND);
        }
        
        Apply apply = applyOptional.get();
        
        // Lấy thông tin CV và User
        Optional<CV> cvOptional = this.cvRepository.findById(apply.getCvId());
        if (cvOptional.isEmpty()) {
            log.error("CV with ID {} not found", apply.getCvId());
            throw new BaseException(Error.CV_NOT_FOUND);
        }
        CV cv = cvOptional.get();
        String userId = cv.getUser().getId();
        
        apply.setStatus(status);
        this.applyRepository.save(apply);
        
        // Gửi thông báo dựa trên trạng thái
        if (status == CVStatusEnum.APPROVED) {
            this.eventPublisher.publishEvent(new ApplyApprovedEvent(applyId, userId, apply.getJobId(), apply.getCvId()));
        } else if (status == CVStatusEnum.REJECTED) {
            this.eventPublisher.publishEvent(new ApplyRejectedEvent(applyId, userId, apply.getJobId(), apply.getCvId()));
        }
    }

    public List<Apply> getApplicationsByHR(String hrId) {
        User hr = this.userRepository.findById(hrId).orElseThrow(
                () -> new BaseException(Error.USER_NOT_FOUND)
        );
        String companyId = hr.getCompany().getId();
        return this.applyRepository.findByCompanyId(companyId);
    }
}
