package com.exxeta.expenseservice.services;

import com.exxeta.expenseservice.entities.Category;
import com.exxeta.expenseservice.files.PropertyHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

public class TestBudgetService {

    private final CategoryService categoryService = mock(CategoryService.class);
    private final PropertyHandler propertyHandler = mock(PropertyHandler.class);
    private final DateService dateService = mock(DateService.class);

    private final BudgetService budgetService = new BudgetService(propertyHandler, categoryService, dateService);

    @Test
    public void testUpdateCategoriesForNewMonth() {
        String mockCurrentMonth = "6";
        long userId = 5;
        Category testCategory1 = new Category(userId, "TestCategory1");
        Category testCategory2 = new Category(userId, "TestCategory2");

        when(propertyHandler.getCurrentMonth()).thenReturn(mockCurrentMonth);
        when(categoryService.getAllCategories()).thenReturn(List.of(testCategory1, testCategory2));
        when(dateService.getRealCurrentMonth()).thenReturn(7);

        budgetService.starterMethod();

        verify(propertyHandler, times(2)).getCurrentMonth();
        verify(propertyHandler, times(1)).changeSaveProperties("7");
        verify(dateService, times(1)).getRealCurrentMonth();
        verify(categoryService, times(1)).getAllCategories();
        verify(categoryService, times(1)).saveCategoryList(anyList());

        verifyNoMoreInteractions(propertyHandler, categoryService, dateService);
    }

    @ParameterizedTest
    @CsvSource({
        "3, 4, true",
        "10, 11, true",
        "2, 3, true",
        "1, 2, true",
        "11, 12, true",
        "12, 1, true",
        "2, 4, false",
        "3, 11, false",
        "11, 2, false",
        "6, 4, false",
        "12, 2, false",
        "5, 2, false"
    })
    public void testMonthDifference(int monthFromProperties, int realActualMonth, boolean isNewMonth) {
        assertEquals(isNewMonth, budgetService.isNewMonth(monthFromProperties, realActualMonth),
            "The result of isNewMonth is wrong for: " + monthFromProperties + " and " + realActualMonth);
    }
}
