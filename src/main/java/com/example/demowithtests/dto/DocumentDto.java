package com.example.demowithtests.dto;

import java.util.UUID;

public record DocumentDto(
        String number,
        String uuid,
        Boolean isHandled) {

    public DocumentDto(String number,
                       String uuid,
                       Boolean isHandled

    ) {
        this.uuid = UUID.randomUUID().toString();
        this.number = number;
        this.isHandled = Boolean.FALSE;
    }
}

