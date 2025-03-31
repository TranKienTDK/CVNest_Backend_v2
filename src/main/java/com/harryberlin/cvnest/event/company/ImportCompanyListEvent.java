package com.harryberlin.cvnest.event.company;

import lombok.Getter;

import java.util.List;

@Getter
public class ImportCompanyListEvent {
    private final List<CreateCompanyEvent> events;

    public ImportCompanyListEvent(List<CreateCompanyEvent> events) {
        this.events = events;
    }
}
