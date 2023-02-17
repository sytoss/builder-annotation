package com.sytoss.utils.annotation.builder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Person extends NamedEntity {

    @BuilderProperty
    private int age;

}
