package com.zslin.core.dto;

import lombok.Data;

/**
 * Created by zsl on 2018/8/7.
 * 数据字典DTO对象
 */
@Data
public class DictionaryDto {

    private String label;

    private Integer value;

    @Override
    public String toString() {
        return "DictionaryDto{" +
                "label='" + label + '\'' +
                ", value=" + value +
                '}';
    }

    public DictionaryDto(){}

    public DictionaryDto(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
