package com.zslin.business.xzqh.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DivisionResultDto {

    private String label;

    private String value;

    private List<DivisionDto> children;

    public void add(DivisionDto dto) {
        this.children.add(dto);
    }

    public DivisionResultDto() {
        if(children==null) {children = new ArrayList<>();}
    }

    public DivisionResultDto(String label, String value) {
        this.label = label;
        this.value = value;
        if(children==null) {children = new ArrayList<>();}
    }

    public DivisionResultDto(String label, String value, List<DivisionDto> children) {
        this.label = label;
        this.value = value;
        this.children = children;
    }
}
