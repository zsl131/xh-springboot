package com.zslin.code.dto;

import lombok.Data;

@Data
public class ContextDto {

    private String key;

    private String value;

    public ContextDto(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
