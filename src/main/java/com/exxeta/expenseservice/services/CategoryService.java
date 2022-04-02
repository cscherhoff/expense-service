package com.exxeta.expenseservice.services;

import com.exxeta.expenseservice.dtos.ArticleDto;
import com.exxeta.expenseservice.dtos.CategoryDto;
import com.exxeta.expenseservice.entities.Category;
import com.exxeta.expenseservice.repositories.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@org.springframework.stereotype.Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void saveCategoryList(List<Category> categoryList) {
        categoryRepository.saveAll(categoryList);
        logger.info("Successfully saved " + categoryList.size() + " categories to the database");
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> getAllCategories(long userId) {return categoryRepository.findAllByUserId(userId);}

    public List<CategoryDto> getAllCategoryDtos(long userId) {
        List<CategoryDto> categoryDtoList = new ArrayList<>();
        for (Category category: categoryRepository.findAllByUserId(userId)) {
            CategoryDto categoryDto = createCategoryDto(category);
            categoryDtoList.add(categoryDto);
        }
        return categoryDtoList;
    }

    public void updateBudgetForCategories(Collection<Category> categories) {
        for (Category category: categories) {
            final Category categoryFromDb = categoryRepository.findCategoryByName(category.getName());
            if (categoryFromDb == null) {
                categoryRepository.save(category);
            } else {
                categoryFromDb.setBudget(category.getBudget());
                categoryRepository.save(categoryFromDb);
            }
        }
    }

    private CategoryDto createCategoryDto(Category category) {
        List<ArticleDto> articleDtoList = createArticleDtoList(category);
        return new CategoryDto(category.getName(), articleDtoList, category.getBudget(),
            category.getCurrentBudget());
    }

    private List<ArticleDto> createArticleDtoList(Category category) {
        List<ArticleDto> articleDtoList = new ArrayList<>();
        category.getArticles().forEach(article -> articleDtoList.add(
            new ArticleDto(article.getName(), article.getDefaultAmount(), article.getDefaultPrice()))
        );
        return articleDtoList;
    }
}
