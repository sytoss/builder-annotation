package com.sytoss.utils.annotation.builder;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface BuilderProperty {
}
