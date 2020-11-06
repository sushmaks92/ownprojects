package com.cerner.hi.pitc.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.cerner.hi.pitc.contract.SerializerDAO;
import com.cerner.hi.pitc.model.config.Configuration;
import com.cerner.hi.pitc.model.project.Project;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;

@Component
public class ConfigurationRepository implements SerializerDAO<Configuration> {

    @Override
    public void store(Configuration configuration,String name) throws FileNotFoundException {
        Kryo kryo = new Kryo();
        String filename = "config/"+name+".dat";
        kryo.setRegistrationRequired(false);
        Output output = new Output(new FileOutputStream(filename,false));
        kryo.writeObject(output,configuration);
        output.close();
    }

    @Override
    public Configuration get(String id) throws FileNotFoundException {
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        String path = "config/"+id+".dat";
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        Input input = new Input(new FileInputStream(path));
        Configuration configuration = kryo.readObject(input, Configuration.class);
        input.close();
        return configuration;
    }

    @Override
    public List<Configuration> getAll() throws Exception {
    	Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
    	File folder = new File("config/");
        List<Configuration> configs = new ArrayList<>();
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        File[] files = folder.listFiles();
        for (File i : files){
            Input input = new Input(new FileInputStream(i.getAbsolutePath()));
            configs.add(kryo.readObject(input, Configuration.class));
            input.close();
        }
        return configs;
    }
}
