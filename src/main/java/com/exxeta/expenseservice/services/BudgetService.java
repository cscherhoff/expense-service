package com.exxeta.expenseservice.services;

import com.exxeta.expenseservice.entities.Category;
import com.exxeta.expenseservice.files.PropertyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.List;

@org.springframework.stereotype.Service
public class BudgetService {

    private final Logger logger = LoggerFactory.getLogger(BudgetService.class);
    private final PropertyHandler propertyHandler;
    private final CategoryService categoryService;
    private final DateService dateService;

    public BudgetService(PropertyHandler propertyHandler, CategoryService categoryService,
                         DateService dateService) {
        this.propertyHandler = propertyHandler;
        this.categoryService = categoryService;
        this.dateService = dateService;
    }

    @PostConstruct
    public void starterMethod() {
        int realMonth = dateService.getRealCurrentMonth();
        String currentMonth = propertyHandler.getCurrentMonth();
        logger.info("The current month from the property file is: " + currentMonth);
        if (isNewMonth(Integer.parseInt(currentMonth), realMonth)) {
            updateAllCategories();
            updateMonthProperties();
        }
    }

    protected boolean isNewMonth( int currentMonthFromProperties, int realMonth) {
        int diff = getMonthDifference(realMonth, currentMonthFromProperties);
        if (diff==1) {
            logger.info("This is the first call in the new month.");
            return true;
        } else if (diff==-11) {
            logger.info("This ist the first call in the new year.");
            return true;
        } else if (diff==0) {
            return false;
        } else {
            logger.error("There is something wrong with the month. Please check!");
            return false;
        }
    }

    private int getMonthDifference(int realMonth, int currentMonthFromProperties) {
        return realMonth - currentMonthFromProperties;
    }

    private void updateAllCategories() {
        List<Category> categoryList = categoryService.getAllCategories();
        updateAllBudgetsInList(categoryList);
        categoryService.saveCategoryList(categoryList);
    }

    private void updateAllBudgetsInList(List<Category> categoryList) {
        for (Category category: categoryList) {
            category.setCurrentBudget(category.getCurrentBudget().add(category.getBudget()));
        }
    }

    /**
     * Updates the month properties by adding one to the current month (except december, when the value is set to "1")
     * every new month.
     * @throws IllegalStateException throws an exception, if the value of old month is not an integer between 1 and 12
     */
    private void updateMonthProperties() throws IllegalStateException {
        int oldMonth = Integer.parseInt(propertyHandler.getCurrentMonth());
        String newMonth;
        if (oldMonth >= 1 && oldMonth < 12) {
            newMonth = String.valueOf(oldMonth + 1);
        } else if (oldMonth == 12) {
            newMonth = String.valueOf(1);
        } else {
            throw new IllegalStateException("The old month was + " + oldMonth);
        }
        propertyHandler.changeSaveProperties(newMonth);
    }
}
