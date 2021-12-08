package com.exxeta.expenseservice.dtos;

import java.util.Objects;

public class ArticleDto {
    public String articleName;
    public double defaultAmount;
    public double defaultPrice;

    public ArticleDto(String articleName, double defaultAmount, double defaultPrice) {
        this.articleName = articleName;
        this.defaultAmount = defaultAmount;
        this.defaultPrice = defaultPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ArticleDto that = (ArticleDto) o;
        return Double.compare(that.defaultAmount, defaultAmount) == 0 &&
            Double.compare(that.defaultPrice, defaultPrice) == 0 &&
            Objects.equals(articleName, that.articleName);
    }
}
