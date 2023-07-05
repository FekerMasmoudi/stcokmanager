package tn.soretras.articlesmanager.service.mapper;

import org.mapstruct.*;
import tn.soretras.articlesmanager.domain.Article;
import tn.soretras.articlesmanager.service.dto.ArticleDTO;

/**
 * Mapper for the entity {@link Article} and its DTO {@link ArticleDTO}.
 */
@Mapper(componentModel = "spring")
public interface ArticleMapper extends EntityMapper<ArticleDTO, Article> {}
