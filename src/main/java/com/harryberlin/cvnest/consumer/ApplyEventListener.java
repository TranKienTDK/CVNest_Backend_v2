package com.harryberlin.cvnest.consumer;

import com.harryberlin.cvnest.domain.CV;
import com.harryberlin.cvnest.domain.Job;
import com.harryberlin.cvnest.domain.Notification;
import com.harryberlin.cvnest.domain.User;
import com.harryberlin.cvnest.event.notification.ApplySummittedEvent;
import com.harryberlin.cvnest.exception.BaseException;
import com.harryberlin.cvnest.repository.CVRepository;
import com.harryberlin.cvnest.repository.JobRepository;
import com.harryberlin.cvnest.repository.NotificationRepository;
import com.harryberlin.cvnest.repository.UserRepository;
import com.harryberlin.cvnest.util.constant.Error;
import com.harryberlin.cvnest.util.constant.NotificationStatusEnum;
import com.harryberlin.cvnest.util.constant.RoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplyEventListener {
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final CVRepository cvRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleApplyEvent(ApplySummittedEvent event) {
        User user = this.userRepository.findById(event.getUserId()).orElseThrow(
                () -> new BaseException(Error.USER_NOT_FOUND)
        );
        Job job = this.jobRepository.findById(event.getJobId()).orElseThrow(
                () -> new BaseException(Error.JOB_NOT_FOUND)
        );
        CV cv = this.cvRepository.findById(event.getCvId()).orElseThrow(
                () -> new BaseException(Error.CV_NOT_FOUND)
        );

        List<User> hrUsers = this.userRepository.findByCompanyAndRole(job.getCompany(), RoleEnum.HR);
        for (User hr: hrUsers) {
            String content = String.format(
                    "New application submitted from '%s' for job '%s'. CV: '%s'",
                    user.getEmail(), job.getTitle(), cv.getCvName()
            );

            Notification notification = Notification.builder()
                    .receiveId(hr.getId())
                    .applyId(event.getApplyId())
                    .content(content)
                    .createdAt(LocalDateTime.now())
                    .status(NotificationStatusEnum.UNREAD)
                    .build();
            notificationRepository.save(notification);

            messagingTemplate.convertAndSendToUser(
                    hr.getId(),
                    "/topic/notifications",
                    notification
            );
        }
    }

}
