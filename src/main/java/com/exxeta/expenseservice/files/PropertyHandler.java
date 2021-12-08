package com.exxeta.expenseservice.files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.util.Properties;

@Service
public class PropertyHandler {

    private static final String separator = System.getProperty("file.separator");
    private final Logger logger = LoggerFactory.getLogger(PropertyHandler.class);
    private final String expensePropertyFile;
    private final String currentMonth;

    public PropertyHandler() {
        this(System.getProperty("user.dir") + separator + "properties" + separator + "expense.properties");
    }

    public PropertyHandler(String expensePropertyFile) {
        this.expensePropertyFile = expensePropertyFile;
        try {
            if (!propertyFileExists()) {
                createPropertyFile();
                writeCurrentMonthToFile();
            }
            logger.info("The property file exits.");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        currentMonth = getCurrentMonthProperty();
    }

    public String getCurrentMonth() {
        return currentMonth;
    }

    private boolean propertyFileExists() {
        logger.info("Check if the property file '" + expensePropertyFile + "' exists.");
        File file = new File(expensePropertyFile);
        return file.exists();
    }

    private void createPropertyFile() {
        try {
            logger.info("The property file doesn't exist yet and is going to be created now.");
            File propertyFile = new File(expensePropertyFile);
            boolean successfullyCreated = propertyFile.createNewFile();
            if (!successfullyCreated) {
                throw new IOException("Property-File could not be created.");
            } else {
                logger.info("The property file was successfully created.");
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void writeCurrentMonthToFile() {
        try {
            int actualCurrentMonth = LocalDate.now().getMonthValue();
            FileWriter writer = new FileWriter(expensePropertyFile);
            writer.write("currentMonth=" + actualCurrentMonth);
            writer.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private String getCurrentMonthProperty() {
        try {
            return loadProperty().getProperty("currentMonth");
        } catch (Exception exception) {
            exception.printStackTrace();
            return String.valueOf(LocalDate.now().getMonthValue());
        }
    }

    public void changeSaveProperties(String value) {
        try {
            Properties p = loadProperty();
            p.replace("currentMonth", value);
            File file = new File(expensePropertyFile);
            FileOutputStream fr;
            fr = new FileOutputStream(file);
            p.store(fr, null);
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Properties loadProperty() throws IOException {
        Properties property = new Properties();
        BufferedInputStream stream;
        stream = new BufferedInputStream(new FileInputStream(expensePropertyFile));
        property.load(stream);
        stream.close();
        return property;
    }
}
