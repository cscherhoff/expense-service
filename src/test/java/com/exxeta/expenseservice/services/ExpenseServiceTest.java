package com.exxeta.expenseservice.services;

import com.exxeta.expenseservice.dtos.ExpenseFromFrontend;
import com.exxeta.expenseservice.dtos.ExpenseToFrontend;
import com.exxeta.expenseservice.entities.Article;
import com.exxeta.expenseservice.entities.Category;
import com.exxeta.expenseservice.entities.Expense;
import com.exxeta.expenseservice.files.ExpenseExporter;
import com.exxeta.expenseservice.repositories.ArticleRepository;
import com.exxeta.expenseservice.repositories.CategoryRepository;
import com.exxeta.expenseservice.repositories.ExpenseRepository;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.*;

public class ExpenseServiceTest {

    private final ExpenseRepository expenseRepository = mock(ExpenseRepository.class);

    private final CategoryRepository categoryRepository = mock(CategoryRepository.class);

    private final ArticleRepository articleRepository = mock(ArticleRepository.class);

    private final ExpenseExporter expenseExporter = mock(ExpenseExporter.class);

    private final ExpenseService service = new ExpenseService(expenseExporter, expenseRepository, categoryRepository,
            articleRepository);

    private final long userId = 6;

    @Test
    public void testSaveNewExpenseWithoutOverrideDefaults() {
        String testCategory = "testCategory";
        String testArticle = "testArticle";

        ExpenseFromFrontend expenseFromFrontend = new ExpenseFromFrontend(userId, LocalDate.of(2021, 7, 14),
            testArticle, testCategory, 3.5, 12.36, false);
        Category testCategoryObject = new Category(userId, testCategory);
        Article mockArticleFromDb = new Article(userId, testCategoryObject, testArticle);

        Expense expense = new Expense(expenseFromFrontend.userId, expenseFromFrontend.date, mockArticleFromDb,
            BigDecimal.valueOf(expenseFromFrontend.amount),
            BigDecimal.valueOf(expenseFromFrontend.price));

        when(categoryRepository.findCategoryByUserIdAndName(userId, testCategory)).thenReturn(Optional.of(testCategoryObject));
        when(articleRepository.findArticleByUserIdAndName(userId, testArticle)).thenReturn(Optional.of(mockArticleFromDb));

        try {
            service.saveAllNewExpenses(List.of(expenseFromFrontend));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        verify(categoryRepository,times(1)).findCategoryByUserIdAndName(userId, testCategory);
        verify(articleRepository,times(1)).findArticleByUserIdAndName(userId, testArticle);
        verify(expenseRepository,times(1)).saveAndFlush(expense);
        verify(categoryRepository, times(1)).save(testCategoryObject);
        verify(categoryRepository,times(1)).findAllByUserId(userId);
        verify(articleRepository,times(1)).findAllByUserId(userId);
        verifyNoMoreInteractions(categoryRepository, articleRepository, expenseRepository);
    }

    @Test
    public void testSaveNewExpenseWithoutOverrideDefaultsAndGetNullForArticleFromDB() {
        String testCategory = "testCategory";
        String testArticle = "testArticle";

        ExpenseFromFrontend expenseFromFrontend = new ExpenseFromFrontend(userId, LocalDate.of(2021, 7, 14),
            testArticle, testCategory, 3.5, 12.36, false);
        Category testCategoryObject = new Category(userId, testCategory);
        Article mockArticleFromDb = new Article(userId, testCategoryObject, testArticle);

        Expense expense = new Expense(expenseFromFrontend.userId, expenseFromFrontend.date, mockArticleFromDb,
            BigDecimal.valueOf(expenseFromFrontend.amount),
            BigDecimal.valueOf(expenseFromFrontend.price));

        when(categoryRepository.findCategoryByUserIdAndName(userId, testCategory)).thenReturn(java.util.Optional.of(testCategoryObject));
        when(articleRepository.findArticleByUserIdAndName(userId, testArticle)).thenReturn(Optional.empty());
        when(articleRepository.saveAndFlush(mockArticleFromDb)).thenReturn(null);
        when(expenseRepository.saveAndFlush(expense)).thenReturn(null);
        when(categoryRepository.save(testCategoryObject)).thenReturn(null);
        when(categoryRepository.findAll()).thenReturn(null);
        when(articleRepository.findAll()).thenReturn(null);

        try {
            service.saveAllNewExpenses(List.of(expenseFromFrontend));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        verify(categoryRepository,times(1)).findCategoryByUserIdAndName(userId, testCategory);
        verify(articleRepository,times(1)).findArticleByUserIdAndName(userId, testArticle);
        verify(categoryRepository,times(1)).findAllByUserId(userId);
        verify(articleRepository,times(1)).findAllByUserId(userId);

        verify(articleRepository, times(1)).saveAndFlush(mockArticleFromDb);
        verify(expenseRepository,times(1)).saveAndFlush(expense);
        verify(categoryRepository, times(1)).save(testCategoryObject);
        verifyNoMoreInteractions(categoryRepository, articleRepository, expenseRepository);
    }

    @Test
    public void testSaveNewExpenseWithOverrideDefaults() {
        String testCategory = "testCategory";
        String testArticle = "testArticle";

        ExpenseFromFrontend expenseFromFrontend = new ExpenseFromFrontend(userId, LocalDate.of(2021, 7, 14),
            testArticle, testCategory, 3.5, 12.36, true);
        Category testCategoryObject = new Category(userId, testCategory);
        Article mockArticleFromDb = new Article(userId, testCategoryObject, testArticle,2.5,2.34);
        Article mockArticleWithNewDefaults = new Article(userId, testCategoryObject, testArticle, expenseFromFrontend.amount,
            expenseFromFrontend.price);

        Expense expense = new Expense(expenseFromFrontend.userId, expenseFromFrontend.date, mockArticleFromDb,
            BigDecimal.valueOf(expenseFromFrontend.amount),
            BigDecimal.valueOf(expenseFromFrontend.price));

        when(categoryRepository.findCategoryByUserIdAndName(userId, testCategory)).thenReturn(Optional.of(testCategoryObject));
        when(articleRepository.findArticleByUserIdAndName(userId, testArticle)).thenReturn(Optional.of(mockArticleFromDb));
        when(expenseRepository.saveAndFlush(expense)).thenReturn(null);
        when(articleRepository.saveAndFlush(mockArticleWithNewDefaults)).thenReturn(null);
        when(categoryRepository.save(testCategoryObject)).thenReturn(null);
        when(categoryRepository.findAll()).thenReturn(null);
        when(articleRepository.findAll()).thenReturn(null);

        try {
            service.saveAllNewExpenses(List.of(expenseFromFrontend));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        verify(categoryRepository,times(1)).findCategoryByUserIdAndName(userId, testCategory);
        verify(articleRepository,times(1)).findArticleByUserIdAndName(userId, testArticle);
        verify(articleRepository, times(1)).saveAndFlush(mockArticleWithNewDefaults);
        verify(expenseRepository,times(1)).saveAndFlush(expense);
        verify(categoryRepository, times(1)).save(testCategoryObject);
        verify(categoryRepository,times(1)).findAllByUserId(userId);
        verify(articleRepository,times(1)).findAllByUserId(userId);
        verifyNoMoreInteractions(categoryRepository, articleRepository, expenseRepository);
    }

    @Test
    public void getAllExpenses() {
        Category testCategory = new Category(userId, "testCategory");
        Article testArticle = new Article(userId, testCategory, "testArticle");
        Expense expense = new Expense(userId, LocalDate.of(2021, 7, 16),
            testArticle, BigDecimal.valueOf(12.5), BigDecimal.valueOf(23.79));

        when(expenseRepository.findAllByUserId(userId)).thenReturn(List.of(expense));

        List<ExpenseToFrontend> expenseFromFrontendList = service.getAllExpenses(userId);

        List<ExpenseToFrontend> expected = List.of(new ExpenseToFrontend(LocalDate.of(2021, 7, 16),
            testArticle.getName(),testCategory.getName(), 12.5, 23.79));

        assertIterableEquals(expected, expenseFromFrontendList, "The lists are not the same");

        verify(expenseRepository, times(1)).findAllByUserId(userId);
        verifyNoMoreInteractions(expenseRepository);
    }

    @Test
    public void getAllExpensesForGivenMonth() {
        int month = 6;
        String category = "testCategory";
        String article = "testArticle";
        LocalDate date = LocalDate.of(2021, 6, 14);

        Category testCategory = new Category(userId, category);
        Article testArticle = new Article(userId, testCategory, article);

        Expense mockExpense = new Expense(userId, date, testArticle, BigDecimal.valueOf(14.5), BigDecimal.valueOf(23.56));

        List<Expense> mockExpenseList = List.of(mockExpense);

        final int year = LocalDate.now().getYear();
        when(expenseRepository.findExpensesByDateIsGreaterThanEqualAndUserId(
            LocalDate.of(year, month, 1), userId)).thenReturn(mockExpenseList);

        List<ExpenseToFrontend> expected = List.of(new ExpenseToFrontend(date, article, category, 14.5, 23.56));
        List<ExpenseToFrontend> actual = service.getExpensesFromGivenMonth(userId, String.valueOf(month));

        assertIterableEquals(expected, actual, "The lists are not the same");

        verify(expenseRepository, times(1)).findExpensesByDateIsGreaterThanEqualAndUserId(
            LocalDate.of(LocalDate.now().getYear(), month, 1), userId);
        verifyNoMoreInteractions(expenseRepository);
    }
}
