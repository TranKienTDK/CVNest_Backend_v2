package com.harryberlin.cvnest.event.job;

import lombok.Getter;

@Getter
public class CreateJobEvent {
    private final String id;

    public CreateJobEvent(String id) {
        this.id = id;
    }

}
