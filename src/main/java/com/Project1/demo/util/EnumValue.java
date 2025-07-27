package com.Project1.demo.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({
	ElementType.ANNOTATION_TYPE,
	ElementType.CONSTRUCTOR,
	ElementType.FIELD,
	ElementType.METHOD,
	ElementType.TYPE_USE,
	ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = EnumValueValidator.class)
public @interface EnumValue {
    String name();
    String message() default "{name} must be any of enum {enumClass}";
    Class<? extends Enum<?>> enumClass();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}