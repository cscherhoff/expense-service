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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(Service.class);
    private final ExpenseExporter expenseExporter;
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final ArticleRepository articleRepository;

    public ExpenseService(ExpenseExporter expenseExporter, ExpenseRepository expenseRepository,
                          CategoryRepository categoryRepository,
                          ArticleRepository articleRepository) {
        this.expenseExporter = expenseExporter;
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
        this.articleRepository = articleRepository;
    }

    public void saveAllNewExpenses(List<ExpenseFromFrontend> expenseFromFrontendList) throws IOException {
        for (ExpenseFromFrontend expenseFromFrontend: expenseFromFrontendList) {
            saveNewExpense(expenseFromFrontend);
        }
        if (!expenseFromFrontendList.isEmpty()) {
            long userId = expenseFromFrontendList.get(0).userId;
            expenseExporter.exportExpenses(userId, expenseFromFrontendList, false);
            expenseExporter.exportCategories(userId, categoryRepository.findAllByUserId(userId));
            expenseExporter.exportArticles(userId, articleRepository.findAllByUserId(userId));
        }
    }

    private void saveNewExpense(ExpenseFromFrontend expenseFromFrontend) {
        Expense expense;
        final String categoryName = expenseFromFrontend.category;
        final long userId = expenseFromFrontend.userId;
        Optional<Category> categoryFromDb = categoryRepository.findCategoryByUserIdAndName(userId, categoryName);
        if (categoryFromDb.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not find the category " + categoryName + " in the database for the user id " + userId);
        }
        Article articleFromDb = getArticleFromDatabase(expenseFromFrontend, categoryFromDb.get());
        expense = saveExpenseToDatabase(expenseFromFrontend, articleFromDb);
        if (expenseFromFrontend.overrideDefaults) {
            overrideArticleDefaults(expense.getArticle(), expense.getAmount().doubleValue(),
                    expense.getPrice().doubleValue());
        }
        updateBudgets(expense.getArticle().getCategory(), expense.getPrice());
    }

    private Article getArticleFromDatabase(ExpenseFromFrontend expenseFromFrontend, Category categoryFromDb) {
        Optional<Article> articleFromDbOptional = articleRepository.findArticleByUserIdAndName(expenseFromFrontend.userId, expenseFromFrontend.article);
        Article article;
        if (articleFromDbOptional.isEmpty()) {
            logger.info("This is a new article - create it");
            article = new Article(expenseFromFrontend.userId, categoryFromDb, expenseFromFrontend.article);
            articleRepository.saveAndFlush(article);
            logger.info("Successfully saved the new article into the database.");
        } else {
            article = articleFromDbOptional.get();
        }
        return article;
    }

    private Expense saveExpenseToDatabase(ExpenseFromFrontend expenseFromFrontend, Article articleFromDb) {
        Expense expense = new Expense(expenseFromFrontend.userId, expenseFromFrontend.date, articleFromDb,
            BigDecimal.valueOf(expenseFromFrontend.amount), BigDecimal.valueOf(expenseFromFrontend.price));
        writeExpenseToLog(expenseFromFrontend);
        expenseRepository.saveAndFlush(expense);
        logger.info("successfully inserted the expense to the database");
        return expense;
    }

    private void writeExpenseToLog(ExpenseFromFrontend expense) {
        try {
            logger.info("Trying to save the new expense into the database: " + mapper.writeValueAsString(expense));
        } catch (JsonProcessingException jsonProcessingException) {
            jsonProcessingException.printStackTrace();
        }
    }

    private void overrideArticleDefaults(Article article, double defaultAmount, double defaultPrice) {
        logger.info("... overwriting the default values for " + article + " ...");
        article.setDefaultAmount(defaultAmount);
        article.setDefaultPrice(defaultPrice);
        articleRepository.saveAndFlush(article);
    }

    private void updateBudgets(Category category, BigDecimal price) {
        logger.info(
                "... updating the budget for category " + category + " from " + category.getCurrentBudget() + " to ");
        decreaseBudget(category, price);
        logger.info(category.getCurrentBudget() + " ...");
        categoryRepository.save(category);
        logger.info("... done updating the budget.");
    }

    private void decreaseBudget(Category category, BigDecimal price) {
        BigDecimal newBudget = category.getCurrentBudget().subtract(price);
        category.setCurrentBudget(newBudget);
    }

    public List<ExpenseToFrontend> getAllExpenses(long userId) {
        return convertExpenseListToFrontend(expenseRepository.findAllByUserId(userId));
    }

    private ExpenseToFrontend convertExpense(Expense expense) {
        return new ExpenseToFrontend(
            expense.getDate(),
            expense.getArticle().getName(),
            expense.getArticle().getCategory().getName(),
            expense.getAmount().doubleValue(),
            expense.getPrice().doubleValue());
    }

    public List<ExpenseToFrontend> getExpensesFromGivenMonth(long userId, String month) {
        return convertExpenseListToFrontend(expenseRepository.findExpensesByDateIsGreaterThanEqualAndUserId(LocalDate.of(LocalDate.now().getYear(),
            Month.of(Integer.parseInt(month)), 1), userId));
    }

    private List<ExpenseToFrontend> convertExpenseListToFrontend(List<Expense> expenseList) {
        List<ExpenseToFrontend> expenseDtoList = new ArrayList<>();
        for (Expense expense: expenseList) {
            ExpenseToFrontend expenseFromFrontend = convertExpense(expense);
            expenseDtoList.add(expenseFromFrontend);
        }
        return expenseDtoList;
    }
}
