package com.exxeta.expenseservice.files;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class PropertyHandlerTest {
    private static final String separator = System.getProperty("file.separator");
    private final String propertyFilePath = "src" + separator + "test" + separator + "resources" + separator + "testFiles" + separator + "expense.properties";

    @Test
    @Disabled
    public void testPropertyFileExists() throws IOException {
        PropertyHandler propertyHandler = new PropertyHandler(propertyFilePath);
    }
}
