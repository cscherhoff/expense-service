package com.exxeta.expenseservice.files;

import com.exxeta.expenseservice.dtos.ExpenseFromFrontend;
import com.exxeta.expenseservice.entities.Article;
import com.exxeta.expenseservice.entities.Category;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseExporterTest {
    private static final String separator = System.getProperty("file.separator");

    private final String path = "src" + separator + "test" + separator + "resources" + separator + "testFiles" + separator;

    private final ExpenseExporter expenseExporter = new ExpenseExporter(path);
    private static final String userId = "5";
    private static List<ExpenseFromFrontend> testExpenseList = new ArrayList<>();
    private static List<Category> testCategoryList = new ArrayList<>();
    private static List<Article> testArticleList = new ArrayList<>();

    @BeforeAll
    static void createTestData() {
        createTestExpenses();
        createTestCategories();
        createTestArticles();
    }

    @Test
//    @Disabled
    public void testAllInThisClass() {
        testExportExpenses();
        testExportAllExpenses();
        testExportArticles();
        testExportCategories();
    }

    private void testExportExpenses() {
        try {
            expenseExporter.exportExpenses(userId, testExpenseList, false);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void testExportAllExpenses() {
        try {
            expenseExporter.exportExpenses(userId, testExpenseList, true);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void testExportArticles() {
        try {
            expenseExporter.exportArticles(userId, testArticleList);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void testExportCategories() {
        try {
            expenseExporter.exportCategories(userId, testCategoryList);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private static void createTestExpenses() {
        ExpenseFromFrontend expense1 = new ExpenseFromFrontend(userId, LocalDate.of(2021, 4, 15), "testArticle1",
                "testCategory1", 122, 248.46, true);
        ExpenseFromFrontend expense2 = new ExpenseFromFrontend(userId, LocalDate.of(2021, 4, 15), "testArticle2",
                "testCategory2", 415, 379.23, true);
        testExpenseList = List.of(expense1, expense2);
    }

    private static void createTestCategories() {
        Category testCategory1 = new Category(userId, "Haushalt", BigDecimal.valueOf(56), BigDecimal.valueOf(57.98));
        Category testCategory2 = new Category(userId, "Freizeit", BigDecimal.valueOf(1.34), BigDecimal.valueOf(574.96));
        Category testCategory3 = new Category(userId, "Kleidung", BigDecimal.valueOf(124.5), BigDecimal.valueOf(23.11));
        Category testCategory4 = new Category(userId, "Reisen", BigDecimal.valueOf(120), BigDecimal.valueOf(82.08));
        testCategoryList = List.of(testCategory1, testCategory2, testCategory3, testCategory4);
    }

    private static void createTestArticles() {
        Article testArticle1 = new Article(userId, testCategoryList.get(0), "Tisch", 12.98, 4.7);
        Article testArticle2 = new Article(userId, testCategoryList.get(1), "Tennis", 45.8, 89.87);
        Article testArticle3 = new Article(userId, testCategoryList.get(2), "Schuhe", 2.98, 287);
        Article testArticle4 = new Article(userId, testCategoryList.get(3), "Italien", 87.98, 2.87);
        testArticleList = List.of(testArticle1, testArticle2, testArticle3, testArticle4);
    }
}
