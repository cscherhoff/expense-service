package com.exxeta.expenseservice.files;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class PropertyHandlerTest {
    private final String propertyFilePath = "src\\test\\resources\\testFiles\\expense.properties";

    @Test
    @Disabled
    public void testPropertyFileExists() {
        PropertyHandler propertyHandler = new PropertyHandler(propertyFilePath);
    }
}
