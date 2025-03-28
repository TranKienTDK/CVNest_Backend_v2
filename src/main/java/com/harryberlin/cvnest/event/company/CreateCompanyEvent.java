package com.harryberlin.cvnest.event.company;

import lombok.Getter;

@Getter
public class CreateCompanyEvent {
    private final String id;

    public CreateCompanyEvent(String id) {
        this.id = id;
    }
}
