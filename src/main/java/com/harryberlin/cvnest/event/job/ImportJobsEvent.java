package com.harryberlin.cvnest.event.job;

import lombok.Getter;

import java.util.List;

@Getter
public class ImportJobsEvent {
    private final List<String> jobIds;

    public ImportJobsEvent(List<String> jobIds) {
        this.jobIds = jobIds;
    }
}
