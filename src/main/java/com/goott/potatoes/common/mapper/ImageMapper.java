package com.goott.potatoes.common.mapper;

import com.goott.potatoes.common.model.Image;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ImageMapper {
    int insertImage(Image image);
    Image selectImageByKey(@Param("imgKey") String imgKey);
    int updateImage(Image image);
    int deleteImage(@Param("imgKey") String imgKey);
}
