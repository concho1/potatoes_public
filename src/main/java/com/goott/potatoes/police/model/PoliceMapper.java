package com.goott.potatoes.police.model;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PoliceMapper {

    public List<Police> list(PolicePage pdto);
    public int add(List<Police> dList);
    public int update_police(Police dto);
    public int add_sec(List<Section> sList);
    public String findAddr(String tel);
    public int countList();
}
