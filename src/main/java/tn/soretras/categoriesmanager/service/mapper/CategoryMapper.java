package tn.soretras.categoriesmanager.service.mapper;

import org.mapstruct.*;
import tn.soretras.categoriesmanager.domain.Category;
import tn.soretras.categoriesmanager.service.dto.CategoryDTO;

/**
 * Mapper for the entity {@link Category} and its DTO {@link CategoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper extends EntityMapper<CategoryDTO, Category> {}
