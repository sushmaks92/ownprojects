package com.cerner.hi.pitc.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Argument {

    private @NonNull String key;
    private @NonNull String value;
    public Argument(){};
}
