package com.example.ryanair.utils;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class TestUtils {

    public static Object parseJSONFile(String fileName) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        ClassLoader classLoader = TestUtils.class.getClassLoader();
        String path = Objects.requireNonNull(classLoader.getResource(fileName)).getPath();
        return parser.parse(new FileReader(path));
    }

}
