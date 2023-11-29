package com.exxeta.expenseservice.dtos;

import com.exxeta.expenseservice.util.LocalDateSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDate;
import java.util.Objects;

public class ExpenseToFrontend {

    @JsonIgnore
    public String userId;
    @JsonSerialize(using = LocalDateSerializer.class)
//    @JsonDeserialize(using = LocalDateDeserializer.class)
    public LocalDate date;
    public String article;
    public String category;
    public double amount;
    public double price;

    public ExpenseToFrontend(LocalDate date, String article, String category, double amount, double price) {
        this.date = date;
        this.article = article;
        this.category = category;
        this.amount = amount;
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExpenseToFrontend that = (ExpenseToFrontend) o;
        return Double.compare(that.amount, amount) == 0 &&
            Double.compare(that.price, price) == 0 &&
            Objects.equals(date, that.date) &&
            Objects.equals(article, that.article) &&
            Objects.equals(category, that.category);
    }
}
