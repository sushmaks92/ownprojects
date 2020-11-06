package com.cerner.hi.pitc.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.cerner.hi.pitc.model.Argument;
import com.cerner.hi.pitc.model.Validator;

public class ValidatorParser {

    public List<Validator> getValidators(String configPath) throws SAXException, IOException, ParserConfigurationException, URISyntaxException {
        List<Validator> validators =  null;
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        ValidatorHandler userhandler = new ValidatorHandler();
        try(InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(configPath)){
            saxParser.parse(inputStream, userhandler);
        };
        validators = userhandler.context.validators;
        return validators;
    }

}

class ValidatorHandler extends DefaultHandler {

    ParsingContext context = new ParsingContext();
    StringBuffer tagContents = new StringBuffer();

    @Override
    public void startElement(String uri, String localName, String name,
                             Attributes attributes) {
        if(name.equals("validator")) {
            tagContents = new StringBuffer();
            context.validator = new Validator();
        }else if(name.equals("param")) {
            Argument param = new Argument(attributes.getValue("key"),attributes.getValue("value"));
            context.validator.getParams().add(param);
        }
    }

    @Override
    public void endElement(String uri, String localName, String name) {
        if(name.equals("name")) {
            String validatorName = tagContents.toString().trim();
            context.validator.setName(validatorName);
            context.validators.add(context.validator);
        }
    }

    @Override
    public void characters(char ch[], int start, int length) {
        tagContents.append(ch,start,length);
    }

}

class ParsingContext{
    List<Validator> validators = new ArrayList<>();
    Validator validator = null;
}
