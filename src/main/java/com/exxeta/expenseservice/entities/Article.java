package com.exxeta.expenseservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Entity
public class Article implements Comparable<Article> {
//public class Article implements Comparable<Article> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long ID;

    private String userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category")
    @JsonIgnore
    private Category category;

    @Column
    private String  name;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.REMOVE,
            fetch = FetchType.EAGER,
            mappedBy = "article")
    private final Collection<Expense> expenses = new ArrayList<>();

    @Column
    private double defaultAmount;

    @Column
    private double defaultPrice;

    public Article() {
    }

    public Article(String userId, Category category, String name) {
        this.userId = userId;
        this.category = category;
        this.name = name;
    }

    public Article(String userId, Category category, String name, double defaultAmount, double defaultPrice) {
        this.userId = userId;
        this.category = category;
        this.name = name;
        this.defaultAmount = defaultAmount;
        this.defaultPrice = defaultPrice;
    }

    public Category getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public void setDefaultAmount(Double defaultAmount) {
        this.defaultAmount = defaultAmount;
    }

    public void setDefaultPrice(Double defaultPrice) {
        this.defaultPrice = defaultPrice;
    }

    public double getDefaultAmount() {
        return defaultAmount;
    }

    public double getDefaultPrice() {
        return defaultPrice;
    }

    public Collection<Expense> getExpenses() {
        return expenses;
    }

    @Override
    public String toString() {
        return name;
    }

//    @Override
//    public int compareTo(Article article) {
//        return this.name.compareTo(article.name);
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Article article = (Article) o;
        return Double.compare(article.getDefaultAmount(), getDefaultAmount()) == 0 &&
            Double.compare(article.getDefaultPrice(), getDefaultPrice()) == 0 &&
            Objects.equals(getCategory(), article.getCategory()) &&
            Objects.equals(getName(), article.getName());
    }

    @Override
    public int compareTo(Article article) {
        return this.name.compareTo(article.name);
    }
}
