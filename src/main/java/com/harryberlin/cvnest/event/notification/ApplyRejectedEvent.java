package com.harryberlin.cvnest.event.notification;

import lombok.Getter;

@Getter
public class ApplyRejectedEvent {
    private final String applyId;
    private final String userId;
    private final String jobId;
    private final String cvId;
    
    public ApplyRejectedEvent(String applyId, String userId, String jobId, String cvId) {
        this.applyId = applyId;
        this.userId = userId;
        this.jobId = jobId;
        this.cvId = cvId;
    }
}
