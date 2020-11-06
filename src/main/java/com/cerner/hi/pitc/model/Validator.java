package com.cerner.hi.pitc.model;

import java.util.ArrayList;
import java.util.List;


public class Validator {

    private String name;
    private List<Argument> params = new ArrayList<>();

    public List<Argument> getParams() {
        return params;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

}
