package com.harryberlin.cvnest.event.company;

import lombok.Getter;

@Getter
public class DeleteCompanyEvent {
    private final String id;

    public DeleteCompanyEvent(String id) {
        this.id = id;
    }
}
