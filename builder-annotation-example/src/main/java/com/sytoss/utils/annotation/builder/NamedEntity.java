package com.sytoss.utils.annotation.builder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NamedEntity extends BaseEntity {

    @BuilderProperty
    private String name;
}
