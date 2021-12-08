package com.exxeta.expenseservice.files;

import com.exxeta.expenseservice.entities.Article;
import com.exxeta.expenseservice.entities.Category;
import com.exxeta.expenseservice.entities.Expense;
import com.exxeta.expenseservice.repositories.ArticleRepository;
import com.exxeta.expenseservice.repositories.CategoryRepository;
import com.exxeta.expenseservice.repositories.ExpenseRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

public class ExpenseImporterTest {

    private final ArticleRepository articleRepository = mock(ArticleRepository.class);
    private final ExpenseRepository expenseRepository = mock(ExpenseRepository.class);
    private final CategoryRepository categoryRepository = mock(CategoryRepository.class);
    private final ExpenseImporter expenseImporter = new ExpenseImporter(articleRepository, expenseRepository, categoryRepository);

    private final String path = "src\\test\\resources\\testFiles\\4\\";

    @Test
    public void testImportCategories() {
        Category importedCategory = new Category(1, "Haushalt", BigDecimal.valueOf(56.0), BigDecimal.valueOf(57.98));

        expenseImporter.importCategories(path + "\\categories_and_articles\\categories_2021-08-17.csv");

        verify(categoryRepository, times(1)).saveAll(List.of(importedCategory));
        verifyNoMoreInteractions(categoryRepository);
        verifyNoInteractions(expenseRepository, articleRepository);
    }

    @Test
    public void testImportArticles() {
        Category testCategory = new Category(1, "Haushalt");
        Article importedArticle = new Article(1, testCategory, "Tisch", 12.98, 4.7);

        when(categoryRepository.findCategoryByName("Haushalt")).thenReturn(testCategory);

        expenseImporter.importArticles(path + "\\categories_and_articles\\articles_2021-08-17.csv");

        verify(categoryRepository, times(1)).findCategoryByName("Haushalt");
        verify(articleRepository, times(1)).saveAll(List.of(importedArticle));
        verifyNoMoreInteractions(articleRepository, categoryRepository);
        verifyNoInteractions(expenseRepository);

    }

    @Test
    public void testImportExpenses() {
        Category category = new Category(1, "testCategory1");
        Article article = new Article(1, category, "testArticle1");
        Expense expense = new Expense(1, LocalDate.of(2021, 4, 15), article,
                BigDecimal.valueOf(122.00), BigDecimal.valueOf(248.46));

        when(articleRepository.findArticleByName("testArticle1")).thenReturn(article);

        expenseImporter.importExpenses(path + "expensesTest.csv");

        verify(expenseRepository, times(1)).saveAll(List.of(expense));
        verify(articleRepository, times(1)).findArticleByName("testArticle1");
        verifyNoMoreInteractions(expenseRepository, articleRepository);
        verifyNoInteractions(categoryRepository);
    }
}
