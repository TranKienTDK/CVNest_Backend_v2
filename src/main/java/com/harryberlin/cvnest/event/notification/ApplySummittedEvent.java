package com.harryberlin.cvnest.event.notification;

import lombok.Getter;

@Getter
public class ApplySummittedEvent {
    private final String userId;
    private final String jobId;
    private final String cvId;
    private final String applyId;
    public ApplySummittedEvent(String userId, String jobId, String cvId, String applyId) {
        this.userId = userId;
        this.jobId = jobId;
        this.cvId = cvId;
        this.applyId = applyId;
    }
}
