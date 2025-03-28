package com.harryberlin.cvnest.event.job;

import lombok.Getter;

@Getter
public class UpdateJobEvent {
    private final String id;

    public UpdateJobEvent(String id) {
        this.id = id;
    }
}
