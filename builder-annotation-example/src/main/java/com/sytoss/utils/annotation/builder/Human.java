package com.sytoss.utils.annotation.builder;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@BuilderProperty
public class Human extends Person {

    private boolean man;

    private Date createDate;
}
