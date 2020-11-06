package com.cerner.hi.pitc.parser;

public class ValidatorParserFactory {

    static ValidatorParser parser;

    public static ValidatorParser getParser() {
        if(null==parser)
            parser = new ValidatorParser();
        return parser;
    }
}

