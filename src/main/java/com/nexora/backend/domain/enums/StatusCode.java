package com.nexora.backend.domain.enums;

public enum StatusCode {

    SUCCESS("0"),

    FAILURE("1");

    private final String value;

    public String valueOf() {
        return this.value;
    }

    StatusCode(String value) {this.value = value;}
}
