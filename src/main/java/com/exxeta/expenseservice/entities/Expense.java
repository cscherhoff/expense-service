package com.exxeta.expenseservice.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long expenseId;

    private String userId;

    @Column
    private LocalDate date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "article")
    private Article article;

    @Column
    private BigDecimal amount;

    @Column
    private BigDecimal price;

    public Expense(String userId, LocalDate date, Article article, BigDecimal amount, BigDecimal price) {
        this.userId = userId;
        this.date = date;
        this.article = article;
        this.amount = amount;
        this.price = price;
    }

    public Expense() {
    }

    public String getUserId() {
        return userId;
    }

    public LocalDate getDate() {
        return date;
    }

    public Article getArticle() {
        return article;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Expense expense = (Expense) o;
        return getUserId() == expense.getUserId() &&
            Objects.equals(getDate(), expense.getDate()) &&
            Objects.equals(getArticle(), expense.getArticle()) &&
            Objects.equals(getAmount(), expense.getAmount()) &&
            Objects.equals(getPrice(), expense.getPrice());
    }
}
