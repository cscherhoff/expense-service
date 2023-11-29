package com.exxeta.expenseservice.repositories;

import com.exxeta.expenseservice.entities.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {
    Article findArticleByName(String name);
    Optional<Article> findArticleByUserIdAndName(String userId, String name);
    List<Article> findAllByUserId(String userId);

}
