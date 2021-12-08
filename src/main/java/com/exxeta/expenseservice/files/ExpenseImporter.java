package com.exxeta.expenseservice.files;

import com.exxeta.expenseservice.entities.Article;
import com.exxeta.expenseservice.entities.Category;
import com.exxeta.expenseservice.entities.Expense;
import com.exxeta.expenseservice.repositories.ArticleRepository;
import com.exxeta.expenseservice.repositories.CategoryRepository;
import com.exxeta.expenseservice.repositories.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExpenseImporter {

    private final ArticleRepository articleRepository;
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    public ExpenseImporter(ArticleRepository articleRepository,
                           ExpenseRepository expenseRepository,
                           CategoryRepository categoryRepository) {
        this.articleRepository = articleRepository;
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
    }

    public void importExpenses(String path) {
        try {
            List<String> importedLines = Files.readAllLines(Path.of(path));
            expenseRepository.saveAll(convertToExpense(importedLines));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private List<Expense> convertToExpense(List<String> stringList) {
        List<Expense> expenseList = new ArrayList<>();
        for (String stringExpense: stringList) {
            expenseList.add(convertToExpense(stringExpense));
        }
        return expenseList;
    }

    private Expense convertToExpense(String stringExpense) {
        String[] stringArray = stringExpense.split(";");
        long userId = 1;
        LocalDate date = LocalDate.parse(stringArray[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Article article = articleRepository.findArticleByName(stringArray[2]);
        double amount = Double.parseDouble(stringArray[3]);
        double price = Double.parseDouble(stringArray[4]);

        return new Expense(userId, date, article, BigDecimal.valueOf(amount), BigDecimal.valueOf(price));
    }

    public void importCategories(String path) {
        try {
            List<String> importedLines = Files.readAllLines(Path.of(path));
            categoryRepository.saveAll(convertToCategories(importedLines));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private List<Category> convertToCategories(List<String> stringList) {
        List<Category> categoryList = new ArrayList<>();
        for (String stringCategory: stringList) {
            categoryList.add(convertToCategory(stringCategory));
        }
        return categoryList;
    }

    private Category convertToCategory(String stringExpense) {
        String[] stringArray = stringExpense.split(";");
        long userId = 1;
        String name = stringArray[0];
        double budget = Double.parseDouble(stringArray[1]);
        double currentBudget = Double.parseDouble(stringArray[2]);

        return new Category(userId, name, BigDecimal.valueOf(budget), BigDecimal.valueOf(currentBudget));
    }

    public void importArticles(String path) {
        try {
            List<String> importedLines = Files.readAllLines(Path.of(path));
            articleRepository.saveAll(convertToArticles(importedLines));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private List<Article> convertToArticles(List<String> stringList) {
        List<Article> articleList = new ArrayList<>();
        for (String stringArticle: stringList) {
            articleList.add(convertToArticle(stringArticle));
        }
        return articleList;
    }

    private Article convertToArticle(String stringArticle) {
        String[] stringArray = stringArticle.split(";");
        long userId = 1;
        String articleName = stringArray[0];
        Category category = categoryRepository.findCategoryByName(stringArray[1]);
        double defaultAmount = Double.parseDouble(stringArray[2]);
        double defaultPrice = Double.parseDouble(stringArray[3]);

        return new Article(userId, category, articleName, defaultAmount, defaultPrice);
    }
}
