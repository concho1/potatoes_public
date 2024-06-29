package com.goott.potatoes.concho.service.schedule;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

// String 타입의 xml 을 직접 Seoul 객체로 바꿔보자
@Service
public class XmlToMapListService {
    public ArrayList<HashMap<String, String>> xmlStrToStringList(String xmlStr, String[] columnNames){
        var columnSet = new HashSet<>(Arrays.asList(columnNames));
        var mapList = new ArrayList<HashMap<String, String>>();
        int colSize = columnSet.size();
        try {
            var columnMap = new HashMap<String, String>();
            char[] xmlCharArr = xmlStr.toCharArray();
            StringBuilder tagBuilder = new StringBuilder();
            StringBuilder valueBuilder = new StringBuilder();
            boolean isTagCh = false;
            int cnt = 0;
            String tag = "";
            String value = "";
            for(char ch : xmlCharArr){
                if(ch == '<'){
                    isTagCh = true;
                    value = valueBuilder.toString();
                    if(columnSet.contains(tag)){
                        cnt++;
                        columnMap.put(tag, value);
                        if(cnt >= colSize){
                            mapList.add(columnMap);
                            columnMap = new HashMap<>();
                            cnt = 0;
                        }
                    }
                    valueBuilder.setLength(0);
                } else if (ch == '>') {
                    isTagCh = false;
                    if(!tagBuilder.toString().isEmpty()){
                        tag = tagBuilder.toString();
                    }
                    tagBuilder.setLength(0);
                }else if(isTagCh){
                    tagBuilder.append(ch);
                }else{
                    valueBuilder.append(ch);
                }
            }

            return mapList;

        }catch (Exception e){
            e.printStackTrace();
        }
        return mapList;
    }
}
