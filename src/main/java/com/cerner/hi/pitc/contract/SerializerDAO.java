package com.cerner.hi.pitc.contract;

import java.util.List;

public interface SerializerDAO<T> {

    public void store(T t, String name) throws Exception;

    public T get(String id) throws Exception;

    public List<T> getAll() throws Exception;
}
