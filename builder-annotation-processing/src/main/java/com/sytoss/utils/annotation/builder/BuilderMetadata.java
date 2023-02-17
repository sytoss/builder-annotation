package com.sytoss.utils.annotation.builder;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class BuilderMetadata {

    private String className;
    private String superClassName;

    private Map<String, String> methods = new HashMap<>();

    public BuilderMetadata(String className, String superClassName) {
        this.className = className;
        this.superClassName = superClassName;
    }
}
