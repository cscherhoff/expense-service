package com.exxeta.expenseservice.services;

import com.exxeta.expenseservice.dtos.ArticleDto;
import com.exxeta.expenseservice.dtos.CategoryDto;
import com.exxeta.expenseservice.entities.Article;
import com.exxeta.expenseservice.entities.Category;
import com.exxeta.expenseservice.repositories.ArticleRepository;
import com.exxeta.expenseservice.repositories.CategoryRepository;
import com.exxeta.expenseservice.repositories.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.*;

@TestInstance(Lifecycle.PER_METHOD)
public class TestService {

    private final ExpenseRepository expenseRepository = mock(ExpenseRepository.class);

    private final CategoryRepository categoryRepository = mock(CategoryRepository.class);

    private final ArticleRepository articleRepository = mock(ArticleRepository.class);

//    private final KafkaTemplate kafkaTemplate = mock(KafkaTemplate.class);

    private final CategoryService categoryService = new CategoryService(categoryRepository);

    private final String userId = "5";


    @Test
    public void testGetAllCategoryDtos() {
        List<Category> categoryList = makeCategoryList();
        when(categoryRepository.findAllByUserId(userId)).thenReturn(categoryList);

        List<CategoryDto> categoryDtoList = categoryService.getAllCategoryDtos(userId);

        List<CategoryDto> expected = createExpectedResult();
        assertIterableEquals(categoryDtoList, expected, "The lists aren't the same!");

        verify(categoryRepository,times(1)).findAllByUserId(userId);
        verifyNoInteractions(expenseRepository, articleRepository);
        verifyNoMoreInteractions(categoryRepository);
    }

    private List<CategoryDto> createExpectedResult() {
        CategoryDto categoryDtoHaushalt = new CategoryDto("Haushalt", createArticleDtoList(),
            BigDecimal.ZERO, BigDecimal.ZERO);
        CategoryDto categoryDtoLebensmittel = new CategoryDto("Lebensmittel", createArticleDtoList(),
            BigDecimal.ZERO, BigDecimal.ZERO);
        return List.of(
            categoryDtoHaushalt,
            categoryDtoLebensmittel
        );
    }

    private List<ArticleDto> createArticleDtoList() {
        List<ArticleDto> articleDtoList = new ArrayList<>();
        for (int i = 0; i<3; i++) {
            articleDtoList.add(new ArticleDto("TestArtikel"+i, 0, 0));
        }
        return articleDtoList;
    }

    private List<Category> makeCategoryList() {
        Category categoryHaushalt = new Category(userId, "Haushalt");
        Category categoryLebensmittel = new Category(userId, "Lebensmittel");
        addArticles(categoryHaushalt);
        addArticles(categoryLebensmittel);
        return List.of(
            categoryHaushalt,
            categoryLebensmittel
        );
    }

    private void addArticles(Category category) {
        List<Article> articleList = new ArrayList<>();
        for (int i = 0; i<3; i++) {
            articleList.add(new Article(userId, category, "TestArtikel" + i));
        }
        category.getArticles().addAll(articleList);
    }

    @Test
    public void testSaveCategoryList() {
        List<Category> categoryList = List.of(
        new Category(userId, "TestCategory1"),
            new Category(userId, "TestCategory2")
        );
        when(categoryRepository.saveAll(categoryList)).thenReturn(null);

        categoryService.saveCategoryList(categoryList);

        verify(categoryRepository, times(1)).saveAll(categoryList);
        verifyNoMoreInteractions(categoryRepository);
        verifyNoInteractions(expenseRepository, articleRepository);
    }

    @Test
    public void testGetAllCategories() {
        List<Category> categoryList = List.of(
            new Category(userId, "TestCategory1"),
            new Category(userId, "TestCategory2")
        );
        when(categoryRepository.findAll()).thenReturn(categoryList);

        List<Category> actual = categoryService.getAllCategories();

        assertIterableEquals(categoryList, actual, "The lists are not the same");

        verify(categoryRepository, times(1)).findAll();
        verifyNoMoreInteractions(categoryRepository);
        verifyNoInteractions(expenseRepository, articleRepository);
    }
}
