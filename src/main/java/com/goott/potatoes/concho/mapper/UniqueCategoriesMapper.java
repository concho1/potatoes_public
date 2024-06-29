package com.goott.potatoes.concho.mapper;

import com.goott.potatoes.concho.model.UniqueCategories;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface UniqueCategoriesMapper {
    List<UniqueCategories> getCategories();
    List<UniqueCategories> updateCate(String catemajor);
}
