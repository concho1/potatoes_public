package com.goott.potatoes.concho.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchSeoul {
    // for 검색
    private String searchStr;
    private String searchOption;
    private Integer searchCnt;
    private Integer dayOption;
    // for 페이지 표시
    private Integer limit;
    private Integer nowGoPageNum;
}
