package com.zslin.core.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class ERModelDto {

    private ERDto result;

    private List<ERFieldDto> fieldList;
}
