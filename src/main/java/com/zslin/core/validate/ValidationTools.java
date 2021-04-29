package com.zslin.core.validate;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * 表单验证工具类
 */
public class ValidationTools {

    public static ValidationDto buildValidate(Object obj) {
        ValidationDto dto = ValidationDto.getInstance();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(obj);
        for (ConstraintViolation cv : constraintViolations) {
            dto.add(cv.getPropertyPath().toString(), cv.getMessage());
            /*System.out.println("ValidationConstraint1: " + cv.getConstraintDescriptor().getAnnotation());
            System.out.println("ValidationConstraint2: " + cv.getConstraintDescriptor());
            System.out.println("ValidationConstraint3: " + cv.getMessageTemplate());
            System.out.println("ValidationConstraint4: " + cv.getInvalidValue());
            System.out.println("ValidationConstraint5: " + cv.getLeafBean());
            System.out.println("ValidationConstraint6: " + cv.getRootBeanClass());
            System.out.println("ValidationConstraint7: " + cv.getPropertyPath().toString());
            System.out.println("ValidationConstraint8: " + cv.getMessage());*/
        }
        return dto;
    }
}
