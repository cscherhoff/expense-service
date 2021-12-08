package com.exxeta.expenseservice.dtos;

import com.exxeta.expenseservice.util.LocalDateSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDate;
import java.util.Objects;

public class ExpenseFromFrontend {

    @JsonIgnore
    public long userId;
    @JsonSerialize(using = LocalDateSerializer.class)
//    @JsonDeserialize(using = LocalDateDeserializer.class)
    public LocalDate date;
    public String article;
    public String category;
    public double amount;
    public double price;
    public boolean overrideDefaults;

    public ExpenseFromFrontend(long userId, LocalDate date, String article, String category, double amount, double price,
        boolean overrideDefaults) {
        this.userId = userId;
        this.date = date;
        this.article = article;
        this.category = category;
        this.amount = amount;
        this.price = price;
        this.overrideDefaults = overrideDefaults;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExpenseFromFrontend that = (ExpenseFromFrontend) o;
        return userId == that.userId &&
            Double.compare(that.amount, amount) == 0 &&
            Double.compare(that.price, price) == 0 &&
            overrideDefaults == that.overrideDefaults &&
            Objects.equals(date, that.date) &&
            Objects.equals(article, that.article) &&
            Objects.equals(category, that.category);
    }

}
