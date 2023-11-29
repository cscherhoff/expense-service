package com.exxeta.expenseservice.files;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class PropertyHandlerTest {
    private final String propertyFilePath = "src\\test\\resources\\testFiles\\expense.properties";

    @Test
    @Disabled
    public void testPropertyFileExists() throws IOException {
        PropertyHandler propertyHandler = new PropertyHandler(propertyFilePath);
    }
}
