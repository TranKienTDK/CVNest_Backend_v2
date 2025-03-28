package com.harryberlin.cvnest.event.company;

import lombok.Getter;

@Getter
public class UpdateCompanyEvent {
    private final String id;

    public UpdateCompanyEvent(String id) {
        this.id = id;
    }
}
