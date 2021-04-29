package com.zslin.business.xzqh.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 行政区划DTO对象
 */
@Data
public class DivisionDto implements Comparable<DivisionDto> {

    private String label;

    private String value;

    private List<DivisionSingleDto> children;

    public DivisionDto() {
        if(children==null) {children = new ArrayList<>();}
    }

    public void add(DivisionSingleDto dto) {
        this.children.add(dto);
    }

    public DivisionDto(String label, String value) {
        this.label = label;
        this.value = value;
        if(children==null) {children = new ArrayList<>();}
    }

    public DivisionDto(String label, String value, List<DivisionSingleDto> children) {
        this.label = label;
        this.value = value;
        this.children = children;
    }

    @Override
    public int compareTo(DivisionDto o) {
//        return parseInt(this.value)>parseInt(o.getValue());
        return parseInt(this.value).compareTo(parseInt(o.getValue()));
    }

    private Integer parseInt(String value) {
        return Integer.parseInt(value);
    }
}
