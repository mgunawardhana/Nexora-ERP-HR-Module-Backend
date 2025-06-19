package com.nexora.backend.domain.enums;

public enum StatusMessage {

    SUCCESS("Success"),

    FAILURE("Failure");

    private final String value;

    public String valueOf() {
        return this.value;
    }

    StatusMessage(String value) {this.value = value;}
}
