package com.zslin.business.mini.dto;

import lombok.Data;

@Data
public class SingleDataDto {

    private String value;
    public SingleDataDto(String value) {this.value = value;}
}
