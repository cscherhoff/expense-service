package com.exxeta.expenseservice.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long userId;

    @OneToMany(cascade = CascadeType.REMOVE,
            fetch = FetchType.EAGER,
            mappedBy = "category")
    private final Collection<Article> articles = new ArrayList<>();

    private String name;

    private BigDecimal budget = BigDecimal.ZERO;

    private BigDecimal currentBudget = BigDecimal.ZERO;

    public Category() {
    }

    public Category(long userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public Category(long userId, String name, BigDecimal budget, BigDecimal currentBudget) {
        this.userId = userId;
        this.name = name;
        this.budget = budget;
        this.currentBudget = currentBudget;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public BigDecimal getCurrentBudget() {
        return currentBudget;
    }

    public Collection<Article> getArticles() {
        return articles;
    }

    public void setCurrentBudget(BigDecimal currentBudget) {
        this.currentBudget = currentBudget;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return userId == category.userId &&
                Objects.equals(name, category.name) &&
                Objects.equals(budget, category.budget) &&
                Objects.equals(currentBudget, category.currentBudget);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
