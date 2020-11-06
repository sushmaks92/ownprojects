package com.cerner.hi.pitc.model;

import com.cerner.hi.pitc.util.Constants.DataTypeForParams;

public class Parameter {

    private String name;
    private DataTypeForParams dataType;

    public Parameter (String name, DataTypeForParams dataType){
        this.name = name;
        this.dataType = dataType;
    }

    public String getName() {
        return name;
    }

    public DataTypeForParams getDataType() {
        return dataType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Parameter other = (Parameter) obj;
        if (dataType != other.dataType)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
