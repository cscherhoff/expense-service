package com.exxeta.expenseservice.dtos;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class CategoryDto {
    public String categoryName;
    public List<ArticleDto> articles;
    public BigDecimal budget;
    public BigDecimal currentBudget;

    public CategoryDto(String categoryName, List<ArticleDto> articles, BigDecimal budget, BigDecimal currentBudget) {
        this.categoryName = categoryName;
        this.articles = articles;
        this.budget = budget;
        this.currentBudget = currentBudget;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CategoryDto that = (CategoryDto) o;
        return Objects.equals(categoryName, that.categoryName) &&
            Objects.equals(articles, that.articles) &&
            Objects.equals(budget, that.budget) &&
            Objects.equals(currentBudget, that.currentBudget);
    }
}
