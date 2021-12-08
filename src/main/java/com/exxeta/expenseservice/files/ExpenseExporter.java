package com.exxeta.expenseservice.files;

import com.exxeta.expenseservice.dtos.ExpenseFromFrontend;
import com.exxeta.expenseservice.entities.Article;
import com.exxeta.expenseservice.entities.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExpenseExporter {

    private final String exportPath;
    private final String pathSeparator = System.getProperty("file.separator");
    private final Logger logger = LoggerFactory.getLogger(ExpenseExporter.class);

    public ExpenseExporter() {
        this(System.getProperty("user.dir") + System.getProperty("file.separator") + "export");
    }

    public ExpenseExporter(String exportPath) {
        Assert.notNull(exportPath, "The export path is null! "
            + "Either the property couldn't be read or it wasn't specified.");
        this.exportPath = exportPath;
        logger.info("The exportPath for the expenses is: " + this.exportPath);
    }

    public void exportExpenses(long userId, List<ExpenseFromFrontend> expenseList, boolean exportAllExpense) throws IOException {
        final String pathToExportFile = getExpenseExportFilePath(userId, exportAllExpense);
        export(convertListOfExpenses(expenseList), pathToExportFile, true);
    }

    private String getExpenseExportFilePath(long userId, boolean exportAllExpense) throws IOException {
        createExportFolders(userId);
        String expenseFileName;
        if (exportAllExpense) {
            String date = getDateToday();
            expenseFileName = "expenses_" + date + ".csv";
        } else {
            expenseFileName = "expenses.csv";
        }
        return exportPath + pathSeparator + userId + pathSeparator + expenseFileName;
    }

    public void exportCategories(long userId, List<Category> categoryList) throws IOException {
        String pathToExportFile = getArticleCategoryExportFilePath(userId, "categories");
        export(convertListOfCategories(categoryList), pathToExportFile, false);
    }

    public void exportArticles(long userId, List<Article> articleList) throws IOException {
        String pathToExportFile = getArticleCategoryExportFilePath(userId, "articles");
        export(convertListOfArticles(articleList), pathToExportFile, false);
    }

    private String getArticleCategoryExportFilePath(long userId, String name) {
        String dateToday = getDateToday();
        final String pathToArticleCategoryExport =
                exportPath + pathSeparator + userId + pathSeparator + "categories_and_articles";
        final String exportFileName = name + "_" + dateToday + ".csv";
        return pathToArticleCategoryExport + pathSeparator + exportFileName;
    }

    private void createExportFolders(long userId) throws IOException {
        if (!folderExists(exportPath)) {
            createFolder(exportPath);
        }
        final String pathToIdFolder = exportPath + pathSeparator + userId;
        if (!folderExists(pathToIdFolder)) {
            createFolder(pathToIdFolder);
        }
        final String pathToArticleCategoryExport = pathToIdFolder + pathSeparator + "categories_and_articles";
        if (!folderExists(pathToArticleCategoryExport)) {
            createFolder(pathToArticleCategoryExport);
        }
    }

    /**
     * Exports the given list into the given csv file
     * @param objectsList A list of String objects - every string represents one line in the csv file
     * @param filePath    The relative path to the csv file
     * @param append      Indicates if given content should be overwritten or if the new content will be appended
     */
    private void export(List<String> objectsList, String filePath, boolean append) throws IOException {
        logger.info("Trying to export to \"" + filePath + "\".");
        StandardOpenOption[] standardOpenOptions;
        if (append) {
            standardOpenOptions = new StandardOpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE};
        } else {
            standardOpenOptions = new StandardOpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.WRITE};
        }
        Files.write(Paths.get(filePath), objectsList, StandardCharsets.UTF_8, standardOpenOptions);
        logger.info("Export successfully completed!");
    }

    public void export(List<ExpenseFromFrontend> expenseList, List<Article> articleList, List<Category> categoryList)
        throws IOException {
//        if (!expenseList.isEmpty()) {
//            if (!folderExists(exportPath)) {
//                createFolder(exportPath);
//            }
//            long userId = expenseList.get(0).userId;
//            final String pathToIdFolder = exportPath + pathSeparator + userId;
//            if (!folderExists(pathToIdFolder)) {
//                createFolder(pathToIdFolder);
//            }
//            logger.info("The folder exists and the expenses will be exported now:");
//            final String expenseFileName = "expenses.csv";
//            export(convertListOfExpenses(expenseList),  pathToIdFolder + pathSeparator + expenseFileName, true);


//            String exportPathArticlesAndCategories = pathToIdFolder + pathSeparator + "categories_and_articles";
//            if (!folderExists(exportPathArticlesAndCategories)) {
//                createFolder(exportPathArticlesAndCategories);
//            }
//            logger.info("The exportPath for the categories and the articles is: " + exportPathArticlesAndCategories);

//            String dateToday = getDateToday();

//            export(convertListOfArticles(articleList),
//                    exportPathArticlesAndCategories + pathSeparator + "articles_" + dateToday + ".csv", false);
//            export(convertListOfCategories(categoryList),
//                    exportPathArticlesAndCategories + pathSeparator + "categories_" + dateToday + ".csv", false);
//        }
    }

    private boolean folderExists(String pathToFolder) {
        logger.info("Check if the folder '" + pathToFolder + "' exists.");
        File file = new File(pathToFolder);
        return file.exists();
    }

    private void createFolder(String pathToFolder) throws IOException {
        logger.info("The folder doesn't exist and is going to be created now");
        File file = new File(pathToFolder);
        boolean successfullyCreated = file.mkdir();
        if(!successfullyCreated){
            throw new IOException("The folder " + pathToFolder + " couldn't be created");
        }
    }

    private String getDateToday() {
        String dateToday = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        final boolean articleFileAlreadyExists = Files.exists(Paths.get("articles_" + dateToday + ".csv"));
        final boolean categoryFileAlreadyExists = Files.exists(Paths.get("categories_" + dateToday + ".csv"));

        if (articleFileAlreadyExists || categoryFileAlreadyExists) {
            dateToday = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_hh:mm:ss"));
        }
        return dateToday;
    }


    private List<String> convertListOfExpenses(List<ExpenseFromFrontend> expenseList) {
        List<String> listOfStrings = new ArrayList<>();
        expenseList.forEach(expense -> listOfStrings.add(convertExpense(expense)));
        return listOfStrings;
    }

    private String convertExpense(ExpenseFromFrontend expense) {
        return expense.date + ";" + expense.category + ";" + expense.article + ";" + expense.amount + ";" + expense.price;
    }

    private List<String> convertListOfArticles(List<Article> articleList) {
        List<String> listOfStrings = new ArrayList<>();
        articleList.forEach(article -> listOfStrings.add(convertArticle(article)));
        return listOfStrings;
    }

    private String convertArticle(Article article) {
        return article.getName() + ";" + article.getCategory().getName() + ";" + article.getDefaultAmount() + ";"
            + article.getDefaultPrice();
    }

    private List<String> convertListOfCategories(List<Category> categoryList) {
        List<String> listOfStrings = new ArrayList<>();
        categoryList.forEach(category -> listOfStrings.add(convertCategory(category)));
        return listOfStrings;
    }

    private String convertCategory(Category category) {
        return category.getName() + ";" + category.getBudget() + ";" + category.getCurrentBudget();
    }
}
