package com.sytoss.utils.annotation.builder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseEntity {

    @BuilderProperty
    private String id;
}
