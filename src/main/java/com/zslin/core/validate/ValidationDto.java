package com.zslin.core.validate;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 验证DTO对象
 */
@Data
public class ValidationDto {

    private boolean hasError = false;

    private List<ObjectError> errors;

    private ValidationDto() {
        errors = new ArrayList<>();
        hasError = false;
    }

    public static ValidationDto getInstance() {
        return new ValidationDto();
    }

    public void add(String property, String msg) {
        this.errors.add(new ObjectError(property, msg));
        this.hasError = true;
    }
}
