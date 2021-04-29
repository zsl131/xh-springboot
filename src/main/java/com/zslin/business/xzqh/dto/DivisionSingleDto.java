package com.zslin.business.xzqh.dto;

import lombok.Data;

@Data
public class DivisionSingleDto {

    private String label;

    private String value;

    public DivisionSingleDto(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public boolean equals(Object obj) {
        DivisionSingleDto dto = (DivisionSingleDto) obj;
        return (this.value.equals(dto.getValue()));
    }
}
