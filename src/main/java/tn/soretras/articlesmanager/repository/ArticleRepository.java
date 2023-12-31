package tn.soretras.articlesmanager.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tn.soretras.articlesmanager.domain.Article;

/**
 * Spring Data MongoDB repository for the Article entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ArticleRepository extends MongoRepository<Article, String> {}
