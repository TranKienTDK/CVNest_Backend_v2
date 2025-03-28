package com.harryberlin.cvnest.event.job;

import lombok.Getter;

@Getter
public class DeleteJobEvent {
    private final String id;

    public DeleteJobEvent(String id) {
        this.id = id;
    }
}
