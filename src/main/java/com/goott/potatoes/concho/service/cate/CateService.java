package com.goott.potatoes.concho.service.cate;

import com.goott.potatoes.concho.mapper.UniqueCategoriesMapper;
import com.goott.potatoes.concho.model.UniqueCategories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class CateService {
    @Autowired
    private UniqueCategoriesMapper categoriesMapper;
    // 카테고리 map List 로 넘겨주기
    public HashMap<String, ArrayList<String>> getCateListMap(){
        List<UniqueCategories> categorieList = categoriesMapper.getCategories();
        var cateMapList = new HashMap<String, ArrayList<String>>();
        
        for(UniqueCategories cate : categorieList){
            String major = cate.getMajorCategory();
            String minor = cate.getMinorCategory();

            if(cateMapList.containsKey(major)){
                cateMapList.get(major).add(minor);
            }else{
                var minorList = new ArrayList<String>();
                minorList.add(minor);
                cateMapList.put(major, minorList);
            }
        }
        return cateMapList;
    }

    public List<UniqueCategories> updateCate(String catemajor){return this.categoriesMapper.updateCate(catemajor);}
}
